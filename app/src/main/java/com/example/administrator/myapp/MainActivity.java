package com.example.administrator.myapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.myapp.audio.AudioRecordButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mItems = new ArrayList<>();
    private AudioRecordButton audioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioButton = (AudioRecordButton) findViewById(R.id.buttonPanel);
        audioButton.setCanRecord(true);
        audioButton.setTalkerId("1231");
        audioButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, final String filePath) {
                final String dest = new File(getExternalCacheDir(), "encode.amr").getAbsolutePath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Jni.encode(filePath, dest);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(MainActivity.this, dest, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
        new LoadTask(getApplication()).execute();
    }

    public void onClick(View view) {
        String s = mItems.get(0);
        String dest = Environment.getExternalStorageDirectory() + "/decode.mp3";
        Jni.decode(s, dest);
        Toast.makeText(this, dest, Toast.LENGTH_SHORT).show();
    }


    private class LoadTask extends AsyncTask<Void, Void, List<String>> {

        private Context mContext;

        public LoadTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> paths = PathUtils.getVoiceFiles_WeChat(mContext);
            ArrayList<String> voicePaths = new ArrayList<>();

            if (paths != null && paths.size() > 0) {
                File file;
                for (String path : paths) {
                    if (path != null) {
                        file = new File(path);
                        if (file != null && file.exists() && file.isDirectory()) {
                            Stack<String> stack = new Stack<>();
                            stack.push(path);
                            while (!stack.empty()) {
                                File[] fs = null;
                                String parent = stack.pop();
                                if (parent != null) {
                                    file = new File(parent);
                                    if (file.isDirectory()) { // ignore file, FIXME
                                        fs = file.listFiles();
                                    } else {
                                        continue;
                                    }
                                }
                                if (fs == null || fs.length == 0) continue;
                                for (int i = 0; i < fs.length; ++i) {
                                    final String name = fs[i].getName();
                                    if (fs[i].isDirectory() && !name.equals(".")
                                            && !name.equals("..")) {
                                        stack.push(fs[i].getPath());
                                    } else if (fs[i].isFile()) {
                                        if (name.endsWith(".amr")) {
                                            voicePaths.add(fs[i].getAbsolutePath());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            return voicePaths;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mItems.clear();
            mItems.addAll(strings);

        }
    }
}

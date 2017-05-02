package com.example.administrator.myapp.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.MediaSyncEvent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AudioMana {

    private final int sampleRateInHz = 24000;
//    private MediaRecorder mRecorder;
    //文件夹位置
    private String mDirString;
    //录音文件保存路径
    private String mCurrentFilePathString;
    //是否真备好开始录音
    private boolean isPrepared;

    private String mFilePath;
    private AudioRecord mAudioRecord;
    private int minBuffer;
    private FileOutputStream outputStream;
    private int mAmplitude;

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    /**
     * 单例化这个类
     */
    private static AudioMana mInstance;

    private AudioMana(String dir) {
        mDirString = dir;
    }

    public static AudioMana getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioMana.class) {
                if (mInstance == null) {
                    mInstance = new AudioMana(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 回调函数，准备完毕，准备好后，button才会开始显示录音框
     *
     * @author nickming
     */
    public interface AudioStageListener {
        void wellPrepared();
    }

    public AudioStageListener mListener;

    public void setOnAudioStageListener(AudioStageListener listener) {
        mListener = listener;
    }

    // 准备方法
    public void prepareAudio() {
        try {
            // 一开始应该是false的
            isPrepared = false;
            //创建所属文件夹
            File file;
            if (TextUtils.isEmpty(mFilePath)) {
                File dir = new File(mDirString);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileNameString = generalFileName();
                file = new File(dir, fileNameString);
            } else {
                file = new File(mFilePath);
            }

            try {
                file = new File(mDirString, "test.pcm");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            //获取文件
            mCurrentFilePathString = file.getAbsolutePath();

//            mRecorder = new MediaRecorder();
//            // 设置输出文件
//            mRecorder.setOutputFile(file.getAbsolutePath());
//            // 设置meidaRecorder的音频源是麦克风
//            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            // 设置文件音频的输出格式为amr
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//            // 设置音频的编码格式为amr。这里采用AAC主要为了适配IOS，保证在IOS上可以正常播放。
//            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//            // 严格遵守google官方api给出的mediaRecorder的状态流程图
//            mRecorder.prepare();
//
//            mRecorder.start();

            minBuffer = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            mAudioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, sampleRateInHz,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, sampleRateInHz * 2);

            // 准备结束
            isPrepared = true;
            // 已经准备好了，可以录制了
            if (mListener != null) {
                mListener.wellPrepared();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startRecording();
                }
            }).start();


        } catch (IllegalStateException e) {
            e.printStackTrace();
        }/* catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private void startRecording() {
        if (null == mAudioRecord) {
            return;
        }

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        byte[] buffer = new byte[sampleRateInHz * 2 * 5];

        mAudioRecord.startRecording();

        int bytesRead = 0;

        while (isPrepared) {

            bytesRead = mAudioRecord.read(buffer, 0, minBuffer);

                cAmplitude = 0;
            if (bytesRead > 0) {
                for (int i=0; i < bytesRead / 2; i++) {
                    short curSample = getShort(buffer[i*2], buffer[i*2+1]);
                    if (curSample > cAmplitude) {
                        cAmplitude = curSample;
                    }
                }
                try {
                    outputStream.write(buffer, 0, bytesRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private int cAmplitude= 0;

    private short getShort(byte argB1, byte argB2) {
        return (short)(argB1 | (argB2 << 8));
    }

    /**
     * 随机生成文件的名称
     *
     * @return
     */
    private String generalFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    // 获得声音的level
    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                // 取证+1，否则去不到7
                Log.d("AudioMana", "-----------cAmplitude:" + cAmplitude);
                if (cAmplitude < 4000) {
                    return 1;
                } else {
                    return maxLevel * cAmplitude / 12000 + 1;
                }
            } catch (Exception e) {

            }
        }

        return 1;
    }

    // 释放资源
    public void release() {
        // 严格按照api流程进行
//        if (mRecorder == null) return;
        try {
            if (isPrepared) {
                isPrepared = false;
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
//                mRecorder.stop();
//                mRecorder.release();
//                mRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 取消,因为prepare时产生了一个文件，所以cancel方法应该要删除这个文件，
    // 这是与release的方法的区别
    public void cancel() {
        release();
        if (mCurrentFilePathString != null) {
            File file = new File(mCurrentFilePathString);
            file.delete();
            mCurrentFilePathString = null;
        }

    }

    public String getCurrentFilePath() {
        return mCurrentFilePathString;
    }

}

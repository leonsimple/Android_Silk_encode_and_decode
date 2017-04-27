package com.example.administrator.myapp;

import android.app.Application;

/**
 * Created by ketian.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PathUtils.init(getApplicationContext());
    }
}

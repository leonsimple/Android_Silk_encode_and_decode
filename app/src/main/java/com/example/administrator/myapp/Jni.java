package com.example.administrator.myapp;

/**
 * Created by Administrator on 2017/3/31.
 */

public class Jni {
    static {
        System.loadLibrary("silk");
    }

    public static native int decode(String org,String dest);

    public static native int encode(String org,String dest);
}

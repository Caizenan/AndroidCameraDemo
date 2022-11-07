package com.example.cameralib;

import android.util.Log;

public class SmlLogger {
    public static void SmlLog_d(String log){
        if(BuildConfig.DEBUG){
            Log.d("SML", log);
        }
    }
}

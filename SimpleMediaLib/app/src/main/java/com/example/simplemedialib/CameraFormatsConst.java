package com.example.simplemedialib;

import android.graphics.ImageFormat;

public class CameraFormatsConst {
    public static int getCameraNV21BufferSize(int width,int height){
        return (width * height * ImageFormat.NV21)/8;
    }
}

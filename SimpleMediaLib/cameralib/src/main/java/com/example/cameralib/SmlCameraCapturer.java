package com.example.cameralib;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SmlCameraCapturer {
    private HandlerThread cameraThread;
    private Handler cameraEventHandler;
    private SmlCameraBuilder cameraBuilder;
    private List<ISmlCameraObserver> observers = new ArrayList<>();

    public void registerCamerObserver(ISmlCameraObserver observer){
        if(!observers.contains(observer)){
            observers.add(observer);
        }
    }

    public void prepare(){
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraEventHandler = new Handler(cameraThread.getLooper());
        cameraBuilder = new SmlCameraBuilder()
                .setExpectFps(30)
                .setOrientation(0)
                .setPreViewSize(1980,720)
                .setPreViewFormat(ImageFormat.NV21)
                .setBuffers(3)
                .setPreViewTexture(42);

        SmlLogger.SmlLog_d("开始预览");
        //Log.d("czn", "线程1: "+Thread.currentThread());
        cameraEventHandler.post(new Runnable() {
            @Override
            public void run() {
                cameraBuilder.getSmlCamera().openCamera();
                cameraBuilder.getSmlCamera().startPreViewWithBuffer(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        for(ISmlCameraObserver it : observers){
                            it.onObserved(data,cameraBuilder.getSmlCamera().preViewWidth,cameraBuilder.getSmlCamera().preViewHeight);
                        }
                        //Log.d("czn", "线程2: "+Thread.currentThread());
                        cameraBuilder.getSmlCamera().addBuffers(data);
                    }
                });
            }
        });
    }
}

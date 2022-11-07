package com.example.cameralib;

import android.view.Surface;

public class SmlCameraPreviewController implements ISmlCameraObserver {
    static{
        System.loadLibrary("CameraRender");
    }

    private SmlCameraCapturer mCapturer;
    private SmlVideoEncoder mEncoder;

    public SmlCameraPreviewController(){
    }

    public void prepareRending(Surface surface){
        if(surface != null){
            if(!cameraRenderInit(surface,0,0,1080,1920)){
                SmlLogger.SmlLog_d("egl环境创建失败");
            }
        }
    }

    public void setUpCameraRenderController(){
        mCapturer = new SmlCameraCapturer();
        mEncoder = new SmlVideoEncoder();
        mCapturer.registerCamerObserver(this);
        mCapturer.registerCamerObserver(mEncoder);
        mCapturer.prepare();
        SmlLogger.SmlLog_d("启动相机");
    }

    @Override
    public void onObserved(byte[] data,int width,int height) {
        SmlLogger.SmlLog_d("获取一帧数据");
        requestCameraRender(data,data.length,width ,height);

    }

    public native boolean cameraRenderInit(Object surface,int left, int top, int width, int height);

    public native void cameraRenderResetSize(int left, int top, int width, int height);

    public native void cameraRenderStop();

    public native void requestCameraRender(byte[] data, long len, long preWidth, long preHeight);
}

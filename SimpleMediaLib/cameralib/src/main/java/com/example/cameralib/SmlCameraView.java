package com.example.cameralib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;


public class SmlCameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SmlCameraPreviewController mRenderController;

    public SmlCameraView(Context context) {
        super(context);
        init();
    }

    public SmlCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmlCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        SmlLogger.SmlLog_d("启动渲染");
        mRenderController = new SmlCameraPreviewController();
        mRenderController.prepareRending(holder.getSurface());
        mRenderController.setUpCameraRenderController();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}

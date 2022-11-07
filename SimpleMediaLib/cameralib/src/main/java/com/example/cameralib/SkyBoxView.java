package com.example.cameralib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class SkyBoxView extends SurfaceView implements SurfaceHolder.Callback{

    private SkyBoxController controller;

    public SkyBoxView(Context context) {
        super(context);
        init();
    }

    public SkyBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SkyBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        controller = new SkyBoxController();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        controller.skyBoxInit(holder.getSurface());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        controller.skyBoxRenderResetSize(0,0,width,height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        controller.skyBoxRelease();
    }

}

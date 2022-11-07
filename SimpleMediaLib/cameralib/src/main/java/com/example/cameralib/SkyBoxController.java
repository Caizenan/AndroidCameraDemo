package com.example.cameralib;

class SkyBoxController {
    static {
        System.loadLibrary("SkyBox");
    }

    public native boolean skyBoxInit(Object surface);

    public native void skyBoxRenderResetSize(int left, int top, int width, int height);

    public native void skyBoxRelease();
}

package com.example.cameralib;

public interface ISmlCameraObserver {
    public void onObserved(byte[] data,int width,int height);
}

package com.example.cameralib;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SmlCameraBuilder {

    private SmlCamera smlCamera;

    public SmlCameraBuilder(){
        smlCamera = new SmlCamera();
    }

    public SmlCameraBuilder setPreViewSize(int width,int height){
        smlCamera.preViewWidth  = width;
        smlCamera.preViewHeight  = height;
        return this;
    }

    public SmlCameraBuilder setPicSize(int width,int height){
        smlCamera.picWidth  = width;
        smlCamera.picHeight  = height;
        return this;
    }

    public SmlCameraBuilder setExpectFps(int fps){
        smlCamera.preViewFps  = fps;
        return this;
    }

    public SmlCameraBuilder setCameraId(int id){
        smlCamera.cameraID  = id;
        return this;
    }

    public SmlCameraBuilder setOrientation(int orientation){
        smlCamera.orientation  = orientation;
        return this;
    }

    public SmlCameraBuilder setPreViewTexture(int texId){
        smlCamera.surfaceTexture = new SurfaceTexture(texId);
        return this;
    }

    public SmlCameraBuilder setPreViewFormat(int format){
        smlCamera.preViewFormat = format;
        return this;
    }


    public SmlCameraBuilder setBuffers(int num){
        smlCamera.buffers = num;
        return this;
    }

    public SmlCameraBuilder setFocusMode(String mode){
        smlCamera.focusMode = mode;
        return this;
    }

    public SmlCamera getSmlCamera(){
        return smlCamera;
    }


    public static class SmlCamera {
        // 相机默认宽高，相机的宽度和高度跟屏幕坐标不一样，手机屏幕的宽度和高度是反过来的。
        private String focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        private int buffers = 3;
        public int preViewWidth = 720;
        public int preViewHeight = 1980;
        private int preViewFps = 30;
        private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        private int orientation = 0;
        private int picWidth = preViewWidth;
        private int picHeight = preViewHeight;
        private android.hardware.Camera camera;
        private SurfaceTexture surfaceTexture;
        private int preViewFormat;

        public void startPreViewWithBuffer(Camera.PreviewCallback callback){
            try {
                camera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setPreviewCallbackWithBuffer(callback);
            for(int i=0;i<buffers;i++){
                final ByteBuffer buffer = ByteBuffer.allocateDirect( preViewWidth * preViewHeight * ImageFormat.getBitsPerPixel(preViewFormat) /8);
                camera.addCallbackBuffer(buffer.array());
            }
            camera.startPreview();
        }

        //根据ID打开相机
        public void openCamera() {
            if(camera == null){
                camera = android.hardware.Camera.open(cameraID);
                android.hardware.Camera.Parameters parameters = camera.getParameters();
                preViewFps = chooseFixedPreviewFps(parameters, preViewFps * 1000);
                parameters.setRecordingHint(true);
                parameters.setFocusMode(focusMode);
                camera.setParameters(parameters);
                setPreviewSize(camera, preViewWidth, picWidth);
                setPictureSize(camera, preViewHeight, picHeight);
                camera.setDisplayOrientation(orientation);
            }else{
                throw new RuntimeException("camera have been initialized");
            }
        }

        public void addBuffers(byte[] buffer){
            camera.addCallbackBuffer(buffer);
        }

        //从相机支持宽高中找到最接近期望宽高的宽和高
        private android.hardware.Camera.Size calculatePerfectSize(List<android.hardware.Camera.Size> sizes, int expectWidth,
                                                                  int expectHeight) {
            sortList(sizes); // 根据宽度进行排序
            android.hardware.Camera.Size result = sizes.get(0);
            boolean widthOrHeight = false; // 判断存在宽或高相等的Size
            // 辗转计算宽高最接近的值
            for (android.hardware.Camera.Size size: sizes) {
                // 如果宽高相等，则直接返回
                if (size.width == expectWidth && size.height == expectHeight) {
                    result = size;
                    break;
                }
                // 仅仅是宽度相等，计算高度最接近的size
                if (size.width == expectWidth) {
                    widthOrHeight = true;
                    if (Math.abs(result.height - expectHeight)
                            > Math.abs(size.height - expectHeight)) {
                        result = size;
                    }
                }
                // 高度相等，则计算宽度最接近的Size
                else if (size.height == expectHeight) {
                    widthOrHeight = true;
                    if (Math.abs(result.width - expectWidth)
                            > Math.abs(size.width - expectWidth)) {
                        result = size;
                    }
                }
                // 如果之前的查找不存在宽或高相等的情况，则计算宽度和高度都最接近的期望值的Size
                else if (!widthOrHeight) {
                    if (Math.abs(result.width - expectWidth)
                            > Math.abs(size.width - expectWidth)
                            && Math.abs(result.height - expectHeight)
                            > Math.abs(size.height - expectHeight)) {
                        result = size;
                    }
                }
            }
            return result;
        }

        /**
         * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
         * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
         * 拍摄的照片需要自行处理
         */
        private int calculateCameraPreviewOrientation(int rotation) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraID, info);
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
                default:
                    break;
            }

            int result;
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
            orientation = result;
            return result;
        }

        //设置预览大小
        private void setPreviewSize(android.hardware.Camera camera, int expectWidth, int expectHeight) {
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            android.hardware.Camera.Size size = calculatePerfectSize(parameters.getSupportedPreviewSizes(),
                    expectWidth, expectHeight);
            preViewWidth = size.width;
            preViewHeight = size.height;
            parameters.setPreviewSize(size.width, size.height);
            camera.setParameters(parameters);
        }

        //设置照片大小
        private void setPictureSize(android.hardware.Camera camera, int expectWidth, int expectHeight) {
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            android.hardware.Camera.Size size = calculatePerfectSize(parameters.getSupportedPictureSizes(),
                    expectWidth, expectHeight);
            parameters.setPictureSize(size.width, size.height);
            picWidth = size.width;
            picHeight = size.height;
            camera.setParameters(parameters);
        }

        //排序
        private static void sortList(List<android.hardware.Camera.Size> list) {
            Collections.sort(list, new Comparator<android.hardware.Camera.Size>() {
                @Override
                public int compare(android.hardware.Camera.Size pre, android.hardware.Camera.Size after) {
                    if (pre.width > after.width) {
                        return 1;
                    } else if (pre.width < after.width) {
                        return -1;
                    }
                    return 0;
                }
            });
        }

        //选择合适的FPS
        private int chooseFixedPreviewFps(Camera.Parameters parameters, int expectedThoudandFps)
        {
            //获取当前支持的预览帧率
            List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
            for(int[] entry : supportedFps)
            {
                if(entry[0]==entry[1]&&entry[0] == expectedThoudandFps)
                {
                    parameters.setPreviewFpsRange(entry[0],entry[1]);
                    return entry[0];
                }
            }
            int[] tmp = new int[2];
            int guess;
            //获取当前帧率，看设置后的实际帧率
            parameters.getPreviewFpsRange(tmp);
            if(tmp[0]==tmp[1])
            {
                guess = tmp[0];
            }
            else
            {
                guess = tmp[1]/2;
            }
            return guess;
        }
    }

}

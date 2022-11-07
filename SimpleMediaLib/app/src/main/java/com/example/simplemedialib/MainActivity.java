package com.example.simplemedialib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.cameralib.SkyBoxView;
import com.example.cameralib.SmlCameraView;
import com.example.simplemedialib.databinding.CameraActivityBinding;

public class MainActivity extends Activity {
    private boolean hasPermission = true;
    private SmlCameraView mCameraView;
    private ImageView mCameraBtn;
    private SkyBoxView mSkyBoxView;
    private CameraActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = CameraActivityBinding.inflate(this.getLayoutInflater());
        getCameraPermission();
        mCameraView = mBinding.cameraView;
        mCameraBtn = mBinding.cameraBtn;
        setContentView(mBinding.getRoot());
    }

    private void setClickListener(){
        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
        }else{
            hasPermission = true;
        }
    }

}
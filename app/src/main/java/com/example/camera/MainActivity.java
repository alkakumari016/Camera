package com.example.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.BitSet;

public class MainActivity extends AppCompatActivity {

    private int CAM_PERMISSION=1000;

    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    private FrameLayout cameraPreviewLayout;

    Camera.PictureCallback pictureCallback=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            if(bitmap==null) {
                Toast.makeText(MainActivity.this,"Captured Image is empty",Toast.LENGTH_SHORT).show();
                return;
            }
            ImageView imageView=new ImageView(getApplicationContext());

            imageView.setImageBitmap(bitmap);
            imageView.setRotation(90f);
            ((LinearLayout)findViewById(R.id.imgcontainer)).addView(imageView);
            camera.startPreview();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPreviewLayout=findViewById(R.id.frame);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},CAM_PERMISSION);
            }
        }

        Button captureButton=findViewById(R.id.btn);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null,null,pictureCallback);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera=initDeviceCamera();
        mImageSurfaceView=new ImageSurfaceView(MainActivity.this,camera);
        cameraPreviewLayout.addView(mImageSurfaceView);
    }

    private Camera initDeviceCamera() {
        Camera mCamera=null;
        try {
            mCamera=Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mCamera;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAM_PERMISSION) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"camera permission granted",Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this,"camera permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.perry.smartposter.activity;

import static com.perry.smartposter.util.ImageStorage.FILENAME_FORMAT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.perry.smartposter.model.DataElementManager;
import com.perry.smartposter.R;
import com.perry.smartposter.model.ImageAnalyzer;

import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public FaceDetector faceDetector;
    private LifecycleCameraController controller;
    public DataElementManager manager;
    private ImageAnalyzer analyzer;

    /**
     * 请求相机权限的一个 ActivityResultLauncher
     * 若获得权限则运行 {@link #bindTakePictureButton()} 方法，否则展示提示
     */
    private final ActivityResultLauncher<String> mRequestLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            setupCamera();
                            bindTakePictureButton();
                        } else
                            Toast.makeText(getApplicationContext(), R.string.camera_permission_denied,
                                    Toast.LENGTH_LONG).show();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置透明状态栏和导航栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            mRequestLauncher.launch(Manifest.permission.CAMERA);
        } else {
            Log.d("Perry", "onStart: No Camera Detected.");
        }
        manager = DataElementManager.getInstance(getApplicationContext());

        // 绑定 view_pictures 按钮的点击事件
        FloatingActionButton viewPicturesBtn = findViewById(R.id.view_pictures);
        viewPicturesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PicturesActivity.class);
            startActivity(intent);
        });

        // 初始化 FaceDetector
        FaceDetectorOptions realTimeOpts = new FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build();
        faceDetector = FaceDetection.getClient(realTimeOpts);
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (faceDetector != null) {
                    faceDetector.close(); // 重要：释放资源
                }
                DefaultLifecycleObserver.super.onDestroy(owner);
            }
        });
        analyzer = new ImageAnalyzer(this);
    }

    /// 设置摄像头并绑定生命周期和显示
    private void setupCamera() {
        controller = new LifecycleCameraController(this);
        controller.bindToLifecycle(this);
        controller.setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA);
        PreviewView previewView = findViewById(R.id.preview_view);
        previewView.setController(controller);
    }

    private void bindTakePictureButton() {
        FloatingActionButton btn = findViewById(R.id.take_photo);
        btn.setOnClickListener(v ->
                controller.takePicture(ContextCompat.getMainExecutor(v.getContext()), new ImageCapture.OnImageCapturedCallback() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                                super.onCaptureSuccess(imageProxy);
                                Toast.makeText(getApplicationContext(), R.string.photo_taken, Toast.LENGTH_SHORT).show();
                                analyzer.analyze(imageProxy);
                            }
                        }
                )
        );
    }
}

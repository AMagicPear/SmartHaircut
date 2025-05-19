package com.perry.smartposter.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
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
import com.perry.smartposter.model.DataElement;
import com.perry.smartposter.model.DataElementManager;
import com.perry.smartposter.R;
import com.perry.smartposter.model.ImageAnalyzer;

import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final SimpleDateFormat FILENAME_FORMAT = new SimpleDateFormat("yyMMdd_HHmmss", Locale.CHINA);
    private FaceDetector faceDetector;
    private LifecycleCameraController controller;
    private DataElementManager manager;
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
        analyzer = new ImageAnalyzer(faceDetector);
    }

    /// 设置摄像头并绑定生命周期和显示
    private void setupCamera() {
        controller = new LifecycleCameraController(this);
        controller.bindToLifecycle(this);
        controller.setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA);
        PreviewView previewView = findViewById(R.id.preview_view);
        previewView.setController(controller);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImage(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();

        analyzer.analyze(imageProxy);


        if (mediaImage != null) {
            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (picturesDir == null) {
                Log.e("Perry", "Could not get external pictures directory");
                imageProxy.close();
                return;
            }
            File file = new File(picturesDir, makeFileName());
            try (imageProxy; FileOutputStream fos = new FileOutputStream(file)) {
                Log.d("Perry", "Saving image to: " + file.getAbsolutePath());
                ByteBuffer buffer = mediaImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                fos.write(bytes);
                fos.flush();
                Log.d("Perry", "Image saved successfully");
            } catch (IOException e) {
                Log.e("Perry", "Error saving image", e);
            }
            // 保存数据
            manager.AddDataElement(new DataElement(System.currentTimeMillis(), file.getAbsolutePath(), "Name2"));

//            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//            // 将图像传递给 ML Kit Vision API
//            faceDetector.process(image).addOnSuccessListener(faces -> {
//                // 任务成功完成
//                for (var face : faces) {
//                    Log.d("Perry", String.format("Face: %s", face.getTrackingId()));
//                }
//            }).addOnFailureListener(e -> {
//                // 任务失败
//                Log.e("MainActivity", "Face detection failed", e);
//                imageProxy.close();
//            });
        } else imageProxy.close();
        // Test Commit
    }

    private void bindTakePictureButton() {
        FloatingActionButton btn = findViewById(R.id.take_photo);
        btn.setOnClickListener(v ->
                controller.takePicture(ContextCompat.getMainExecutor(v.getContext()), new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                                super.onCaptureSuccess(imageProxy);
                                Toast.makeText(getApplicationContext(), R.string.photo_taken, Toast.LENGTH_SHORT).show();
                                processImage(imageProxy);
                            }
                        }
                )
        );
    }

    private String makeFileName() {
        return "IMG_" + FILENAME_FORMAT.format(new Date()) + ".jpg";
    }
}

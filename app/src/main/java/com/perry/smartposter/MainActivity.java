package com.perry.smartposter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final ActivityResultLauncher<String> mRequestLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean isGranted) {
                            if (isGranted)
                                performAction();
                            else
                                Toast.makeText(getApplicationContext(),"相机权限不能被申请！",
                                        Toast.LENGTH_LONG).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        super.onStart();
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            mRequestLauncher.launch(Manifest.permission.CAMERA);
        } else {
            Log.d("Perry", "onStart: No Camera Detected.");
        }
    }

    private void performAction(){
        LifecycleCameraController controller =
                new LifecycleCameraController(this);
        controller.bindToLifecycle(this);
        controller.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        PreviewView previewView =findViewById(R.id.preview_view);
        previewView.setController(controller);

        FaceDetectorOptions realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(realTimeOpts);

        Button btn = findViewById(R.id.take_photo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.takePicture(
                        ContextCompat.getMainExecutor(v.getContext()),
                        new ImageCapture.OnImageCapturedCallback() {
                            @OptIn(markerClass = ExperimentalGetImage.class)
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                                super.onCaptureSuccess(imageProxy);
//                                ImageView iv = findViewById(R.id.photo_result);
//                                Bitmap res = image.toBitmap();
//                                iv.setImageBitmap(res);
//                                image.close();

                                Image mediaImage = imageProxy.getImage();
                                if (mediaImage != null) {
                                    InputImage image =
                                            InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                                    // Pass image to an ML Kit Vision API
                                    // ...

                                    Task<List<Face>> result =
                                            detector.process(image)
                                                    .addOnSuccessListener(
                                                            new OnSuccessListener<List<Face>>() {
                                                                @Override
                                                                public void onSuccess(List<Face> faces) {
                                                                    // Task completed successfully
                                                                    for (Face face : faces){
                                                                        // If contour detection was enabled:
                                                                        List<PointF> faceContour =
                                                                                face.getContour(FaceContour.FACE).getPoints();
                                                                        ImageView iv = findViewById(R.id.photo_result);
                                                                        MainActivity.drawFaceContours(image.getBitmapInternal(),faceContour,iv);
                                                                        }
                                                                }
                                                            })
                                                    .addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Task failed with an exception

                                                                    }
                                                                }
                                                            );



                                }
                            }
                        });
            }
        });
    }


    // 在Bitmap上绘制线条示例代码
    public static void drawFaceContours(Bitmap capturedImageBitmap, List<PointF> faceContourPoints, ImageView imageView) {
        if (capturedImageBitmap != null && !capturedImageBitmap.isRecycled() && faceContourPoints != null && !faceContourPoints.isEmpty()) {
            // 根据原始bitmap图创建可修改副本
            Bitmap mutableBitmap = capturedImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            // 创建Paint实例用于画线
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5f);
            paint.setStyle(Paint.Style.STROKE);

            // 遍历脸轮廓数组，依次连线
            for (int i = 0; i < faceContourPoints.size() - 1; i++) {
                PointF startPoint = faceContourPoints.get(i);
                PointF endPoint = faceContourPoints.get(i + 1);
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
            }

            // 连接轮廓的首尾两点
            if (faceContourPoints.size() > 1) {
                PointF firstPoint = faceContourPoints.get(0);
                PointF lastPoint = faceContourPoints.get(faceContourPoints.size() - 1);
                canvas.drawLine(lastPoint.x, lastPoint.y, firstPoint.x, firstPoint.y, paint);
            }

            // 在参数传入的ImageView上显示图像副本
            imageView.setImageBitmap(mutableBitmap);
        }
    }
}
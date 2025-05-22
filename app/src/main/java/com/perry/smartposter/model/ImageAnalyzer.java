package com.perry.smartposter.model;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.perry.smartposter.R;
import com.perry.smartposter.activity.MainActivity;
import com.perry.smartposter.util.FacePainter;

import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private final MainActivity activity;

    public ImageAnalyzer(MainActivity activity) {
        this.activity = activity;
    }

    @ExperimentalGetImage
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            activity.faceDetector.process(image)
                    .addOnSuccessListener(faces -> {
                        Log.d("Perry", "Face detected: " + faces.size());
                        if (!faces.isEmpty()) {
                            Face firstFace = faces.getFirst();
                            Log.d("Perry", "Face contour size: " + firstFace.getAllContours().size());
                            List<PointF> faceContourPoints = firstFace.getAllContours().getFirst().getPoints();
                            Log.d("Perry", "Face contour points: " + faceContourPoints.size());
                            Log.d("Perry", "Media Image Size: " + mediaImage.getHeight() + " " + mediaImage.getWidth());
                            Bitmap capturedImageBitmap = Bitmap.createBitmap(
                                    mediaImage.getWidth(),
                                    mediaImage.getHeight(),
                                    Bitmap.Config.ARGB_8888
                            );
                            showBottomSheet(capturedImageBitmap, faceContourPoints);
                        }

                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        imageProxy.close();
                    });
        } else {
            imageProxy.close();
        }
    }

    private void showBottomSheet(Bitmap bitmap, List<PointF> faceContourPoints) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        // 修复第68行：使用父布局容器作为视图根参数
        View bottomSheetView = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_layout, activity.findViewById(android.R.id.content), false);
        ImageView imageView = bottomSheetView.findViewById(R.id.bottom_sheet_img);
        FacePainter.drawFaceContours(bitmap, faceContourPoints, imageView);
        imageView.setImageBitmap(bitmap);
//        imageView.setImageResource(R.drawable.fish);
        Button yesButton = bottomSheetView.findViewById(R.id.yes_button);
        Button noButton = bottomSheetView.findViewById(R.id.no_button);
        yesButton.setOnClickListener(v -> {
            Log.d("Perry", "showBottomSheet: yesbutton");
        });
        noButton.setOnClickListener(v -> {
            Log.d("Perry", "showBottomSheet: nobutton");
        });
        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetView.getLayoutParams().height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.5);
        bottomSheetDialog.show();
    }
}

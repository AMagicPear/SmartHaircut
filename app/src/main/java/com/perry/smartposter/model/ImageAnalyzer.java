package com.perry.smartposter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.perry.smartposter.R;
import com.perry.smartposter.activity.MainActivity;
import com.perry.smartposter.util.FacePainter;
import com.perry.smartposter.util.ImageStorage;

import java.nio.ByteBuffer;
import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private final MainActivity activity;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    /// 用于缓存过程中的bitmap
    private Bitmap cachedBitmap;
    public static final int IMAGE_VIEW_WIDTH = 244;
    public static final int IMAGE_VIEW_HEIGHT = 326;

    public ImageAnalyzer(MainActivity activity) {
        this.activity = activity;
    }

    /// 分析器的主要功能函数
    @ExperimentalGetImage
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            // 先拷贝 Bitmap 出来，避免后续异步中访问失效
            getBitmapFromMediaImage(mediaImage);
            rotateBitmap(rotationDegrees);
            // 拷贝完成后再创建 InputImage 用于人脸检测
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            imageProxy.close(); // 立即关闭 imageProxy，因为已经拷贝完数据

            activity.faceDetector.process(image)
                    .addOnSuccessListener(faces -> {
                        Log.d("Perry", "Face detected: " + faces.size());

                        if (!faces.isEmpty() && cachedBitmap != null) {
                            Face firstFace = faces.getFirst();
                            List<PointF> faceContourPoints = firstFace.getAllContours().getFirst().getPoints();
                            showBottomSheet(faceContourPoints);
                        } else {
                            Log.e("Perry", "Face empty");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Perry", "Face detection failed", e);
                        imageProxy.close();
                    });
        } else {
            imageProxy.close();
        }
    }


    /// 在底部弹出一个半屏，展示发型选项
    private void showBottomSheet(List<PointF> faceContourPoints) {
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetView = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_layout, activity.findViewById(android.R.id.content), false);
        ImageView imageView = bottomSheetView.findViewById(R.id.bottom_sheet_img);
        Log.d("Perry", "Bitmap is null? " + (cachedBitmap == null));
        Log.d("Perry", "Face contour points size: " + faceContourPoints.size());
        imageView.setImageBitmap(cachedBitmap);
        setupButtons(bottomSheetView);
        // 在叠加层上绘制发型
        try {
            FacePainter.drawHaircut(activity, bottomSheetView.findViewById(R.id.bottom_sheet_overlay), faceContourPoints, (float) IMAGE_VIEW_WIDTH / cachedBitmap.getWidth(), R.drawable.male_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void setupButtons(View bottomSheetView) {
        Button yesButton = bottomSheetView.findViewById(R.id.yes_button);
        Button noButton = bottomSheetView.findViewById(R.id.no_button);
        TextInputEditText nameEditText = bottomSheetView.findViewById(R.id.text_input);

        yesButton.setOnClickListener(v -> {
            var text = nameEditText.getText();
            if (text == null || text.toString().isEmpty()) {
                return;
            }
            Log.d("Perry", "showBottomSheet: yesbutton");
            var filePath = ImageStorage.saveBitmapImage(cachedBitmap, activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            activity.manager.AddDataElement(new DataElement(System.currentTimeMillis(), filePath, text.toString(), 0));
            Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            Log.d("Perry", "showBottomSheet: nobutton");
            bottomSheetDialog.dismiss();
        });
    }

    /// 将 Image 转换成 Bitmap
    private void getBitmapFromMediaImage(Image image) {
        if (image.getFormat() == ImageFormat.JPEG) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] jpegBytes = new byte[buffer.remaining()];
            buffer.get(jpegBytes);
            cachedBitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
            return;
        }
        Log.e("Perry", "Unsupported image format");
    }

    private void rotateBitmap(int rotationDegrees) {
        if (rotationDegrees == 0 || cachedBitmap == null) return;
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(rotationDegrees);
        cachedBitmap = Bitmap.createBitmap(cachedBitmap, 0, 0, cachedBitmap.getWidth(), cachedBitmap.getHeight(), matrix, true);
    }
}

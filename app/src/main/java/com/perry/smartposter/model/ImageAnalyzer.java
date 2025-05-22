package com.perry.smartposter.model;

import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetector;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private final FaceDetector faceDetector;

    public ImageAnalyzer(FaceDetector faceDetector) {
        this.faceDetector = faceDetector;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            faceDetector.process(image).addOnSuccessListener(faces -> {
                        Log.d("Perry", "Face Detect Success, num of faces: " + faces.size());
                        for (Face face : faces) {
                            var point1 = face.getAllContours().getFirst().getPoints().getFirst();
                            Log.d("Perry", point1.x + " " + point1.y);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Perry", "Face detection failed", e);
                        imageProxy.close();
                    });
        }else{
            Log.e("Perry","Image not found.");
        }
    }
}
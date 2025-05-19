package com.perry.smartposter.model;

import android.media.Image;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
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
        }
    }
}
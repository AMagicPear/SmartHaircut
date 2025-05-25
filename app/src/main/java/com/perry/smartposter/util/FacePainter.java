package com.perry.smartposter.util;

import static com.perry.smartposter.model.ImageAnalyzer.IMAGE_VIEW_HEIGHT;
import static com.perry.smartposter.model.ImageAnalyzer.IMAGE_VIEW_WIDTH;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import java.util.List;

public final class FacePainter {

    public static Bitmap drawHaircut(Activity activity, View overlayView, List<PointF> faceContourPoints, float scaleFactor) {
        Bitmap overlayBitmap = Bitmap.createBitmap(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.STROKE);
        // 遍历脸轮廓数组，依次连线
        for (int i = 0; i < faceContourPoints.size() - 1; i++) {
            PointF startPoint = faceContourPoints.get(i);
            PointF endPoint = faceContourPoints.get(i + 1);
            canvas.drawLine(startPoint.x * scaleFactor, startPoint.y * scaleFactor, endPoint.x * scaleFactor, endPoint.y * scaleFactor, paint);
        }

        // 连接轮廓的首尾两点
        if (faceContourPoints.size() > 1) {
            PointF firstPoint = faceContourPoints.get(0);
            PointF lastPoint = faceContourPoints.get(faceContourPoints.size() - 1);
            canvas.drawLine(lastPoint.x * scaleFactor, lastPoint.y * scaleFactor, firstPoint.x * scaleFactor, firstPoint.y * scaleFactor, paint);
        }

        // 设置View背景
        overlayView.setBackground(new BitmapDrawable(activity.getResources(), overlayBitmap));
        return overlayBitmap;
    }

    public static Bitmap mergeBitmaps(Bitmap bottomBitmap, Bitmap topBitmap) {
        // 创建一个新的Bitmap，大小与原始图片相同
        Bitmap mergedBitmap = Bitmap.createBitmap(
                bottomBitmap.getWidth(),
                bottomBitmap.getHeight(),
                bottomBitmap.getConfig()
        );
        // 创建一个Canvas来绘制合并后的图片
        Canvas canvas = new Canvas(mergedBitmap);
        // 先绘制底部的Bitmap
        canvas.drawBitmap(bottomBitmap, 0, 0, null);
        // 再绘制顶部的Bitmap
        canvas.drawBitmap(topBitmap, 0, 0, null);
        return mergedBitmap;
    }
}

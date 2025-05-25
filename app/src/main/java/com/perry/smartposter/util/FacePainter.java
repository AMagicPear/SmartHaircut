package com.perry.smartposter.util;

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
    public static void drawHaircut(Activity activity, View overlayView, List<PointF> faceContourPoints) {
        Bitmap overlayBitmap = Bitmap.createBitmap(244, 326, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        // 遍历脸轮廓数组，依次连线
        for (int i = 0; i < faceContourPoints.size() - 1; i++) {
            PointF startPoint = faceContourPoints.get(i);
            PointF endPoint = faceContourPoints.get(i + 1);
            canvas.drawLine(startPoint.x / 10, startPoint.y / 10, endPoint.x / 10, endPoint.y / 10, paint);
        }

        // 连接轮廓的首尾两点
        if (faceContourPoints.size() > 1) {
            PointF firstPoint = faceContourPoints.get(0);
            PointF lastPoint = faceContourPoints.get(faceContourPoints.size() - 1);
            canvas.drawLine(lastPoint.x / 10, lastPoint.y / 10, firstPoint.x / 10, firstPoint.y / 10, paint);
        }

        // 设置View背景
        overlayView.setBackground(new BitmapDrawable(activity.getResources(), overlayBitmap));
    }
}

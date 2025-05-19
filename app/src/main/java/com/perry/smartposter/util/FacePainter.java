package com.perry.smartposter.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.widget.ImageView;

import java.util.List;

public final class FacePainter {
    /// 在Bitmap上绘制线条示例代码
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

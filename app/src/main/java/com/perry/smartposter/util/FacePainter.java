package com.perry.smartposter.util;

import static com.perry.smartposter.model.ImageAnalyzer.IMAGE_VIEW_HEIGHT;
import static com.perry.smartposter.model.ImageAnalyzer.IMAGE_VIEW_WIDTH;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import com.perry.smartposter.R;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public final class FacePainter {

    public static Bitmap drawHaircut(Activity activity, View overlayView, List<PointF> faceContourPoints, float scaleFactor, int hairStyleImgId, float postScaleFactor) {
        Bitmap overlayBitmap = Bitmap.createBitmap(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);
        // 基准点绘制
        if (!faceContourPoints.isEmpty()) {
            // 计算发型基准点
            var calculateHairBasePoint = calculateHairBasePoint(faceContourPoints);
            PointF hairBasePoint = calculateHairBasePoint.getKey();
            float faceHeight = calculateHairBasePoint.getValue();
            Log.d("Perry", "drawHaircut: "+ hairBasePoint.x + hairBasePoint.y +"faceHeight" + faceHeight);
            // 加载发型图片
            Bitmap hairBitmap = BitmapFactory.decodeResource(activity.getResources(), hairStyleImgId);
            // 计算图片绘制位置（使图片中心对齐基准点）
            Matrix matrix = new Matrix();
            float scale = 0.00006f * faceHeight * postScaleFactor; // 缩放比例
            matrix.postScale(scale, scale);
            matrix.postTranslate(
                    hairBasePoint.x * scaleFactor - hairBitmap.getWidth()*scale/2f,
                    hairBasePoint.y * scaleFactor - hairBitmap.getHeight()*scale/2f + 30f
            );
            canvas.drawBitmap(hairBitmap, matrix, null);
            // 绘制发型图片（中心对齐基准点）
            canvas.drawBitmap(hairBitmap, matrix, null);
        }

        // 设置View背景
        overlayView.setBackground(new BitmapDrawable(activity.getResources(), overlayBitmap));
        return overlayBitmap;
    }

    // 在 FacePainter 类中添加以下方法
    private static Map.Entry<PointF, Float> calculateHairBasePoint(List<PointF> faceContourPoints) {
        // 找到面部轮廓的最高点（Y值最小）和最低点（Y值最大）
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float sumX = 0;
        int count = 0;

        for (PointF point : faceContourPoints) {
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }

        for (PointF point : faceContourPoints) {
            // 仅处理上半部分的面部轮廓点（Y值小于中点）
            if (point.y < (minY + maxY) / 2) {
                sumX += point.x;
                count++;
            }
        }

        // 计算平均X坐标和基准Y坐标
        float baseX = count > 0 ? sumX / count : faceContourPoints.get(0).x;
        float faceHeight = maxY - minY;
        float baseY = minY - faceHeight * 0.2f; // 在额头基础上再上移20%的脸部高度
        return new AbstractMap.SimpleEntry<>(new PointF(baseX,baseY), faceHeight);
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

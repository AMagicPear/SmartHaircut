package com.perry.smartposter.util;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ImageStorage {
    public static final SimpleDateFormat FILENAME_FORMAT = new SimpleDateFormat("yyMMdd_HHmmss", Locale.CHINA);

    public static String makeFileName() {
        return "IMG_" + FILENAME_FORMAT.format(new Date()) + ".jpg";
    }

    public static String saveMediaImage(Image mediaImage, File picturesDir) {
        if (mediaImage != null) {
            if (picturesDir == null) {
                Log.e("Perry", "Could not get external pictures directory");
                return null;
            }
            File file = new File(picturesDir, makeFileName());
            try (FileOutputStream fos = new FileOutputStream(file)) {
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
            return file.getAbsolutePath();
            // 保存数据
//            manager.AddDataElement(new DataElement(System.currentTimeMillis(), file.getAbsolutePath(), "Name2"));
        } else {
            return null;
        }
    }

    public static String saveBitmapImage(Bitmap bitmap, File picturesDir) {
        if (bitmap != null) {
            if (picturesDir == null) {
                Log.e("Perry", "Could not get external pictures directory");
                return null;
            }
            File file = new File(picturesDir, makeFileName());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                return file.getAbsolutePath();
            } catch (IOException e) {
                Log.e("Perry", "Error saving image", e);
            }
        }
        return null;
    }
}

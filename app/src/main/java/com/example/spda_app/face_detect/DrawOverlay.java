package com.example.spda_app.face_detect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.params.Face;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

public class DrawOverlay extends Drawable {
    private final Paint boundingRectPaint = new Paint();
    private final Paint dotPaint = new Paint();
    private final Paint contentTextPaint = new Paint();
    private final float padding = 25;
    Face face;
    PreviewView previewView;
    private Metadata metadata;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private int width, height;
    private float ratio;
    public DrawOverlay(Metadata metadata) {
        this.metadata = metadata;
        boundingRectPaint.setStyle(Paint.Style.STROKE);
        boundingRectPaint.setColor(Color.GREEN);
        boundingRectPaint.setStrokeWidth(5F);
        boundingRectPaint.setAlpha(200);

        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.RED);
        dotPaint.setAlpha(255);

        contentTextPaint.setColor(Color.WHITE);
        contentTextPaint.setAlpha(255);
        contentTextPaint.setTextSize(36F);

        left = metadata.getRectData().left;
        right = metadata.getRectData().right;
        top = metadata.getRectData().top;
        bottom = metadata.getRectData().bottom;

        width = metadata.getWidth();
        height = metadata.getHeight();
        ratio = (float) width / height;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //canvas.drawRect(metadata.getRectData(), boundingRectPaint);
        //canvas.drawRect(left, top, right, bottom, boundingRectPaint);
        canvas.drawRect(left,top,right,bottom, boundingRectPaint);
        canvas.drawCircle(metadata.faceLeftTopCoord().get(0), metadata.faceLeftTopCoord().get(1), 10, dotPaint);
        canvas.drawCircle(metadata.faceLeftBottomCoord().get(0), metadata.faceLeftBottomCoord().get(1), 10, dotPaint);
        canvas.drawCircle(metadata.faceRightBottomCoord().get(0), metadata.faceRightBottomCoord().get(1), 10, dotPaint);
        canvas.drawCircle(metadata.faceRightTopCoord().get(0), metadata.faceRightTopCoord().get(1), 10, dotPaint);
        canvas.drawText(String.valueOf(ratio), left + 200, bottom + padding*2, contentTextPaint);
        canvas.drawText(String.valueOf(width), left + 200, bottom + padding*4, contentTextPaint);
        canvas.drawText(String.valueOf(height), left + 200, bottom + padding*6, contentTextPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        boundingRectPaint.setAlpha(alpha);
        dotPaint.setAlpha(alpha);
        contentTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        boundingRectPaint.setColorFilter(colorFilter);
        dotPaint.setColorFilter(colorFilter);
        contentTextPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

package com.example.spda_app.face_detect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class DrawLandmark extends Drawable {
    private LandmarkData landmarkData;
    private Metadata metadata;
    private final Paint dotPaint = new Paint();
    private final Paint contentTextPaint = new Paint();
    private int[] mapCoordX;
    private int[] mapCoordY;
    private double earRight;
    private double earLeft;
    private Rect rectData;
    private final float padding = 25;

    public DrawLandmark(LandmarkData landmarkData, Metadata metadata) {
        this.landmarkData = landmarkData;
        this.metadata = metadata;
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.GREEN);
        dotPaint.setAlpha(255);
        contentTextPaint.setColor(Color.WHITE);
        contentTextPaint.setAlpha(255);
        contentTextPaint.setTextSize(36F);

        mapCoordX = landmarkData.getMapCoordX();
        mapCoordY = landmarkData.getMapCoordY();
        earLeft = landmarkData.earLeft();
        earRight = landmarkData.earRight();

        rectData = metadata.getRectData();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        for(int i = 0; i < 68; i++) {
            canvas.drawCircle(mapCoordX[i], mapCoordY[i], 10, dotPaint);
        }
        Log.d("coords", "coordX "+ Arrays.toString(mapCoordX));
        Log.d("coords", "coordY "+ Arrays.toString(mapCoordY));
        canvas.drawText(String.valueOf(earLeft), 600, 500, contentTextPaint);
        canvas.drawText(String.valueOf(earLeft), 600, 600, contentTextPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}

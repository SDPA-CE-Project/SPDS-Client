package com.example.spda_app.face_detect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawLandmarkGraphic extends GraphicOverlay.Graphic {
    private final Paint dotPaint = new Paint();
    private int[] mapCoordX;
    private int[] mapCoordY;
    private LandmarkData landmarkData;
    public DrawLandmarkGraphic(GraphicOverlay overlay, LandmarkData landmarkData) {
        super(overlay);
        this.landmarkData = landmarkData;
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.GREEN);
        dotPaint.setAlpha(255);

        mapCoordX = landmarkData.getMapCoordX();
        mapCoordY = landmarkData.getMapCoordY();

    }
    @Override
    public void draw(Canvas canvas) {
        for(int i = 0; i < 68; i++) {
            canvas.drawCircle(mapCoordX[i] * 2, mapCoordY[i] * 2, 5, dotPaint);
        }
    }
}

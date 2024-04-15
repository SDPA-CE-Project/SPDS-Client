package com.example.spda_app.face_detect;

import android.graphics.Rect;

import java.util.List;

public class LandmarkData {
    private int[] mapCoordXY;
    private Metadata metadata;
    private int[] mapCoordX;
    private int[] mapCoordY;

    public LandmarkData(int[] mapCoordXY, Metadata metadata) {
        this.mapCoordXY = mapCoordXY;
        this.metadata = metadata;
    }

    public int[] getMapCoordX() {
        int[] temp = new int[68];
        Rect rect = metadata.getRectData();
        int width = metadata.getWidth();
        float scale = (float) width / 256;
        for(int i = 0; i < temp.length; i++) {
            //temp[i] = Math.round(rect.left + (mapCoordXY[i*2] * scale));
            temp[i] = mapCoordXY[i*2];
        }
        return temp;
    }
    public int[] getMapCoordY() {
        int[] temp = new int[68];
        Rect rect = metadata.getRectData();
        int height = metadata.getHeight();
        float scale = (float) height / 256;
        for(int i = 0; i < temp.length; i++) {
            //temp[i] = Math.round(rect.top + (mapCoordXY[i*2+1] * scale));
            temp[i] = mapCoordXY[i*2+1];
        }
        return temp;
    }
}

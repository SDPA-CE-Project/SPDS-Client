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
    private double calculateEuclideanDistance(int point1X, int point2X, int point1Y, int point2Y) {
        float dx = point1X - point2X;
        float dy = point1Y - point2Y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public double earLeft() {
        int[] coordX = getMapCoordX();
        int[] coordY = getMapCoordY();

        double A = calculateEuclideanDistance(coordX[37], coordX[40], coordY[37], coordY[40]);
        double B = calculateEuclideanDistance(coordX[38], coordX[41], coordY[38], coordY[41]);
        double C = calculateEuclideanDistance(coordX[36], coordX[39], coordY[36], coordY[39]);
        return (A + B) / (2.0 * C);
    }
    public double earRight() {
        int[] coordX = getMapCoordX();
        int[] coordY = getMapCoordY();

        double A = calculateEuclideanDistance(coordX[43], coordX[46], coordY[43], coordY[46]);
        double B = calculateEuclideanDistance(coordX[44], coordX[47], coordY[44], coordY[47]);
        double C = calculateEuclideanDistance(coordX[42], coordX[45], coordY[42], coordY[45]);
        return (A + B) / (2.0 * C);
    }
    public double marAvg() {
        int[] coordX = getMapCoordX();
        int[] coordY = getMapCoordY();

        double A = calculateEuclideanDistance(coordX[61], coordX[65], coordY[61], coordY[65]);
        double B = calculateEuclideanDistance(coordX[63], coordX[67], coordY[63], coordY[67]);
        double C = calculateEuclideanDistance(coordX[60], coordX[64], coordY[60], coordY[64]);
        return (A + B) / (2.0 * C);
    }
    public double earAvg() {
        double right = earRight();
        double left = earLeft();

        return (left+right)/2;
    }
}

package com.example.spda_app.face_detect;

import android.graphics.Rect;
import com.google.mlkit.vision.face.Face;

import java.util.ArrayList;
import java.util.List;

public class Metadata {
    private Face face;
    Metadata() {}
    public Metadata(Face face) {
        this.face = face;
    }
    Face getFaceData() {
        return face;
    }
    Rect getRectData() {
        return face.getBoundingBox();
    }
    List<Integer> faceLeftTopCoord() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0, getRectData().left);
        list.add(1, getRectData().top);
        return list;
    }
    List<Integer> faceLeftBottomCoord() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0, getRectData().left);
        list.add(1, getRectData().bottom);
        return list;
    }
    List<Integer> faceRightTopCoord() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0, getRectData().right);
        list.add(1, getRectData().top);
        return list;
    }
    List<Integer> faceRightBottomCoord() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0, getRectData().right);
        list.add(1, getRectData().bottom);
        return list;
    }
    int getWidth() {
        return faceRightTopCoord().get(0) - faceLeftTopCoord().get(0);
    }
    int getHeight() {
        return faceLeftBottomCoord().get(1) - faceLeftTopCoord().get(1);
    }

}

package com.example.spda_app;

public class Value {
    public double left;
    public double right;
    public double avg;
    public boolean check;

    public Value(double left, double right, double avg) {
        this.left = left;
        this.right = right;
        this.avg = avg;

    }
    public void setCheck(boolean check) {
        this.check = check;
    }
    public Value() {

    }

//    public double getLeft() {
//        return left;
//    }
//    public void setLeft(double left) {
//        this.left = left;
//    }
//    public double getRight() {
//        return right;
//    }
//    public void setRight() {
//        this.right = right;
//    }
}

package com.yung.widget;

import android.graphics.Bitmap;

/**
 * Created by Brian on 2017-04-17.
 */

public class CircleCollision {
    private int iniX;//初始化时记录的坐标
    private int iniY;//初始化时最起初的Y坐标
    private float centerX;
    private float centerY;
    private float radius;//自身半径bitmap图形
    private float tickradius;//钟摆半径
    private float conheight;
    private float conwidth;
    private double radians;//当前度数
    private boolean right;//向右摆动
    private Bitmap bitmap;//当前图形

    public CircleCollision() {
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getConheight() {
        return conheight;
    }

    public void setConheight(float conheight) {
        this.conheight = conheight;
    }

    public float getConwidth() {
        return conwidth;
    }

    public void setConwidth(float conwidth) {
        this.conwidth = conwidth;
    }

    public int getIniX() {
        return iniX;
    }

    public void setIniX(int iniX) {
        this.iniX = iniX;
    }

    public int getIniY() {
        return iniY;
    }

    public void setIniY(int iniY) {
        this.iniY = iniY;
    }

    public double getRadians() {
        return radians;
    }

    public void setRadians(double radians) {
        this.radians = radians;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getTickradius() {
        return tickradius;
    }

    public void setTickradius(float tickradius) {
        this.tickradius = tickradius;
    }
}

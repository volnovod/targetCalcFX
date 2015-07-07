package com.aim.comvision;

import java.util.ArrayList;

/**
 * Created by victor on 07.07.15.
 */
public class Cluster {

    private int currentX;
    private int currentY;
    private int lastX;
    private int lastY;

    ArrayList<Pixel> pixels;

    public int getClusterSize() {
        return clusterSize;
    }

    public void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    private int clusterSize;

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public ArrayList<Pixel> getPixels() {
        return pixels;
    }

    public void setPixels(ArrayList<Pixel> pixels) {
        this.pixels = pixels;
    }

    public Cluster() {

    }
}

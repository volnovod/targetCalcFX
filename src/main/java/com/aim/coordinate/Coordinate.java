package com.aim.coordinate;

/**
 * Created by victor on 20.02.15.
 * клас, який описує координати точки
 */
public class Coordinate {

    private double latitude;
    private double longitude;
    private double latitudeGrad;
    private double longitudeGrad;

    public Coordinate(double latitude, double longitude) {
        this.latitude = Math.toRadians(latitude);
        this.longitude = Math.toRadians(longitude);
        this.latitudeGrad = latitude;
        this.longitudeGrad = longitude;
    }

    public Coordinate() {
    }

    public double getLatitudeRad() {
        return latitude;
    }

    public void setLatitudeRad(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitudeRad() {
        return longitude;
    }

    public void setLongitudeRad(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitudeGrad() {
        return latitudeGrad;
    }

    public void setLatitudeGrad(double latitudeGrad) {
        this.latitudeGrad = latitudeGrad;
    }

    public double getLongitudeGrad() {
        return longitudeGrad;
    }

    public void setLongitudeGrad(double longitudeGrad) {
        this.longitudeGrad = longitudeGrad;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitudeGrad=" + latitudeGrad +
                ", longitudeGrad=" + longitudeGrad +
                '}';
    }
}

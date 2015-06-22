package com.aim.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;

/**
 * Created by victor on 17.02.15.
 * об’єкт, який являє собою ціль із параметрами - координати в системі WGS84,CK42
 * шлях до фото цілі, порядковий номер, дальність
 */
@Entity
@Table(name="AIM")
public class Aim {

    @Id
    @SequenceGenerator(name="sequence", sequenceName = "AIM_SEQ", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private Integer id;

    @Column(name = "LATITUDE", nullable = false)
    private double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private double longitude;

    @Column(name = "PATH", nullable = true)
    private String path;

    @Column ( name = "LATITUDECK42", nullable = false)
    private double latitudeck42;

    @Column ( name = "LONGITUDECK42", nullable = false)
    private double longitudeck42;

    @Column (name = "DISTANCE", nullable = false)
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getLatitudeck42() {
        return latitudeck42;
    }

    public void setLatitudeck42(double latitudeck42) {
        this.latitudeck42 = latitudeck42;
    }

    public double getLongitudeck42() {
        return longitudeck42;
    }

    public void setLongitudeck42(double longitudeck42) {
        this.longitudeck42 = longitudeck42;
    }

    @Override
    public String toString() {
        return "Aim{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", path='" + path + '\'' +
                ", latitudeck42=" + latitudeck42 +
                ", longitudeck42=" + longitudeck42 +
                ", distance=" + distance +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




}

package com.aim.aimcalculator;


import com.aim.coordinate.Coordinate;

/**
 * Created by victor on 04.03.15.
 * клас, який перетворює координати із прямокутної системи координат в
 * координати на елепісоїді красовького
 */
public class BackProjectionImpl implements BackProjection{

    private final double aKr = 6378245;
    private final double bKr = 6356863.019;
    private double eKr;

    private final double error = 0.001;
    private WGStoCK42Impl ck42;

    public BackProjectionImpl() {
        this.ck42 = new WGStoCK42Impl();
        this.eKr = Math.sqrt((aKr * aKr - bKr * bKr) / (aKr * aKr));
    }

    public WGStoCK42Impl getCk42() {
        return ck42;
    }

    public void setCk42(WGStoCK42Impl ck42) {
        this.ck42 = ck42;
    }

    /**
     * метод, який виконує перерахунок координат
     * @return координати в системі СК-42
     */
    @Override
    public Coordinate transform() {
        double longitude;
        double latitude;
        double height;
        Coordinate res = new Coordinate();

        double x = this.ck42.getLatitude42();
        double y = this.ck42.getLongitude42();
        double z = this.ck42.getHeight42();

        double D = Math.sqrt(x * x + y * y);
        if (D == 0) {
            latitude = Math.toDegrees((Math.PI / 2) * z / (Math.abs(z)));
        } else {

            double la = Math.abs(Math.asin(y/D));

            if (y < 0 && x > 0) {
                longitude = Math.toDegrees(2 * Math.PI - la);
                res.setLongitudeGrad(longitude);
            } else if (y < 0 && x < 0) {
                longitude = Math.toDegrees(Math.PI + la);
                res.setLongitudeGrad(longitude);
            } else if (y > 0 && x < 0) {
                longitude = Math.toDegrees(Math.PI - la);
                res.setLongitudeGrad(longitude);
            } else if (y > 0 && x > 0) {
                longitude = Math.toDegrees(la);
                res.setLongitudeGrad(longitude);
            } else if (y == 0 && x > 0) {
                longitude = 0;
                res.setLongitudeGrad(longitude);
            } else if (y == 0 && x < 0) {
                longitude = Math.toDegrees(Math.PI);
                res.setLongitudeGrad(longitude);
            }
        }

        if (z == 0) {
            latitude = 0;
            height = D - aKr;
        } else {
            double r = Math.sqrt(x * x + y * y + z * z);
            double c = Math.asin(z / r);
            double p = eKr * eKr * aKr / (2 * r);

            double s1;
            double s2 = 0;
            double b;
            double d;
            do {
                s1 = s2;
                b = c + s1;
                s2=Math.asin((p * Math.sin(2 * b))/(Math.sqrt(1 - eKr*eKr*Math.sin(b)*Math.sin(b))));
                d = Math.abs(s2-s1);

            }while (d > error);
                latitude = b;
                height = D*Math.cos(latitude) + z*Math.sin(latitude) - aKr * (Math.sqrt(1 - eKr*eKr*Math.sin(bKr)*Math.sin(bKr)));
            }

            if(latitude < 0){
                latitude += 2 * Math.PI;
            }

            res.setLatitudeGrad(Math.toDegrees(latitude));
        return res;
        }
    }


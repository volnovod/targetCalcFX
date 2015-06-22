package com.aim.aimcalculator;


import com.aim.coordinate.Coordinate;
import com.aim.model.Aim;

import java.lang.Math;

/**
 * Created by victor on 20.02.15.
 * клас, який рахує координати цілі по відомим координатам спостеріганням,
 * дальності, азимуті цілі
 */
public class AimCalculatorImpl implements AimCalculator {

    private Coordinate coordinate;
    private Aim aim;
    private WGStoCK42Impl wgStoCK42;
    private final long a=6378137;//meters
    private final double f = 1/298.257223;
    private final long b = (long) (a*(1-f));
    private double height;

    public AimCalculatorImpl() {
        this.coordinate = new Coordinate();
        this.aim = new Aim();
        this.wgStoCK42 = new WGStoCK42Impl();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Aim getAim() {
        return aim;
    }

    public void setAim(Aim aim) {
        this.aim = aim;
    }

    public double calcE(){
        return (Math.sqrt((a*a-b*b)/(a*a)));
    }

    public double[] calcNM(double B){
        double[] res = new double[2];
        double e = calcE();
        double W = Math.sqrt(1-e*e*Math.sin(B*Math.PI/180)*Math.sin(B*Math.PI/180));
        double N = a/W;
        double M =a*(1-e*e)/(W*W*W);
        res[0] = N;
        res[1] = M;
        return res;
    }


    /**
     * метод розрахунку координат цілі
     * @param B - широта спостерігання в градусах
     * @param L - довгота спостерігання в градусах
     * @param A - азимут цілі в градусах
     * @param S - дальність цілі
     */
    @Override
    public void calcCoordinate(double B, double L, double A, double S){
        double[] NM = calcNM(B);
        double N = NM[0];
        double M = NM[1];
        this.coordinate.setLatitudeGrad(calcB(B, A, S, M));
        this.coordinate.setLongitudeGrad(calcL(B, A, L, S, N));

        this.wgStoCK42.setLatitude(B);
        this.wgStoCK42.setLongitude(L);
        this.wgStoCK42.setHeight(getHeight());

        wgStoCK42.transformtoCk42();
        BackProjectionImpl backProjectionImpl = new BackProjectionImpl();
        backProjectionImpl.setCk42(wgStoCK42);
        Coordinate ck42= backProjectionImpl.transform();

        this.aim.setLatitude(this.coordinate.getLatitudeGrad());
        this.aim.setLongitude(this.coordinate.getLongitudeGrad());
        this.aim.setLatitudeck42(ck42.getLatitudeGrad());
        this.aim.setLongitudeck42(ck42.getLongitudeGrad());
    }

    /**
     *метод розраховує широту цілі
     * @param B - широта спостерігання в градусах
     * @param A - азимут цілі в градусах
     * @param S - дальність цілі
     * @return широта цілі в градусах
     */
    @Override
    public double calcB(double B, double A, double S, double M){
        double res;
        res = Math.toDegrees(S * Math.cos(Math.toRadians(A))/M) + B;
        return res;
    }

    /**
     * розрахунок довготи цілі
     * @param B - широта спостерігання в градусах
     * @param A - азимут цілі в градусах
     * @param L - довгота спостерігання в градусах
     * @param S - дальність цілі
     * @param N - допоміжний коефіцієнт
     * @return довгота цілі в градусах
     */
    @Override
    public double calcL(double B, double A, double L, double S, double N){
        double res;
        res = Math.toDegrees(S * Math.sin(Math.toRadians(A))/(N * Math.cos(Math.toRadians(B)))) + L;
        return res;
    }

    /**
     *розраховує азимут цілі
     * @param B - широта спостерігання в градусах
     * @param A - азимут цілі в градусах
     * @param S - дальність цілі
     * @param N - допоміжний коефіцієнт
     * @return азимут цілі
     */
    @Override
    public double calcA(double B, double A, double S, double N){
        double res;
        res = Math.toDegrees(S * Math.sin(Math.toRadians(A)) * Math.tan(Math.toRadians(B)) / N) + A;
        return res;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}

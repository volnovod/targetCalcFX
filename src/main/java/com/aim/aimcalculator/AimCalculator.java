package com.aim.aimcalculator;

/**
 * Created by victor on 20.02.15.
 * it must calculate aim coordinates
 */
public interface AimCalculator {

    double calcE();
    void calcCoordinate(double B, double L, double A, double S);
    double calcB(double B, double A, double S, double M);
    double calcL(double B, double A, double L, double S, double N);
    double calcA(double B, double A, double S, double N);
}

package com.aim.aimcalculator;


import com.aim.coordinate.Coordinate;
import com.aim.model.Aim;

/**
 * Created by victor on 02.03.15.
 */
public class Calc2 {
    private Coordinate coordinate;
    private Aim aim;
    private final long a=6378137;//metrs
    private final double f = 1/298.257223;
    private final long b = (long) (a*(1-f));

    public Calc2() {
        this.coordinate = new Coordinate();
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

//    public static void main(String[] args) {
//        Calc2 calc = new Calc2();
//        calc.calcCoordinate(50.155446, 31.904916, 0, 435);
//        System.out.println(calc.getCoordinate().toString());
//    }




    //    B,L,A must be grad
    public void calcCoordinate(double B, double L, double A, double S){
        double[] NM = calcNM(B);
        double N = NM[0];
        double M = NM[1];
        this.coordinate.setLatitudeGrad(calcB(B, A, S, M));
        this.coordinate.setLongitudeGrad(calcL(B, A, L, S, N));
    }

    /* result in degrees*/
    public double calcB(double B, double A, double S, double M){
        double res;
        res = Math.toDegrees(S * Math.cos(Math.toRadians(A))/M) + B;
        return res;
    }

    public double calcL(double B, double A, double L, double S, double N){
        double res;
        System.out.println(S * Math.sin(Math.toRadians(A))/(N * Math.cos(Math.toRadians(B))));
        res = Math.toDegrees(S * Math.sin(Math.toRadians(A))/(N * Math.cos(Math.toRadians(B)))) + L;
        return res;
    }

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

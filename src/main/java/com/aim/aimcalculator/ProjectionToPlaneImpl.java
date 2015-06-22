package com.aim.aimcalculator;

/**
 * Created by victor on 03.03.15.
 * клас, який перетворює координати із сферичної сиистеми координат в прямокутну
 */
public class  ProjectionToPlaneImpl implements ProjectionToPlane {

    private final long a=6378137;// велика піввісь еліпсоїда WGS
    private final double f = 1/298.257223;// стискання еліпсоїда WGS
    private final long b = (long) (a*(1-f));// мала піввісь

    public ProjectionToPlaneImpl() {
    }

    /**
     * метод розраховує ексцентриситет еліпсоїда
     * @return E
     */
    public double calcE(){
        return (Math.sqrt((a*a-b*b)/(a*a)));
    }

    /**
     * метод розраховує допоміжний коефіцієнт N
     * @param B
     * @return N
     */
    public double calcN(double B){
        double e = calcE();
        double W = Math.sqrt(1-e*e*Math.sin(Math.toRadians(B))*Math.sin(Math.toRadians(B)));
        double N = a/W;
        return N;
    }

    /*return array, array[0] - X, array[1] - Y, array[2] - Z*/

    /**
     * метод розрахунку прямокутних координат
     * @param B - широта точки
     * @param L - довгота точки
     * @param H - геодезична висота точки
     * @return координати X,Y,Z
     */
    @Override
    public double[] projection(double B, double L, double H) {
        double[] res = new double[3];
        double e = calcE();
        double N = this.calcN(B);
        res [0] = (N + H) * Math.cos(Math.toRadians(B)) * Math.cos(Math.toRadians(L));
        res [1] = (N + H) * Math.cos(Math.toRadians(B)) * Math.sin(Math.toRadians(L));
        res [2] = ((1 - e * e)*N + H) * Math.sin(Math.toRadians(B));
        return res;
    }

}

package com.aim.comvision;

import java.util.Random;

/**
 * Created by victor on 16.08.15.
 */
public class GaussNoise {

    private final int width=1280;
    private final int height=720;
    private double[] array;

    public GaussNoise() {
        this.array = new double[width*height];
    }

    public double[] getArray(){

        Random random = new Random();

        for( int i=0; i<this.array.length; i++){
            this.array[i] = random.nextDouble()*255;
        }
        return this.array;
    }

    public static void main(String[] args) {
        GaussNoise noise = new GaussNoise();
        double[] array = noise.getArray();

        double m=0;
        double sum=0;
        for (int i = 0; i<array.length; i++){
            sum+=array[i];
        }

        m=sum/array.length;
        System.out.println("Маточікування "+m);

        sum=0;
        for (int i=0; i<array.length; i++){
            sum+=(array[i]-m)*(array[i]-m);
        }
        double CKO=Math.sqrt(sum/array.length);
        System.out.println("CKO="+CKO);

//        Маточікування 0.500598736136539
//        CKO=0.2887517372525874

    }
}

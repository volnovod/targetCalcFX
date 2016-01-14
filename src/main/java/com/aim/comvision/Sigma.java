package com.aim.comvision;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by root on 26.08.15.
 */
public class Sigma {

    public static void main(String[] args) {
//        double[] SN = {10,9.5,9,8.5,8,7.5,7,6.5,6};
        double[] SN = new double[19];
        SN[0]=10;;
        for (int i =1; i<19; i++){
            SN[i]=SN[i-1]-0.5;
        }
        double[] sigma = new double[SN.length];
        double M=50;
        File file = new File("sigma10-1.txt");
        try {
            FileWriter writer = new FileWriter(file);
            for (int i=0; i<sigma.length; i++){
                sigma[i] = M/Math.pow(10, (SN[i]/20));
                writer.write(String.valueOf(sigma[i]) + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

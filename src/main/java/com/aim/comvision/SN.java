package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by root on 18.08.15.
 */
public class SN {

    public static void main(String[] args) {
        File file1 = new File("/home/victor/Java/workFX/img/testNoise27.png");
        BufferedImage bufferedImage1 = new BufferedImage(1280, 720, BufferedImage.TYPE_BYTE_GRAY);
        try {

            bufferedImage1 = ImageIO.read(new FileInputStream(file1));

        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("H="+bufferedImage1.getHeight());
        WritableRaster raster = bufferedImage1.getRaster();

        int[] values = new int[1280*720];
        raster.getPixels(0, 0, 1280, 720, values);




        double m=0;
        double CKO=0;
        double sum=0;

        for (int i=0; i<values.length;i++){
            sum+=values[i];        }
        m=sum/values.length;

        sum=0;
        for (int i=0; i<values.length; i++){
            sum+=(values[i]-m)*(values[i]-m);
        }
        CKO = Math.sqrt(sum/values.length);

        double sn = 20*Math.log10(m/CKO);
        System.out.println("CKO="+CKO + " M="+m);
        System.out.println("S/N="+sn);
    }

}

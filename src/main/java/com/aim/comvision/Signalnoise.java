package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by root on 13.08.15.
 */
public class Signalnoise {

    public static BufferedImage convertToGrayScale(BufferedImage image) {
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    public static void main(String[] args) {

        File file1 = new File("/home/victor/Java/workFX/img/lena.png");
        BufferedImage bufferedImage1 = null;
        try {

            bufferedImage1 = ImageIO.read(new FileInputStream(file1));

        } catch (IOException e) {
            e.printStackTrace();

        }

        int[] array1 = bufferedImage1.getRGB(0, 0, bufferedImage1.getWidth(), bufferedImage1.getHeight(), null, 0, bufferedImage1.getWidth());

//        int[] array1 = bufferedImage11.

        File file2 = new File("/home/victor/Java/workFX/img/lenaClean.png");
        BufferedImage bufferedImage2 = null;
        try {

            bufferedImage2 = ImageIO.read(new FileInputStream(file2));

        } catch (IOException e) {
            e.printStackTrace();

        }


        int[] array2 = bufferedImage2.getRGB(0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight(), null, 0, bufferedImage2.getWidth());

        File file3 = new File("/home/victor/Java/workFX/img/lena_noiseImg.jpg");
        BufferedImage bufferedImage3 = null;
        try {

            bufferedImage3 = ImageIO.read(new FileInputStream(file3));

        } catch (IOException e) {
            e.printStackTrace();

        }


        GaussNoise noise = new GaussNoise();
        double[] noiseArray = noise.getArray();


        int[] array3 = bufferedImage3.getRGB(0, 0, bufferedImage3.getWidth(), bufferedImage3.getHeight(), null, 0, bufferedImage3.getWidth());


        double[] blue1 = new double[array1.length];
        double[] blue2 = new double[array1.length];
        double[] blue3 = new double[array1.length];

        double[] red1 = new double[array1.length];
        double[] red2 = new double[array1.length];
        double[] red3 = new double[array1.length];


        double[] green1 = new double[array1.length];
        double[] green2 = new double[array1.length];
        double[] green3 = new double[array1.length];

        for (int i = 0; i < array1.length; i++) {

            blue1[i] = array1[i] & 0xFF;
            green1[i] = (array1[i] >> 8) & 0xFF;
            red1[i] = (array1[i] >> 16) & 0xFF;


            blue2[i] = array2[i] & 0xFF;
            green2[i] = (array2[i] >> 8) & 0xFF;
            red2[i] = (array2[i] >> 16) & 0xFF;


            blue3[i] = array3[i] & 0xFF;
            green3[i] = (array3[i] >> 8) & 0xFF;
            red3[i] = (array3[i] >> 16) & 0xFF;
        }


//        CKO=73.63704276240007
        // добавляємо шум до чистої картинки
        for (int i=0; i< array1.length; i++){
            if(blue1[i] >= noiseArray[i]){
                blue1[i] = blue1[i] - noiseArray[i];
            } else {
                blue1[i] = noiseArray[i] - blue1[i];
            }
            if (red1[i] >= noiseArray[i]){
                red1[i] = red1[i] - noiseArray[i];

            }else {
                red1[i] = noiseArray[i] - red1[i];
            }
            if (green1[i] >= noiseArray[i]){
                green1[i] = green1[i] - noiseArray[i];
            } else {
                green1[i] = noiseArray[i] - green1[i];
            }
        }
        // записуємо каринку із шумом із 3 масивів ЧБЗ

        int[] o_array = new int[1280*720];

        for (int i=0; i<array1.length; i++){
            o_array[i] = ((int)red1[i] << 16) | ((int)green1[i] << 8) | (int)blue1[i];
        }

        double[] v1 = new double[array1.length];
        double[] v2 = new double[array1.length];
        double[] v3 = new double[array1.length];

        for (int i = 0; i < array1.length; i++) {
            v1[i] = red1[i] + blue1[i] + green1[i];
            v2[i] = red2[i] + blue2[i] + green2[i];
            v3[i] = red3[i] + blue3[i] + green3[i];
        }



            double[] input1 = new double[array1.length];
            for (int i = 0; i < array1.length; i++) {
                input1[i] = array1[i];
            }

            double[] input2 = new double[array1.length];
            for (int i = 0; i < array2.length; i++) {
                input2[i] = array2[i];
            }

            double[] input3 = new double[array1.length];
            for (int i = 0; i < array3.length; i++) {
                input3[i] = array3[i];
            }

            double sumr = 0;
            double sumb = 0;
            double sumg = 0;

            for (int i = 0; i < array1.length; i++) {
                sumr += (red2[i] - red1[i]) * (red2[i] - red1[i]);
                sumb += (blue2[i] - blue1[i]) * (blue2[i] - blue1[i]);
                sumg += (green2[i] - green1[i]) * (green2[i] - green1[i]);
            }


        double CKOfilterRED = Math.sqrt(Math.abs(sumr) / array1.length);

        double CKOfilterBLUE = Math.sqrt(Math.abs(sumb) / array1.length);

        double CKOfilterGREEN = Math.sqrt(Math.abs(sumg) / array1.length);

            sumr = 0;
            sumb = 0;
            sumg = 0;

            for (int i = 0; i < array1.length; i++) {
                sumr += (red3[i] - red1[i]) * (red3[i] - red1[i]);
                sumb += (blue3[i] - blue1[i]) * (blue3[i] - blue1[i]);
                sumg += (green3[i] - green1[i]) * (green3[i] - green1[i]);
            }

            double CKOnRED = Math.sqrt(Math.abs(sumr) / array1.length);

            double CKOnBLUE = Math.sqrt(Math.abs(sumb) / array1.length);

            double CKOnGREEN = Math.sqrt(Math.abs(sumg) / array1.length);

        System.out.println("CKO with noise");
        System.out.println("RED="+CKOnRED);
        System.out.println("BLUE="+CKOnBLUE);
        System.out.println("GREEN="+CKOnGREEN);
        System.out.println("CKO filter");

        System.out.println("RED="+CKOfilterRED);
        System.out.println("BLUE="+CKOfilterBLUE);
        System.out.println("GREEN="+CKOfilterGREEN);

        double maxRed1 = 0;
        double maxBlue1 = 0;
        double maxGreen1 = 0;

        for (int i=0; i<array1.length; i++){
            if (red1[i]>maxRed1){
                maxRed1=red1[i];
            }

            if (blue1[i]>maxBlue1){
                maxBlue1 = blue1[i];
            }

            if (green1[i] > maxGreen1){
                maxGreen1 = green1[i];
            }
        }


        double noiseRed = 20*Math.log10(maxRed1/CKOnRED);
        double noiseBlue = 20*Math.log10(maxBlue1/CKOnBLUE);
        double noisegreen = 20*Math.log10(maxGreen1/CKOnGREEN);
        double noRed = 20*Math.log10(maxRed1/CKOfilterRED);
        double noBlue = 20*Math.log10(maxBlue1/CKOfilterBLUE);
        double noGreen = 20*Math.log10(maxGreen1/CKOfilterGREEN);
        System.out.println();
        System.out.println();
        System.out.println("S/N with noise");
        System.out.println("R=" + noiseRed + " B="+noiseBlue+" G="+noisegreen);
        System.out.println("S/N filter");
        System.out.println("R=" + noRed + " B="+noBlue+" G="+noGreen);


        // in GRAYSCALE

        double[] gray1 = new double[1280*720];//clear
        double[] gray2 = new double[1280*720];//filtered
        double[] gray3 = new double[1280*720];//with noise
         for (int i=0; i<array1.length; i++){
            gray1[i] = (red1[i] + blue1[i] + green1[i])/3;
             gray2[i] = (red2[i] + blue2[i] + green2[i])/3;
             gray3[i] = (red3[i] + blue3[i] + green3[i])/3;
        }

        double sum1 = 0;
        double sum2 = 0;

        for (int i = 0; i < array1.length; i++) {
            sum1 += (gray3[i] - gray1[i]) * (gray3[i] - gray1[i]);
            sum2 += (gray2[i] - gray1[i]) * (gray2[i] - gray1[i]);
        }

        double CKOgray1 = Math.sqrt(Math.abs(sum1) / array1.length);
        double CKOgray2 = Math.sqrt(sum2/array1.length);

        System.out.println("CKO with noise gray1 " + CKOgray1);
        System.out.println("CKO no noise gray2 " + CKOgray2);


        double  sn1= 20*Math.log10(255/CKOgray1);
        double sn2= 20*Math.log10(255/CKOgray2);

        System.out.println("Gray noise S/N="+sn1);
        System.out.println("Gray no noise S/N="+sn2);


            double min = v3[0];
            double max = v3[0];
            double m = 0;
            double sum = 0;

            for (int i = 0; i < array1.length; i++) {
                if (v3[i] > max) {
                    max = v3[i];
                }
                if (v3[i] < min) {
                    min = v3[i];
                }
                sum += v3[i];
            }

            m = sum / array1.length;
//
//        noise
//        CKO=0.06250777897360692
//        S/N=20.30019391908933
//
//         median filtering
//        CKO=0.06447722837388517
//        S/N=19.02741587504548

//        ideal
//        CKO=0.12999653891475463
//        S/N=17.72136420774531

            sum = 0;

            for (int i = 0; i < array1.length; i++) {

                sum += (v3[i] - m) * (v3[i] - m);

            }

//            double sn = 10 * Math.log10((max - min) / CKO);

//            System.out.println("CKO=" + CKO);
//            System.out.println("S/N=" + sn);

        }

    }

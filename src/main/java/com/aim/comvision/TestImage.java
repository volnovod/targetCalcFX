package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by root on 19.08.15.
 */
public class TestImage {

    public static void main(String[] args) throws IOException {

//        double[] sigma = {15.81, 16.75, 17.74, 18.79, 19.9, 21.08, 22.33, 23.65, 25.06};

        double[] sigma = new double[19];
        File file = new File("sigma10-1.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int a=0;
        while ((line = reader.readLine() )!= null){
                sigma[a] = Double.valueOf(line);
                a++;
        }

        for (int asd=0; asd< sigma.length; asd++){
            for (int p=0; p<10; p++){


                BufferedImage res = new BufferedImage(640,480, BufferedImage.TYPE_BYTE_GRAY);
                int count = 0;
                double[] imageValues = new double[480 * 640];
                WritableRaster raster = res.getRaster();

                Random random = new Random();
                int black=0;
                int white=0;
                boolean check=true;
                int counter=0;

                for (int i=0; i<480; i++){
                    for (int j=0; j<640; j++){
                        double noise = (random.nextGaussian())*sigma[asd];
                        check=true;
                        if( j>=340 & j<355 & i>=200 & i<215){
                            imageValues[count] = noise+80;
                        } else {
                            imageValues[count] = noise+50;
                        }

                        raster.setSample(j,i,0,imageValues[count]);
                        count++;

                    }
                }

                double[] randomV = new double[640 * 480];
                for (int i=0; i<randomV.length;i++){
                    randomV[i] = (random.nextGaussian())*23.71;
                }

                double sum=0;

                for (int i=0; i<imageValues.length; i++){
                    sum+=imageValues[i];
                }
                double m=sum/imageValues.length;

                sum = 0;
                for (int i=0; i<randomV.length; i++){
                    sum+=(randomV[i]-m)*(randomV[i]-m);
                }

                double CKO = Math.sqrt(sum/randomV.length);

                File image1 = new File("img/step/tester/"+asd +"/tester"+p+".png");
                try {
                    ImageIO.write(res, "png", image1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}

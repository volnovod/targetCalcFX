package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by root on 27.08.15.
 */
public class LFilterfirst {

    public static void main(String[] args) {
        for (int asd =0; asd<19; asd++){

            for (int a1=0; a1<10;a1++){
                File file = new File("img/step/tester/"+asd+"/tester"+a1+".png");
                BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);
                try {
                    image = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WritableRaster raster = image.getRaster();
                double[] values = new double[640 * 480];
                raster.getPixels(0, 0, 640, 480, values);
                double[] newValues = new double[values.length];
                double a = 1;


                double[] window1 = {a, a, a, a, a, a, a, a, a};
                double[] window2 = {1, 1, 1, 1, 2, 1, 1, 1, 1};
                double[] window = new double[9];

                int count;
                int index = 0;

                for (int i = 0; i < 478; i++) {
                    for (int j = 0; j < 638; j++) {
                        count = i * 640 + j;
                        for (int k = 0; k < 3; k++) {
                            for (int l = 0; l < 3; l++) {
                                window[k * 3 + l] = values[count + l];
                                if (k * 3 + l == 4) {
                                    index = count + l;
                                }
                            }
                            count += 640;
                        }


                        count = i * 640 + j;
//                System.out.println(count);
                        double sum = 0;
                        for (int k = 0; k < 3; k++) {
                            for (int l = 0; l < 3; l++) {
                                sum += window[k * 3 + l] * window2[k * 3 + l];

                            }

                        }
                        newValues[index] = sum / 10;


                    }
                }

                BufferedImage out1 = new BufferedImage(640,480, BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster raster1 = out1.getRaster();
                int coun=0;
                for (int i=0; i<480;i++){
                    for (int j=0;j<640;j++){
                        raster1.setSample(j,i,0,newValues[coun]);
                        coun++;
                    }
                }


                File image1 = new File("img/step/lf/"+asd+"/lfilter"+a1+".png");
                try {
                    ImageIO.write(out1, "png", image1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

}

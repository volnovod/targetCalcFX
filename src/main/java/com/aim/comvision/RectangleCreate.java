package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by root on 02.09.15.
 */
public class RectangleCreate {

    public static void main(String[] args) {


        for (int k=4; k<20; k++){

            int width = k;
            int height = k;
            BufferedImage image= new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = image.getRaster();
            Random random = new Random();
//        int[] values = raster.get
            for (int i=0; i<height; i++){
                for (int j=0; j<width; j++){
                    double noise = random.nextGaussian()*0.37;
                    raster.setSample(j,i,0, (150+noise));
                }
            }

            File image1 = new File("img/rect/"+k+"_"+k+".png");
            try {
                ImageIO.write(image, "png", image1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

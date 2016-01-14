package com.aim.comvision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * Created by root on 02.09.15.
 */
public class ImageFounder {

    public static void main(String[] args) throws IOException {

        File fileorx = new File("img/step/absErrCoordX_original.txt");
        FileWriter originalx = new FileWriter(fileorx);

        File filemedx = new File("img/step/absErrCoordX_median.txt");
        FileWriter medianx= new FileWriter(filemedx);

        File filelfx = new File("img/step/absErrCoordX_lf.txt");
        FileWriter lfx = new FileWriter(filelfx);


        File fileory = new File("img/step/absErrCoordY_original.txt");
        FileWriter originaly = new FileWriter(fileory);

        File filemedy = new File("img/step/absErrCoordY_median.txt");
        FileWriter mediany= new FileWriter(filemedy);

        File filelfy = new File("img/step/absErrCoordY_lf.txt");
        FileWriter lfy = new FileWriter(filelfy);
//
//        original.write(String.format("%5s %5s", "Xerr", "Yerr \n"));
//        median.write(String.format("%5s %5s", "Xerr", "Yerr\n"));
//        lf.write(String.format("%5s %5s", "Xerr", "Yerr\n"));
//            original.write(String.format("%3s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s",
//                    "С/Ш,дБ","знайд.X","знайд.Y","відн.пом.X","відн.пом.Y","абс.пом.X,%","абс.пом.Y,%",
//                    "Xмед   ","Yмед   ","відн.пом.X","відн.пом.Y","абс.пом.X,%","абс.пом.Y,%","Xнч   ","Yнч   ","відн.пом.X",
//                    "відн.пом.Y", "абс.пом.X,%","абс.пом.Y,%\r\n"));



//        String string = new String();
//        string+="+-------------------------------------------------------------------------------------------------------------------------------------------------------------------+\n";
//        string+="|С/Ш|знайд.X|знайд.Y|відн.пом.X|відн.пом.Y|абс.пом.X|абс.пом.Y|Xмед|Yмед|відн.пом.X|відн.пом.Y|абс.пом.X|абс.пом.Y|" +
//                "Xнч|Yнч|відн.пом.X|відн.пом.Y|абс.пом.X|абс.пом.Y|\n";
//        string+="+-------------------------------------------------------------------------------------------------------------------------------------------------------------------+\n";

            File file1 = new File("img/rect/0_0.png");

            BufferedImage rect = new BufferedImage(15, 15, BufferedImage.TYPE_BYTE_GRAY);
            try {
                rect = ImageIO.read(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int x=340;
            int y=200;

            int[] sn = {52,30,20,10,6,3,1};


            // file2- original images, file3- median filter, file4- lf
        for (int asd=0; asd<19; asd++){

            double xerror2=0;
            double xerror3=0;
            double xerror4=0;
            double yerror2=0;
            double yerror3=0;
            double yerror4=0;


            for(int p=0; p<10; p++) {


                File file2 = new File("img/step/tester/"+asd+"/tester"+p+".png");
                File file3 = new File("img/step/median/"+asd+"/mfilter"+p+".png");
                File file4 = new File("img/step/lf/"+asd+"/lfilter"+p+".png");
                BufferedImage image2 = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);

                try {
                    image2 = ImageIO.read(file2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedImage image3 = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);

                try {
                    image3 = ImageIO.read(file3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedImage image4 = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);

                try {
                    image4 = ImageIO.read(file4);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WritableRaster rectRastr = rect.getRaster();
                WritableRaster imageRaster2 = image2.getRaster();
                WritableRaster imageRaster3 = image3.getRaster();
                WritableRaster imageRaster4 = image4.getRaster();

                int[] rectValues = new int[15 * 15];
                int[] imageValues2 = new int[640 * 480];
                int[] imageValues3 = new int[640 * 480];
                int[] imageValues4 = new int[640 * 480];

                rectRastr.getPixels(0, 0, 15, 15, rectValues);
                imageRaster2.getPixels(0, 0, 640, 480, imageValues2);
                imageRaster3.getPixels(0, 0, 640, 480, imageValues3);
                imageRaster4.getPixels(0, 0, 640, 480, imageValues4);

                int[] window2 = new int[15 * 15];
                int[] window3 = new int[15 * 15];
                int[] window4 = new int[15 * 15];
                int xIndex2 = 0;
                int yIndex2 = 0;
                double kor2 = 0;

                int xIndex3 = 0;
                int yIndex3 = 0;
                double kor3 = 0;

                int xIndex4 = 0;
                int yIndex4 = 0;
                double kor4 = 0;


                int counter = 0;

                for (int i = 1; i < 464; i++) {
                    for (int j = 1; j < 624; j++) {
                        counter = i * 640 + j;
                        for (int k = 0; k < 15; k++) {
                            for (int l = 0; l < 15; l++) {
                                window2[k * 15 + l] = imageValues2[counter + l];
                                window3[k * 15 + l] = imageValues3[counter + l];
                                window4[k * 15 + l] = imageValues4[counter + l];
                            }
                            counter += 640;
                        }

                        counter = i * 640 + j;
                        double sum2 = 0;
                        double sum3 = 0;
                        double sum4 = 0;
                        for (int k = 0; k < 15; k++) {
                            for (int l = 0; l < 15; l++) {
                                sum2 += window2[k * 15 + l] * imageValues2[counter + l];
                                sum3 += window3[k * 15 + l] * imageValues3[counter + l];
                                sum4 += window4[k * 15 + l] * imageValues4[counter + l];

                            }
                            counter += 640;

                        }
                        if (sum2 > kor2) {
                            kor2 = sum2;
                            xIndex2 = j;
                            yIndex2 = i;
                        }if (sum3 > kor3) {
                            kor3 = sum3;
                            xIndex3 = j;
                            yIndex3 = i;
                        }if (sum4 > kor4) {
                            kor4 = sum4;
                            xIndex4 = j;
                            yIndex4 = i;
                        }

                    }

                }
                int aXerr2=Math.abs(x-xIndex2);
                int aYerr2=Math.abs(y-yIndex2);

                int aXerr3=Math.abs(x-xIndex3);
                int aYerr3=Math.abs(y-yIndex3);

                int aXerr4=Math.abs(x-xIndex4);
                int aYerr4=Math.abs(y-yIndex4);

                xerror2+=aXerr2;
                xerror3+=aXerr3;
                xerror4+=aXerr4;

                yerror2+=aYerr2;
                yerror3+=aYerr3;
                yerror4+=aYerr4;

                int vXerr2 = (100*Math.abs(x-xIndex2)/15);
                int vYerr2 = (100*Math.abs(y-yIndex2)/15);

                int vXerr3 = (100*Math.abs(x-xIndex3)/15);
                int vYerr3 = (100*Math.abs(y-yIndex3)/15);

                int vXerr4 = (100*Math.abs(x-xIndex4)/15);
                int vYerr4 = (100*Math.abs(y-yIndex4)/15);




//                original.write(String.format("%3s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s",
//                        sn[p],xIndex2,yIndex2,vXerr2,vYerr2,aXerr2,aYerr2,
//                        xIndex3,yIndex3,vXerr3,vYerr3,aXerr3,aYerr3,xIndex4,yIndex4,vXerr4,
//                        vYerr4, aXerr4,aYerr4+"\r\n"));



//            string+="| "+(int)sn[p]+" |  "+xIndex2+"  | "+yIndex2+" | "+vXerr2+"  |  "+vYerr2+"  |  "+aXerr2+"  |  "+aYerr2+"  |  "+xIndex3+"  |  "+
//                    yIndex3+"  |  "+vXerr3+"  |  "+vYerr3+"  |  "+aXerr3+"  |  "+aYerr3+"  |  "+xIndex4+"  |  "+yIndex4+"  |  "+vXerr4+"  |  "+vYerr4+"  |  "+
//                    aXerr4+"  |  " + aYerr4+" |\n";
//            string+="+-------------------------------------------------------------------------------------------------------------------------------------------------------------------+\n";


            }

            originalx.write(String.format("%5s", (xerror2/10)+"\n"));
            medianx.write(String.format("%5s", (xerror3/10)+"\n"));
            lfx.write(String.format("%5s", ((xerror4/10))+"\n"));

            originaly.write(String.format("%5s", (yerror2/10)+"\n"));
            mediany.write(String.format("%5s", (yerror3/10)+"\n"));
            lfy.write(String.format("%5s", (yerror4/10)+"\n"));
        }



        originalx.flush();
        originalx.close();
        medianx.flush();
        medianx.close();
        lfx.flush();
        lfx.close();
        originaly.flush();
        originaly.close();
        mediany.flush();
        mediany.close();
        lfy.flush();
        lfy.close();


//        File fileor = new File("result15.txt");
//        try {
//            Writer original = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileor)));
//            original.write(string);
//
//
//            original.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("x="+xIndex+" y="+yIndex);
//        imageRaster.setSample(xIndex, yIndex, 0, 0);
//
//        imageRaster.setSample(xIndex + 1, yIndex, 0, 0);
//        imageRaster.setSample(xIndex-1, yIndex,0,0);
//        imageRaster.setSample(xIndex+2, yIndex,0,0);
//        imageRaster.setSample(xIndex-2, yIndex,0,0);
//        imageRaster.setSample(xIndex+3, yIndex,0,0);
//        imageRaster.setSample(xIndex-3, yIndex,0,0);
//        imageRaster.setSample(xIndex+4, yIndex,0,0);
//        imageRaster.setSample(xIndex-4, yIndex,0,0);
//        imageRaster.setSample(xIndex, yIndex+1,0,0);
//        imageRaster.setSample(xIndex, yIndex-1,0,0);
//        imageRaster.setSample(xIndex, yIndex+2,0,0);
//        imageRaster.setSample(xIndex, yIndex-2,0,0);
//        imageRaster.setSample(xIndex, yIndex+3,0,0);
//        imageRaster.setSample(xIndex, yIndex-3,0,0);
//        imageRaster.setSample(xIndex, yIndex+4,0,0);
//        imageRaster.setSample(xIndex, yIndex-4,0,0);
//        File image1 = new File("img/XY.png");
//        try {
//            ImageIO.write(image, "png", image1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}

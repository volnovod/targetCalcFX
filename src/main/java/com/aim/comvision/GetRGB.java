package com.aim.comvision;

import com.aim.view.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * Created by victor on 02.07.15.
 */
public class GetRGB {

    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private Player player;

    public GetRGB() {
        Player player = new Player();

    }

    // get from image rgb-pixels
    public static void main(String[] args){
        GetRGB getRGB = new GetRGB();
        int[] rgb1 = getRGB.getRGBArray("1.png");
        int[] rgb2 = getRGB.getRGBArray("2.png");
        int[] rgb = new int[rgb1.length];
        for (int i =0; i<rgb1.length; i++){
            rgb[i]=-rgb1[i]-rgb2[i];
        }
        BufferedImage out = getRGB.createImageFromRGBArray(rgb, getRGB.width, getRGB.height);

        File image1 = new File("out3.png");
        try {
            ImageIO.write(out, "png", image1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getRGBArray(String fileName){
        File image = new File(fileName);
        BufferedImage bufferedImage = null;
        try {

            bufferedImage = ImageIO.read(new FileInputStream(image));

        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = bufferedImage.getWidth();
        int height  =bufferedImage.getHeight();
        setHeight(height);
        setWidth(width);
        return  bufferedImage.getRGB(0, 0, width, height, null, 0, width);
    }

    public BufferedImage createImageFromRGBArray(int[] rgb, int width, int height){
        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();

        DataBufferInt buffer = new DataBufferInt(rgb, width*height);
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height,width, cm.getMasks(), null);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }
}

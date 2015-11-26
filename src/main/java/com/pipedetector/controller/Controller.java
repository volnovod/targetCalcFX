package com.pipedetector.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamStorage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.input.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by track on 26.11.15.
 */
public class Controller {

    private Webcam webcam;
    private Integer counter = 0;
    private Integer width;
    private Integer height;
    private double[][] etalonMatrix;
    private int startX;
    private int startY;
    private int lastX;
    private int lastY;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @FXML
    private ImageView imageView;

    @FXML
    private Rectangle rectangle;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    private BufferedImage image;

    static {
        Webcam.setDriver(new IpCamDriver(new IpCamStorage("src/main/resources/cameras.xml")));
    }

    @FXML
    public void init(){

        webcam = Webcam.getWebcams().get(0);
        webcam.open();
        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        setImageView();
                    }
                }, 0, 42);


        imageView.setOnMousePressed(event -> createEtalon(event));
        imageView.setOnMouseReleased(event -> saveEtalon(event));
        imageView.setOnMouseDragged(event -> changeEtalon(event));
    }

    public void createEtalon(MouseEvent event){
        this.startX = (int) event.getX();
        this.startY = (int) event.getY();
        this.rectangle.setX(this.startX);
        this.rectangle.setY(this.startY);
        this.rectangle.setVisible(true);
    }

    public void changeEtalon(MouseEvent event){
        this.lastX = (int) event.getX();
        this.lastY = (int) event.getY();
        this.rectangle.setWidth((lastX-startX));
        this.rectangle.setHeight((lastY-startY));
    }

    public void saveEtalon(MouseEvent event){
        this.rectangle.setVisible(false);
        setWidth((int) this.rectangle.getWidth());
        setHeight((int) this.rectangle.getHeight());
        this.etalonMatrix = new double[getWidth()][getHeight()];
        double[][] fullImage = new double[image.getWidth()][image.getHeight()];

        for (int i=0, k=0; i< image.getWidth(); i++ ){
            for (int j=0, l=0; j<image.getHeight(); j++){
                fullImage[i][j] = image.getRGB(i,j);
                if (i>=startX && i<(startX+getWidth() ) && j>=startY && j<(startY+getHeight())){
                    etalonMatrix[k][l] = fullImage[i][j];
                    l++;
                }
            }
            if (i>=startX && i<(startX+getWidth()-1 )){

                k++;
            }
        }

        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();

        int counter=0;
        int[] buf_arr = new int[getHeight()*getWidth()];
        for (int i = 0; i < getHeight(); i++){
            for (int j=0; j < getWidth(); j++){

                buf_arr[counter] = (int)etalonMatrix[j][i];
                counter++;
            }
        }

        DataBufferInt buffer = new DataBufferInt(buf_arr, getHeight()*getWidth());
        WritableRaster raster = Raster.createPackedRaster(buffer, getWidth(), getHeight(),getWidth(), cm.getMasks(), null);
        BufferedImage out1 =  new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);


        File image1 = new File("gpu.png");
        try {
            ImageIO.write(out1, "png", image1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i=0; i<(image.getHeight()-getHeight()); i++){
            for (int j=0; j<(image.getWidth()-getWidth()); j++){

            }
        }
    }

    public void setImageView(){
        image = webcam.getImage();
        imageView.setImage(SwingFXUtils.toFXImage(image, null));
    }






}

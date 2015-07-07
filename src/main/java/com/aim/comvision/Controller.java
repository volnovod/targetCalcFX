package com.aim.comvision;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 * Created by victor on 06.07.15.
 */
public class Controller {

    private SimpleDoubleProperty startX = new SimpleDoubleProperty();
    private SimpleDoubleProperty startY = new SimpleDoubleProperty();
    private SimpleDoubleProperty stopX = new SimpleDoubleProperty();
    private SimpleDoubleProperty stopY = new SimpleDoubleProperty();


    public double getStartX() {
        return startX.get();
    }

    public SimpleDoubleProperty startXProperty() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX.set(startX);
    }

    public double getStartY() {
        return startY.get();
    }

    public SimpleDoubleProperty startYProperty() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY.set(startY);
    }

    public double getStopX() {
        return stopX.get();
    }

    public SimpleDoubleProperty stopXProperty() {
        return stopX;
    }

    public void setStopX(double stopX) {
        this.stopX.set(stopX);
    }

    public double getStopY() {
        return stopY.get();
    }

    public SimpleDoubleProperty stopYProperty() {
        return stopY;
    }

    public void setStopY(double stopY) {
        this.stopY.set(stopY);
    }

    private Rectangle rectangle;

    @FXML
    private ImageView imageView;

    @FXML
    private Pane pane;

    @FXML
    public void move(){
    }

    private double imageStartX;
    private double imageStartY;
    private double width;
    private double height;

    @FXML
    public void drag(MouseEvent event){
        setStopX(event.getSceneX());
        setStopY(event.getSceneY());

    }

    @FXML
    public void press(MouseEvent event){
        setStartX(event.getSceneX());
        setStartY(event.getSceneY());
        setStopX(event.getSceneX());
        setStopY(event.getSceneY());
        rectangle.setX(getStartX());
        rectangle.setY(getStartY());
        imageStartX = event.getX();
        imageStartY = event.getY();
    }

    @FXML
    public void release(MouseEvent event) {
        setStopX(event.getSceneX());
        setStopY(event.getSceneY());
        Rectangle rect = getRect();
        rect.setX(rectangle.getX());
        rect.setY(rectangle.getY());
        rect.setWidth(rectangle.getWidth());
        rect.setHeight(rectangle.getHeight());
        pane.getChildren().add(rect);

        stopX.set(0);
        stopY.set(0);
        width = rect.getWidth();
        height = rect.getHeight();

        setImagePixels();
    }


    public Rectangle getRect(){
        Rectangle rectangle  = new Rectangle();
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.AQUA);
        return rectangle;
    }

    public void init(){
        rectangle = getRect();
        rectangle.widthProperty().bind(stopX.subtract(startX));
        rectangle.heightProperty().bind(stopY.subtract(startY));
        pane.getChildren().add(rectangle);
    }

    public void setImagePixels(){

        Image image = imageView.getImage();
        PixelReader pixelReader = image.getPixelReader();

        int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        double kw = width/imageView.getFitWidth();
        double kh = height/imageView.getFitHeight();

        int[][] buffer = new  int[height][width];
        for (int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                buffer[i][j] = pixelReader.getArgb(j,i);
            }
        }

        int[][] transBuff = new int[height][width];
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                if(i>=imageStartY*kh && i<=(imageStartY*kh+this.height*kh) && j>=imageStartX*kh && j<=(imageStartX*kh+this.width*kh)){
                    transBuff[i][j] = 1000;
                }else{
                    transBuff[i][j] = buffer[i][j];
                }
            }
        }
        WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                pixelWriter.setArgb(j,i,transBuff[i][j]);
            }
        }

        imageView.setImage(writableImage);

    }
}

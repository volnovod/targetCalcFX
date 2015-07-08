package com.aim.comvision;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

/**
 * Created by victor on 08.07.15.
 */
public class MedianFilter extends Application {

    public MedianFilter() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        File file = new File("/home/victor/Java/workFX/2.png");
        Image image = new Image(file.toURI().toString());

        PixelReader pixelReader = image.getPixelReader();

        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        int[][] buffer = new  int[height][width];
        for (int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                buffer[i][j] = pixelReader.getArgb(j,i);
            }
        }


        ImageView imageView = new ImageView(image);

        int[][] transBuff = getMedian(buffer);
        WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                pixelWriter.setArgb(j,i,transBuff[i][j]);
            }
        }

        imageView.setImage(writableImage);
        StackPane root = new StackPane();
        Scene scene = new Scene(root,1300,700);

        root.getChildren().add(imageView);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public void filter(int[] pixelsArray, int width, int height){

        int counter = 0;
        int[][] pixels = vectorToArray(pixelsArray, width, height);



    }

    public int[][] getMedian(int[][] inputPixels){
        int height = inputPixels.length;
        int width = inputPixels[0].length;

        int[][] window = new int[3][3];

        int[] vectorFromWindow = new int[9];

        for(int i=0; i < height-2; i++){
            for (int j = 0; j<width-2; j++){

                //k-rows, l-cols
                for(int k=0; k<3; k++){
                    for (int l=0; l<3; l++){
                        window[k][l] = inputPixels[i+k][j+l];
                    }
                }
                vectorFromWindow = arrayToVector(window);
                Arrays.sort(vectorFromWindow);
                inputPixels[i+1][j+1] = vectorFromWindow[4];
            }
        }


        return inputPixels;
    }

    public int[][] vectorToArray(int[] input, int width, int height){
        int counter = 0;
        int[][] out = new int[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                out[i][j] = input[counter];
                counter++;
            }
        }
        return out;
    }

    public int[] arrayToVector(int[][] input){
        int[] out = new int[input.length*input[0].length];
        int counter = 0;
        for (int i = 0; i< input.length; i++){
            for (int j=0; j< input[0].length; j++){
                out[counter] = input[i][j];
                counter++;
            }
        }
        return out;
    }

    public static void main(String[] args) {
        launch(args);
    }

}

package com.aim.comvision;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;


/**
 * A line chart demonstrating a CategoryAxis implementation.
 */
public class CategoryLineChartApp extends Application {




    public Parent createContent() throws IOException {

        File sn= new File("signalnoise.txt");

        File orig = new File("img/step/absErrCoordY_original.txt");
        File med = new File("img/step/absErrCoordY_median.txt");
        File lf = new File("img/step/absErrCoordY_lf.txt");

        BufferedReader SNreader = new BufferedReader(new FileReader(sn));
        BufferedReader origreader = new BufferedReader(new FileReader(orig));
        BufferedReader medreader = new BufferedReader(new FileReader(med));
        BufferedReader lfreader = new BufferedReader(new FileReader(lf));

        String snoise;
        double[] SN = new double[19];
        int counter=0;
        while((snoise=SNreader.readLine())!=null){
            SN[counter] = Double.valueOf(snoise);
            counter++;
        }

        String orValues;
        double[] original= new double[19];
        counter=0;
        while((orValues=origreader.readLine())!=null){
            double val = Double.valueOf(orValues);
            if (val<=16){
                original[counter] = val;
            }
            else {
                original[counter] = 480;
            }
            counter++;
        }

        String[] ORIGINALCATEGORIES = new String[original.length];
        for(int i=0; i<original.length;i++) {
            ORIGINALCATEGORIES[i] = String.valueOf(SN[i]);
        }

        String medValues;
        double[] median= new double[19];
        counter=0;
        while((medValues=medreader.readLine())!=null){
            double val = Double.valueOf(medValues);
            if (val<=16){

                median[counter] = val ;
            }else {
                median[counter] = 480;
            }

            counter++;
        }

        String lfValues;
        double[] lowf= new double[19];
        counter=0;
        while((lfValues=lfreader.readLine())!=null){
            double val = Double.valueOf(lfValues);
            if (val<=16){

                lowf[counter] = val;
            } else {
                lowf[counter] = 480;
            }
            counter++;
        }


        LineChart<String, Number> chart;
        CategoryAxis xAxis;
        NumberAxis yAxis;

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        // setup chart
        chart.setTitle("Залежність абсолютної похибки по координаті Y від С/Ш");
        xAxis.setLabel("С/Шб, дБ");
        yAxis.setLabel("Абсолютна помилка");
        // add starting data
        XYChart.Series<String, Number> originalSeries = new XYChart.Series<>();
        originalSeries.setName("Оригінальне фото");
        for (int i=0; i<original.length; i++){

            originalSeries.getData().add(new XYChart.Data<String, Number>(ORIGINALCATEGORIES[i], original[i]));
        }
        chart.getData().add(originalSeries);

        XYChart.Series<String, Number> medianSeries = new XYChart.Series<>();
        medianSeries.setName("Медіанний фільтр");
        for (int i=0; i<median.length; i++){

            medianSeries.getData().add(new XYChart.Data<String, Number>(ORIGINALCATEGORIES[i], median[i]));
        }
        chart.getData().add(medianSeries);

        XYChart.Series<String, Number> lfSeries = new XYChart.Series<>();
        lfSeries.setName("Фільтр нижніх частот");
        for (int i=0; i<lowf.length; i++){

            lfSeries.getData().add(new XYChart.Data<String, Number>(ORIGINALCATEGORIES[i], lowf[i]));
        }
        chart.getData().add(lfSeries);
        return chart;
    }

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}

package com.aim.comvision;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import jcuda.Pointer;
import jcuda.jcufft.JCufft;
import jcuda.jcufft.cufftHandle;
import jcuda.jcufft.cufftType;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaMemcpyKind;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * A chart that displays rectangular bars with heights indicating data values
 * for categories. Used for displaying information when at least one axis has
 * discontinuous or discrete data.
 */
public class BarChartApp extends Application {

    private BarChart chart;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    float[] inputData;
    double[] fftres;
    BufferedImage image;

    public Parent createContent() {

        setImage("/home/victor/Java/workFX/img/median-f5.png");
        inputData = new float[512*256];
        setArrayData();
        System.out.println("fft start");
        long start = new Date().getTime();
        fftres = jcudaTransformDeviceMemory(inputData);
        System.out.println("fft stop" + (new Date().getTime()-start)/1000.0);
        Map<Integer, Integer> values = new TreeMap<>();
        int key=0;
        int val= 0;

        int q=80;

        for (int i=0; i<fftres.length; i++){
            key = (int)(fftres[i]/q);
            if (values.containsKey(key)){
                val = values.get(key);
                values.remove(key);
                val++;
                values.put(key, val);
            } else {
                values.put(key,1);
            }
        };
        System.out.println("map stop");
        String[] colors = new String[values.size()];
        List<BarChart.Data> dataList = new ArrayList<>();
        int counter = 0;
        for (Map.Entry el: values.entrySet()){
            dataList.add(new BarChart.Data(String.valueOf(el.getKey()), el.getValue()));
            colors[counter] = String.valueOf(el.getKey());
            counter++;
        }
        xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(colors));
        yAxis = new NumberAxis("Кількість пікселів", 0.0d, 2200.0d, 100.0d);
        ObservableList<BarChart.Data> list = FXCollections.observableList(dataList);
        ObservableList<BarChart.Series> barChartData = FXCollections.observableArrayList(new BarChart.Series("Значення Пікселів", FXCollections.observableArrayList(dataList)));

        chart = new BarChart(xAxis, yAxis, barChartData, 0.0d);

        double p = q*Math.sqrt((512*256-1)/(Math.pow((512*256),2)));
        System.out.println(p);

        return chart;
    }

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent(), 1360,700));
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void setImage(String url){
        File file1 = new File(url);
        image= new BufferedImage(512, 256, BufferedImage.TYPE_BYTE_GRAY);
        try {

            image = ImageIO.read(new FileInputStream(file1));

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void setArrayData(){
        image.getRaster().getPixels(64,112,512,256,inputData);
    }

    public static double[] jcudaTransformDeviceMemory(float[] inputData){

        float[] fftResults = new float[inputData.length + 2];

        // указатель на устройство
        Pointer deviceDataIn = new Pointer();
        // выделение памяти на видеокарте для входных данных
        JCuda.cudaMalloc(deviceDataIn, inputData.length * 4);
        // копирование входных данных в память видеокарты
        JCuda.cudaMemcpy(deviceDataIn, Pointer.to(inputData), inputData.length * 4,
                cudaMemcpyKind.cudaMemcpyHostToDevice);

        Pointer deviceDataOut = new Pointer();
        // выделение памяти на видеокарте для результатов преобразования
        JCuda.cudaMalloc(deviceDataOut, fftResults.length * 4);

        // создание плана
        cufftHandle plan = new cufftHandle();
        JCufft.cufftPlan1d(plan, inputData.length, cufftType.CUFFT_R2C, 1);

        // выполнение БПФ
        JCufft.cufftExecR2C(plan, deviceDataIn, deviceDataOut);

        // копирование результатов из памяти видеокарты в оперативную память
        JCuda.cudaMemcpy(Pointer.to(fftResults), deviceDataOut, fftResults.length * 4,
                cudaMemcpyKind.cudaMemcpyDeviceToHost);

        // освобождение ресурсов
        JCufft.cufftDestroy(plan);
        JCuda.cudaFree(deviceDataIn);
        JCuda.cudaFree(deviceDataOut);
        return cudaComplexToDouble(fftResults);
    }

    public static double[] cudaComplexToDouble(float[] complexData){

        double[] result = new double[complexData.length/2];
        int j=0;
        for(int i=0; i < complexData.length-1; i++) {
            result[j++] = Math.sqrt(complexData[i]*complexData[i] + complexData[i+1]*complexData[i+1]);
            i++;
        }

        return result;
    }
}

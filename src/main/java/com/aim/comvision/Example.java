package com.aim.comvision;

import jcuda.Pointer;
import jcuda.jcufft.*;
import jcuda.runtime.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;


import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;

import javax.imageio.ImageIO;

/**
 * Created by victor on 24.06.15.
 */
public class Example {


     static BufferedImage image;
     static float[] inputData;

    public static void setImage(String url){
        File file1 = new File(url);
        image= new BufferedImage(1024, 512, BufferedImage.TYPE_BYTE_GRAY);
        try {

            image = ImageIO.read(new FileInputStream(file1));

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void setArrayData(){
        image.getRaster().getPixels(128,104,1024,512,inputData);
    }


    public static void main(String[] args) throws Exception{
//
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("rtsp://admin:12345@192.168.1.64//rtsp_tunnel");
//
//        grabber.start();
//        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
//        opencv_core.IplImage iplImage = converter.convert(grabber.grab());
//        CanvasFrame frame = new CanvasFrame("Video", CanvasFrame.getDefaultGamma()/grabber.getGamma());


        double[] fftResults;
        inputData = new float[1024*512];
        setImage("/home/victor/Java/workFX/img/testNoise21.png");
        setArrayData();
        System.out.println("1D БПФ с использованием apache commons math...");
        fftResults = commonsTransform(floatDataToDoubleData(inputData.clone()));
        printSomeValues(fftResults);

        System.out.println();
        System.out.println("1D БПФ JCufft (данные в оперативной памяти)...");
        fftResults = jcudaTransformHostMemory(inputData.clone());
        printSomeValues(fftResults);

        System.out.println();
        System.out.println("1D БПФ JCufft (данные в памяти видеокарты)...");
        long start = new Date().getTime();
        fftResults = jcudaTransformDeviceMemory(inputData.clone());
        System.out.println("time"+(new Date().getTime()-start)/1000.0);
        printSomeValues(fftResults);

    }

    public static float[] createRandomData(int dataSize){
        Random random = new Random();
        float data[] = new float[dataSize];

        for (int i = 0; i < dataSize; i++)
            data[i] = random.nextFloat();

        return data;
    }

    public static double[] floatDataToDoubleData(float[] data){

        double[] doubleData = new double[data.length];
        for(int i=0; i < data.length; i++) doubleData[i] = data[i];

        return doubleData;
    }

    public static double[] jcudaTransformHostMemory(float[] inputData){
        float[] fftResults = new float[inputData.length + 2];
        long timeStart = new Date().getTime();
        // создание плана
        cufftHandle plan = new cufftHandle();
        JCufft.cufftPlan1d(plan, inputData.length, cufftType.CUFFT_R2C, 1);
        // выполнение БПФ
        JCufft.cufftExecR2C(plan, inputData, fftResults);
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart)/1000.0+" сек");
        // уничтожение плана
        JCufft.cufftDestroy(plan);

        return cudaComplexToDouble(fftResults);
    }

    public static double[] jcudaTransformDeviceMemory(float[] inputData){

        float[] fftResults = new float[inputData.length + 2];

        long timeStart = new Date().getTime();
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
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart) / 1000.0 + " сек");
        JCuda.cudaMemcpy(Pointer.to(fftResults), deviceDataOut, fftResults.length * 4,
                cudaMemcpyKind.cudaMemcpyDeviceToHost);

        // освобождение ресурсов
        JCufft.cufftDestroy(plan);
        JCuda.cudaFree(deviceDataIn);
        JCuda.cudaFree(deviceDataOut);

        return cudaComplexToDouble(fftResults);
    }

    public static double[] commonsTransform(double[] inputData){

        FastFourierTransformer fft = new FastFourierTransformer();
        long timeStart = new Date().getTime();
        Complex[] cmx = fft.transform(inputData);
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart)/1000.+" сек");

        double[] fftReults = new double[inputData.length/2 + 1];
        for(int i = 0; i < fftReults.length; i++){
            fftReults[i] = cmx[i].abs();
        }

        return fftReults;
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

    public static void printSomeValues(double[] data){
        System.out.println("[0]: "+data[0]);
        System.out.println("["+(data.length/2)+"]: "+data[data.length/2]);
        System.out.println("["+(data.length-1)+"]: "+data[data.length-1]);
    }
}

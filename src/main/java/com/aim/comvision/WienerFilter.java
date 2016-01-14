package com.aim.comvision;

import jcuda.Pointer;
import jcuda.jcufft.JCufft;
import jcuda.jcufft.cufftHandle;
import jcuda.jcufft.cufftType;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaMemcpyKind;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by root on 18.08.15.
 */
public class WienerFilter {

    public static void main(String[] args) {
        float[] h = new WienerFilter().getH(720,1280);

        File file1 = new File("/home/victor/Java/workFX/img/testNoise2.png");
        BufferedImage bufferedImage1 = null;
        try {

            bufferedImage1 = ImageIO.read(new FileInputStream(file1));

        } catch (IOException e) {
            e.printStackTrace();

        }

        WritableRaster raster = bufferedImage1.getRaster();



        float[] gray1 = new float[1280 * 720];//clear
        raster.getPixels(0,0,1280,720, gray1);

        double K=5;

        // create G(u,v) matrix from wiener filtering

        float[] g = new WienerFilter().jcudaTransformDeviceMemory(gray1);

        float[] h2 = new float[1280 * 720];
        for (int i=0; i<h2.length; i++){
            h2[i] = (float) Math.pow(h[i], 2);
        }

        float[] fshapka = new float[1280 * 720];

        for (int i=0; i<fshapka.length; i++){
            fshapka[i] = (float) ((((1/h[i]) * (h2[i]/(h2[i]+K))))*g[i]);
        }

        fshapka = new WienerFilter().jcudaInverseTransformDeviceMemory(fshapka);

        BufferedImage res = new BufferedImage(1280,720, BufferedImage.TYPE_BYTE_GRAY);
        int count1=0;
        byte[] imageValues = ((DataBufferByte)res.getRaster().getDataBuffer()).getData();
        for (int i=0; i<720; i++){
            for (int j=0; j<1280; j++){
                raster.setSample(j,i,0,(int)fshapka[count1]);
                count1++;
            }

        }

        File image1 = new File("img/wiener.png");
        try {
            ImageIO.write(res, "png", image1);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        int count=0;
//        for ( int i=0; i<720;i++){
//            for (int j=0;j<1280; j++ ){
//                System.out.print(fshapka[count] + " ");
//                count++;
//            }
//            System.out.println();
//        }

    }

    public double[] idft(double[] input){
        double[] out = new double[1280*720];

        System.out.println("Start inverse ft");
        int N = out.length;
        Complex[] in = new Complex[N];
        for (int i=0; i<N; i++){
            in[i] = new Complex(input[i], 0);
        }

        Complex[] temp = new Complex[N];

        for (int n=0; n<N; n++){
            Complex sum = new Complex(0,0);
            for (int k=0; k<N; k++){
                sum.add(in[k].multiply(Math.exp(2*Math.PI*Math.sqrt(-1)*k*n/N))) ;
            }
            temp[n] = sum.divide(new Complex(N, 0));

        }

        for (int i=0; i<N; i++){
            out[i] = temp[i].abs();
        }
        System.out.println("Stop idft");
        return out;

    }

    public double[] fft(double[] inputData){
        int N=inputData.length;
        double[] outData = new double[N];
        System.out.println("Start fft");
        Complex[] in = new Complex[N];
        for (int i=0; i<N; i++){
            in[i] = new Complex(inputData[i], 0);
        }

        Complex[] temp = new Complex[N];

        for (int k=0; k<N; k++){
            Complex sum = new Complex(0,0);
            for (int n=0; n<N; n++){
                System.out.println("k="+k+" n="+n);
                sum.add(in[k].multiply(Math.exp(-2*Math.PI*Math.sqrt(-1)*k*n)));
            }
            temp[k] = sum;
        }

        for (int k=0; k<N; k++){
            outData[k] = temp[k].abs();
        }
        System.out.println("Stop fft");
        return outData;
    }

    public static float[] jcudaTransformDeviceMemory(float[] inputData){

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

        return fftResults;
    }

    public static float[] jcudaInverseTransformDeviceMemory(float[] inputData){

        float[] ifftResults = new float[inputData.length + 2];

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
        JCuda.cudaMalloc(deviceDataOut, ifftResults.length * 4);

        // создание плана
        cufftHandle plan = new cufftHandle();
        JCufft.cufftPlan1d(plan, inputData.length, cufftType.CUFFT_C2R, 1);

        // выполнение БПФ
        JCufft.cufftExecR2C(plan, deviceDataIn, deviceDataOut);

        // копирование результатов из памяти видеокарты в оперативную память
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart) / 1000.0 + " сек");
        JCuda.cudaMemcpy(Pointer.to(ifftResults), deviceDataOut, ifftResults.length * 4,
                cudaMemcpyKind.cudaMemcpyDeviceToHost);

        // освобождение ресурсов
        JCufft.cufftDestroy(plan);
        JCuda.cudaFree(deviceDataIn);
        JCuda.cudaFree(deviceDataOut);

        return ifftResults;
    }

    public float[] getH(int height, int width){
        float[] h = new float[width * height];
        int counter=0;
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                h[counter] = (float) Math.exp(-0.0025*Math.pow((i*i+j*j), 0.83));
                counter++;
            }
        }
        return h;
    }

}

package com.aim.view;


import jcuda.Pointer;
import jcuda.jcufft.JCufft;
import jcuda.jcufft.cufftHandle;
import jcuda.jcufft.cufftType;
import jcuda.runtime.*;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;

import java.util.Date;
import java.util.Random;

/**
 * Created by victor on 26.06.15.
 */
public class ExamGpu {




    public static void main(String[] args) {

        double[] fftResults;
        int dataSize = 1<<23;

        System.out.println("Генерация входных данных размером "+dataSize+" значений...\n");
        float[] inputData = createRandomData(dataSize);

        System.out.println();
        System.out.println("1D БПФ JCufft (данные в оперативной памяти)...");
        fftResults = jcudaTransformHostMemory(inputData.clone());
        printSomeValues(fftResults);

        System.out.println();
        System.out.println("1D БПФ JCufft (данные в памяти видеокарты)...");
        fftResults = jcudaTransformDeviceMemory(inputData.clone());
        printSomeValues(fftResults);
    }

    /**
     * Генерирует массив случайных чисел
     *
     * @param dataSize - размер генерируемого массива
     * @return массив случайных чисел
     */
    public static float[] createRandomData(int dataSize){
        Random random = new Random();
        float data[] = new float[dataSize];

        for (int i = 0; i < dataSize; i++)
            data[i] = random.nextFloat();

        return data;
    }

    /**
     * Конвертирует массив значений типа float в массив значений double
     *
     * @param data - массив который нужно конвертировать
     * @return - сконвертированный массив
     */
    public static double[] floatDataToDoubleData(float[] data){

        double[] doubleData = new double[data.length];
        for(int i=0; i < data.length; i++) doubleData[i] = data[i];

        return doubleData;
    }

    /**
     * Выполняет БПФ массива значений с помощью CUDA, осуществляя
     * операции с данными в оперативной памяти
     *
     * @param inputData - массив входных значений
     * @return массив с результатами БПФ
     */
    public static double[] jcudaTransformHostMemory(float[] inputData){
        float[] fftResults = new float[inputData.length + 2];
        // создание плана
        cufftHandle plan = new cufftHandle();
        JCufft.cufftPlan1d(plan, inputData.length, cufftType.CUFFT_R2C, 1);
        // выполнение БПФ
        long timeStart = new Date().getTime();
        JCufft.cufftExecR2C(plan, inputData, fftResults);
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart)/1000.0+" сек");
        // уничтожение плана
        JCufft.cufftDestroy(plan);

        return cudaComplexToDouble(fftResults);
    }

    /**
     * Выполняет БПФ массива значений с помощью CUDA, осуществляя
     * операции с данными в памяти видеокарты
     *
     * @param inputData - массив входных значений
     * @return массив с результатами БПФ
     */
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
        long timeStart = new Date().getTime();
        JCufft.cufftExecR2C(plan, deviceDataIn, deviceDataOut);
        System.out.println("Время преобразования: " + (new Date().getTime() - timeStart)/1000.+" сек");

        // копирование результатов из памяти видеокарты в оперативную память
        JCuda.cudaMemcpy(Pointer.to(fftResults), deviceDataOut, fftResults.length * 4,
                cudaMemcpyKind.cudaMemcpyDeviceToHost);

        // освобождение ресурсов
        JCufft.cufftDestroy(plan);
        JCuda.cudaFree(deviceDataIn);
        JCuda.cudaFree(deviceDataOut);

        return cudaComplexToDouble(fftResults);
    }

    /**
     * Выполняет БПФ массива значений с помощью Apache Commons Math
     *
     * @param inputData - массив входных значений
     * @return массив с результатами БПФ
     */
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

    /**
     * Метод осуществляет преобразование массива комплексных чисел в
     * массив, содержащий их модули
     *
     * @param complexData - массив комплексных чисел
     * @return массив модулей комплексных чисел
     */
    public static double[] cudaComplexToDouble(float[] complexData){

        double[] result = new double[complexData.length/2];
        int j=0;
        for(int i=0; i < complexData.length-1; i++) {
            result[j++] = Math.sqrt(complexData[i]*complexData[i] + complexData[i+1]*complexData[i+1]);
            i++;
        }

        return result;
    }

    /**
     * Выводит на стандартный вывод первое, среднее
     * и последнее значения массива
     *
     * @param data - массив, значения которого необходимо вывести
     */
    public static void printSomeValues(double[] data){
        System.out.println("[0]: "+data[0]);
        System.out.println("["+(data.length/2)+"]: "+data[data.length/2]);
        System.out.println("["+(data.length-1)+"]: "+data[data.length-1]);
    }

}

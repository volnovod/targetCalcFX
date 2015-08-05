package com.aim.comvision;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static jcuda.driver.JCudaDriver.*;
import static jcuda.driver.JCudaDriver.cuMemFree;

/**
 * Created by victor on 05.08.15.
 */
public class OptimizFilter {

    public static void main(String[] args) {

//        long[] in_array ={1,22,3,4,5,63,7,38,9,10,1,23,3,43,5,63,7,8,9,10,1,2,33,4,5,63,7,8,9,310,1,2,3,43,5,6,7,8,9,10,
//                13,42,3,4,5,6,7,8,9,1010,1,23,3,4,53,6,744,8,94,10};
//        int threads = 6;
//        int size = 10;

        File file = new File("/home/victor/Java/workFX/6.jpg");
        BufferedImage bufferedImage = null;
        try {

            bufferedImage = ImageIO.read(new FileInputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }


        int threads = bufferedImage.getHeight();
        int size = bufferedImage.getWidth();

        int[] int_array = bufferedImage.getRGB(0,0,size, threads, null, 0, size);
        long[] o_array = new long[size * threads];

        long[] in_array = new long[int_array.length];
        for (int i = 0; i<int_array.length; i++){
            in_array[i] = int_array[i];
        }
        int numElements = threads * size;

        int blockSizeX = 16;
        int blockSizeY = 16;
        int gridSizeX = (int)Math.ceil((double)size / blockSizeX);
        int gridSizeY = (int)Math.ceil((double)threads/ blockSizeY );

        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext pctx = new CUcontext();
        cuCtxCreate(pctx, 0, device);

        CUmodule module = new CUmodule();
        cuModuleLoad(module, "/home/victor/Java/workFX/src/main/java/com/aim/comvision/optimizkernel.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "filter");


        long start = new Date().getTime();
        CUdeviceptr input = new CUdeviceptr();
        CUdeviceptr out = new CUdeviceptr();

        cuMemAlloc(input, size * threads * Sizeof.LONG);

        cuMemcpyHtoD(input, Pointer.to(in_array), size * threads * Sizeof.LONG);

        cuMemAlloc(out, size * threads * Sizeof.LONG);

        Pointer kernelParam = Pointer.to(Pointer.to(input),
                Pointer.to(out),
                Pointer.to(new int[]{size}),
                Pointer.to(new int[]{threads}));
        cuLaunchKernel(function,
                gridSizeX, gridSizeY, 1,      // Grid dimension
                blockSizeX, blockSizeY, 1,      // Block dimension
                0, null,               // Shared memory size and stream
                kernelParam, null // Kernel- and extra parameters
        );
        System.out.println("time " + (new Date().getTime()-start)/1.0 + " ms");
        cuMemcpyDtoH(Pointer.to(o_array), out, size * threads * Sizeof.LONG);

        cuMemFree(input);
        cuMemFree(out);
        int count1=0;


        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();

        int[] buf_arr = new int[o_array.length];
        for (int i = 0; i < o_array.length; i++){
            buf_arr[i] = (int)o_array[i];
        }

        DataBufferInt buffer = new DataBufferInt(buf_arr, size*threads);
        WritableRaster raster = Raster.createPackedRaster(buffer, size, threads,size, cm.getMasks(), null);
        BufferedImage out1 =  new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);


        File image1 = new File("gpu.png");
        try {
            ImageIO.write(out1, "png", image1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

package com.aim.comvision;

import javafx.scene.image.Image;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.cudaEvent_t;

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

    public long[] getArrayFromImage(Image image){

        if (image == null){
            return null;
        }

        long[] outArray = new long[(int) (image.getHeight() * image.getWidth())];
        int iterator = 0;
        for (int i=0; i < image.getHeight(); i++){
            for (int j=0; j < image.getWidth(); j++){
                outArray[iterator] =  image.getPixelReader().getArgb(j,i);
                iterator++;
            }
        }
        return outArray;
    }

    public static void main(String[] args) {

        File file = new File("/home/victor/Java/workFX/lena_noiseImg.jpg");
        BufferedImage bufferedImage = null;
        try {

            bufferedImage = ImageIO.read(new FileInputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }



        int threads = bufferedImage.getHeight();
        int size = bufferedImage.getWidth();

        int[] int_array = bufferedImage.getRGB(0,0,size, threads, null, 0, size);
        int[] o_array = new int[size * threads];


        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext pctx = new CUcontext();
        cuCtxCreate(pctx, 0, device);

        CUmodule module = new CUmodule();
        cuModuleLoad(module, "/home/victor/Java/workFX/src/main/java/com/aim/comvision/sharedFilter.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "filter");



        CUdeviceptr input = new CUdeviceptr();
        CUdeviceptr out = new CUdeviceptr();

        cuMemAlloc(input, size * threads * Sizeof.INT);

        long start = new Date().getTime();
        cuMemcpyHtoD(input, Pointer.to(int_array), size * threads * Sizeof.INT);

        cuMemAlloc(out, size * threads * Sizeof.INT);

        int blockSizeX = 16;
        int blockSizeY = 16;
        int gridSizeX = (int)Math.ceil((double)size / blockSizeX);
        int gridSizeY = (int)Math.ceil((double)threads/ blockSizeY );

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
        cuMemcpyDtoH(Pointer.to(o_array), out, size * threads * Sizeof.INT);
        System.out.println("time " + (new Date().getTime() - start) / 1.0 + " ms");


        cuMemFree(input);
        cuMemFree(out);
        int count1=0;


        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();


        DataBufferInt buffer = new DataBufferInt(o_array, size*threads);
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

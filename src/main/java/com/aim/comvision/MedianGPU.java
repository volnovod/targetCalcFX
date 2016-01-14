package com.aim.comvision;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static jcuda.driver.JCudaDriver.*;

/**
 * Created by victor on 21.07.15.
 */
public class MedianGPU {

    public static void main(String[] args) {
        for (int asd=0; asd<19;asd++ ){
            for (int a =0; a< 10; a++){
                File file = new File("/home/victor/Java/workFX/img/step/tester/"+asd+"/tester"+a+".png");
                BufferedImage bufferedImage = new BufferedImage(640,480, BufferedImage.TYPE_BYTE_GRAY);
                try {

                    bufferedImage = ImageIO.read(new FileInputStream(file));

                } catch (IOException e) {
                    e.printStackTrace();
                }


                int threads = bufferedImage.getHeight();
                int size = bufferedImage.getWidth();


                cuInit(0);
                CUdevice device = new CUdevice();
                cuDeviceGet(device, 0);
                CUcontext pctx = new CUcontext();
                cuCtxCreate(pctx, 0, device);

                CUmodule module = new CUmodule();
                cuModuleLoad(module, "src/main/java/com/aim/comvision/optimizkernel.ptx");
                CUfunction function = new CUfunction();
                cuModuleGetFunction(function, module, "filter");

                CUdeviceptr input = new CUdeviceptr();
                CUdeviceptr out = new CUdeviceptr();

                cuMemAlloc(input, size * threads* Sizeof.INT);


                cuMemAlloc(out, size * threads* Sizeof.INT);

                int blockSizeX = 16;
                int blockSizeY = 16;
                int gridSizeX = (int) Math.ceil((double) size / blockSizeX);
                int gridSizeY = (int) Math.ceil((double) threads / blockSizeY);


                int[] o_array = new int[threads * size];


                WritableRaster raster = bufferedImage.getRaster();
                int[] int_array = new int[640*480];
                raster.getPixels(0,0,640,480,int_array);
                cuMemcpyHtoD(input, Pointer.to(int_array), size * threads * Sizeof.INT);
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
                cuMemFree(input);
                cuMemFree(out);

                BufferedImage out1 = new BufferedImage(640,480, BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster raster1 = out1.getRaster();
                int count=0;
                for (int i=0; i<480;i++){
                    for (int j=0;j<640;j++){
                        raster1.setSample(j,i,0,o_array[count]);
                        count++;
                    }
                }


                File image1 = new File("img/step/median/"+asd+"/mfilter"+a+".png");
                try {
                    ImageIO.write(out1, "png", image1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }





    }
}

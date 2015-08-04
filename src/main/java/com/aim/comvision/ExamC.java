package com.aim.comvision;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import static jcuda.driver.JCudaDriver.*;

/**
 * Created by victor on 17.07.15.
 */
public class ExamC {

    public static void main(String[] args) {
        int[][] a = {{1,100,2},
                     {1,200,3},
                     {1,300,4}};

        int[][] b = {{1,400,2},
                {1,500,3},
                {1,600,4}};

        int threads = 3;
        int size = 3;

        int[][] c = new int[threads][size];

        cuInit(0);
        CUcontext pctx = new CUcontext();
        CUdevice dev  = new CUdevice();
        cuDeviceGet(dev, 0);
        cuCtxCreate(pctx, 0, dev);

        CUmodule module=new CUmodule();
        cuModuleLoad(module, "/home/victor/Java/workFX/src/main/java/com/aim/comvision/exam.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "example");

        CUdeviceptr a_dev[] = new CUdeviceptr[threads];
        CUdeviceptr b_dev[] = new CUdeviceptr[threads];

        // allocate memory on gpu
        for (int i = 0; i< threads; i++){

            a_dev[i] = new CUdeviceptr();
            cuMemAlloc(a_dev[i], size*Sizeof.INT);
            b_dev[i] = new CUdeviceptr();
            cuMemAlloc(b_dev[i], size*Sizeof.INT);

        }

        // copy data to gpu memory

        for (int i=0; i<threads; i++){
            cuMemcpyHtoD(a_dev[i], Pointer.to(a[i]), size*Sizeof.INT);
            cuMemcpyHtoD(b_dev[i], Pointer.to(b[i]), size*Sizeof.INT);
        }

        CUdeviceptr gpuinA = new CUdeviceptr();
        cuMemAlloc(gpuinA, threads*Sizeof.POINTER);
        cuMemcpyHtoD(gpuinA, Pointer.to(a_dev), threads * Sizeof.POINTER);


        CUdeviceptr gpuinB = new CUdeviceptr();
        cuMemAlloc(gpuinB, threads * Sizeof.POINTER);
        cuMemcpyHtoD(gpuinB, Pointer.to(b_dev), threads * Sizeof.POINTER);

        CUdeviceptr c_dev = new CUdeviceptr();
            cuMemAlloc(c_dev, size*Sizeof.INT);

        CUdeviceptr outData = new CUdeviceptr();
        cuMemAlloc(outData, threads * Sizeof.POINTER);

        Pointer kernelParam = Pointer.to(
                Pointer.to(gpuinA),
                Pointer.to(gpuinB),
                Pointer.to(outData),
                Pointer.to(new int[]{threads}),
                Pointer.to(new int[]{size})
        );

        cuLaunchKernel(function, 1, 1, 1, threads, 1, 1, 0, null, kernelParam, null);
        int[] out = {0,0,0};
            cuMemcpyDtoH(Pointer.to(out), outData, threads * Sizeof.INT);
        cuMemFree(gpuinA);
        cuMemFree(gpuinB);
        cuMemFree(outData);


    }
}

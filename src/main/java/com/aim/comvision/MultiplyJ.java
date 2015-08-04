package com.aim.comvision;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaMemcpyKind;

import static jcuda.driver.JCudaDriver.*;
import static jcuda.runtime.JCuda.cudaMemcpy;

/**
 * Created by victor on 16.07.15.
 */
public class MultiplyJ {

    public static void main(String[] args) {

        float[] a = new float[] {(float)1.35};
        float[] b = new float[] {(float)2.5};
        float[] c = new float[1];

        cuInit(0);
        CUcontext pctx = new CUcontext();
        CUdevice dev = new CUdevice();
        cuDeviceGet(dev, 0);
        cuCtxCreate(pctx, 0, dev);

        CUmodule module = new CUmodule();
        cuModuleLoad(module, "src/main/java/com/aim/comvision/multiply2.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "multiply");

        CUdeviceptr a_dev = new CUdeviceptr();
        cuMemAlloc(a_dev, Sizeof.FLOAT);
        cuMemcpyHtoD(a_dev, Pointer.to(a), Sizeof.FLOAT);

        CUdeviceptr b_dev = new CUdeviceptr();
        cuMemAlloc(b_dev, Sizeof.FLOAT);
        cuMemcpyHtoD(b_dev, Pointer.to(b), Sizeof.FLOAT);

        CUdeviceptr c_dev = new CUdeviceptr();
        cuMemAlloc(c_dev, Sizeof.FLOAT);

        Pointer kernelParameters = Pointer.to(
                Pointer.to(a_dev),
                Pointer.to(b_dev),
                Pointer.to(c_dev)
        );

        cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, null, kernelParameters, null);
        cuMemcpyDtoH(Pointer.to(c), c_dev, Sizeof.FLOAT);
        JCuda.cudaFree(a_dev);
        JCuda.cudaFree(b_dev);
        JCuda.cudaFree(c_dev);

        System.out.println("Result = "+c[0]);
    }

}

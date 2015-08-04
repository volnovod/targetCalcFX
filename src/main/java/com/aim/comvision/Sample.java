package com.aim.comvision;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static jcuda.driver.JCudaDriver.*;

/**
 * Created by victor on 19.07.15.
 */
public class Sample {

    public static void main(String[] args) {
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC


        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUcontext pctx = new CUcontext();
        CUdevice dev = new CUdevice();
        cuDeviceGet(dev, 0);
        cuCtxCreate(pctx, 0, dev);

        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, "src/main/java/com/aim/comvision/multiply2.ptx");

        // Obtain a function pointer to the "sampleKernel" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "sampleKernel");

        int numThreads = 8;
        int size = 128;

        // Allocate and fill host input memory: A 2D float array with
        // 'numThreads' rows and 'size' columns, each row filled with
        // the values from 0 to size-1.
        float sum=0;
        float hostInput[][] = new float[numThreads][size];
        for(int i = 0; i < numThreads; i++)
        {
            sum=0;
            for (int j=0; j<size; j++)
            {
                hostInput[i][j] = (float)j;
                sum+=hostInput[i][j];
            }
            System.out.println(sum);
        }

        // Allocate arrays on the device, one for each row. The pointers
        // to these array are stored in host memory.
        CUdeviceptr hostDevicePointers[] = new CUdeviceptr[numThreads];
        for(int i = 0; i < numThreads; i++)
        {
            hostDevicePointers[i] = new CUdeviceptr();
            cuMemAlloc(hostDevicePointers[i], size * Sizeof.FLOAT);
        }

        // Copy the contents of the rows from the host input data to
        // the device arrays that have just been allocated.
        for(int i = 0; i < numThreads; i++)
        {
            cuMemcpyHtoD(hostDevicePointers[i],
                    Pointer.to(hostInput[i]), size * Sizeof.FLOAT);
        }

        // Allocate device memory for the array pointers, and copy
        // the array pointers from the host to the device.
        CUdeviceptr deviceInput = new CUdeviceptr();
        cuMemAlloc(deviceInput, numThreads * Sizeof.POINTER);
        cuMemcpyHtoD(deviceInput, Pointer.to(hostDevicePointers),
                numThreads * Sizeof.POINTER);

        // Allocate device output memory: A single column with
        // height 'numThreads'.
        CUdeviceptr deviceOutput = new CUdeviceptr();
        cuMemAlloc(deviceOutput, numThreads * Sizeof.FLOAT);

        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParams = Pointer.to(
                Pointer.to(deviceInput),
                Pointer.to(new int[]{size}),
                Pointer.to(deviceOutput)
        );

        // Call the kernel function.
        cuLaunchKernel(function,
                1, 1, 1,           // Grid dimension
                numThreads, 1, 1,  // Block dimension
                0, null,           // Shared memory size and stream
                kernelParams, null // Kernel- and extra parameters
        );
        cuCtxSynchronize();

        // Allocate host output memory and copy the device output
        // to the host.
        float hostOutput[] = new float[numThreads];
        cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput,
                numThreads * Sizeof.FLOAT);

        // Verify the result
        boolean passed = true;
        for(int i = 0; i < numThreads; i++)
        {
            float expected = 0;
            for(int j = 0; j < size; j++)
            {
                expected += hostInput[i][j];
            }
            if (Math.abs(hostOutput[i] - expected) > 1e-5)
            {
                passed = false;
                break;
            }
        }
        System.out.println("Test "+(passed?"PASSED":"FAILED"));

        // Clean up.
        for(int i = 0; i < numThreads; i++)
        {
            cuMemFree(hostDevicePointers[i]);
        }
        cuMemFree(deviceInput);
        cuMemFree(deviceOutput);
    }



    /**
     * Fully reads the given InputStream and returns it as a byte array
     *
     * @param inputStream The input stream to read
     * @return The byte array containing the data from the input stream
     * @throws IOException If an I/O error occurs
     */
    private static byte[] toByteArray(InputStream inputStream)
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

}

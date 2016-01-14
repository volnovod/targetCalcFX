package com.aim.comvision;

import jcuda.Pointer;
import jcuda.runtime.JCuda;

import java.util.ArrayList;

/**
 * Created by victor on 07.07.15.
 */
public class Klust {


    public static void main(String args[])
    {
        Pointer pointer = new Pointer();
        JCuda.cudaMalloc(pointer, 4);
        System.out.println("Pointer: "+pointer);
        JCuda.cudaFree(pointer);
    }
}

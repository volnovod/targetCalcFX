package com.aim.view;

import javax.swing.*;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;



/**
 * Created by victor on 26.06.15.
 */
public class ExamGpu {

//    public static void main(String[] args) {
//        Mat src = imread("auto.png",0) ;
//        if(src.empty()){
//            System.out.println("Null");
//        }
//
//        Mat dst = new Mat();
//
//        bilateralFilter(src, dst, -1, 50, 7);
//        Canny(dst, dst, 35, 200);
//        imwrite("out.png", dst);
//    }

    public static void main(String[] args) {
        Mat src = imread("auto.png", 0);
        GpuMat gpuMat = new GpuMat(src);
//        GpuMat dst = new GpuMat();
//        bilateralFilter(gpuMat, dst, -1, 50, 7);
    }
}

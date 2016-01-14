package com.aim.comvision;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import static jcuda.driver.JCudaDriver.*;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;

/**
 * Created by victor on 12.08.15.
 */
public class OnlineFilter extends Application{

    private BufferedImage bufferedImage;
    private int size;
    private int threads;
    private Webcam webcam;
    private WritableImage image =new WritableImage(640,480);

    public OnlineFilter() throws HeadlessException {

    }

    public void init(){
        this.webcam = Webcam.getDefault();
        if (this.webcam.isOpen()){
            this.webcam.close();
        }

        this.webcam.setViewSize(new Dimension(640, 480));
        WebcamUtils.capture(this.webcam, "jpg");
        this.webcam.open();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final OnlineFilter filter = new OnlineFilter();
        filter.init();
        ImageView imageView = new ImageView(this.image);

        imageView.setImage(filter.image);
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageView.setImage(filter.image);
                filter.startFiltering();
            }
        });

        StackPane root = new StackPane();
        Scene scene = new Scene(root,800,600);

        root.getChildren().add(imageView);
        primaryStage.setScene(scene);
        primaryStage.show();
        timer.start();
    }

    public void startFiltering(){
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext pctx = new CUcontext();
        cuCtxCreate(pctx, 0, device);

        CUmodule module = new CUmodule();
        cuModuleLoad(module, "/home/victor/Java/workFX/src/main/java/com/aim/comvision/optimizkernel.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "filter");

        CUdeviceptr input = new CUdeviceptr();
        CUdeviceptr out = new CUdeviceptr();

        cuMemAlloc(input, 640 * 480 * Sizeof.INT);


        cuMemAlloc(out, 640 * 480 * Sizeof.INT);

        int blockSizeX = 16;
        int blockSizeY = 16;
        int gridSizeX = (int) Math.ceil((double) 640 / blockSizeX);
        int gridSizeY = (int) Math.ceil((double) 480 / blockSizeY);

        bufferedImage = webcam.getImage();

        int[] int_array = new int[640*480];
        int[] o_array = new int[640 * 480];

        int threads = bufferedImage.getHeight();
        int size = bufferedImage.getWidth();

        boolean check = true;

//        while( check ){
            bufferedImage = webcam.getImage();

            int_array = bufferedImage.getRGB(0, 0, size, threads, null, 0, size);
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

            setBufferedImage(o_array);
        cuMemFree(input);
        cuMemFree(out);
//        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(int[] array) {

        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();

        DataBufferInt buffer = new DataBufferInt(array, 640 * 480);
        WritableRaster raster = Raster.createPackedRaster(buffer, 640, 480, 640, cm.getMasks(), null);
        this.bufferedImage = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
        setImage();
    }

    public void setImage(){

        this.image = SwingFXUtils.toFXImage(this.bufferedImage, null);
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }
}

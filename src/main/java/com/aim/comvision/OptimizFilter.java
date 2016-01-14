package com.aim.comvision;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;
import javafx.scene.image.Image;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.cudaEvent_t;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public long[] getArrayFromImage(Image image) {

        if (image == null) {
            return null;
        }

        long[] outArray = new long[(int) (image.getHeight() * image.getWidth())];
        int iterator = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                outArray[iterator] = image.getPixelReader().getArgb(j, i);
                iterator++;
            }
        }
        return outArray;
    }

    BufferedImage bufferedImage = null;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }


    int count = 0;
    public static void main(String[] args) throws IOException {

//        File file = new File("/home/victor/Java/workFX/teplo.jpg");
//        BufferedImage bufferedImage = null;
//        try {
//
//            bufferedImage = ImageIO.read(new FileInputStream(file));
//
//        } catch (IOException e) {
//            e.printStackTrace();

//        }
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640, 480));

        WebcamUtils.capture(webcam, "jpg");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        JPanel panel = new JPanel();
        panel.setSize(640, 480);

        BufferedImage image = webcam.getImage();

        JLabel label = new JLabel(new ImageIcon(image));
        panel.add(label);

        frame.add(panel);

        frame.setVisible(true);

        OptimizFilter filter = new OptimizFilter();
        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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

                BufferedImage bufferedImage = webcam.getImage();
                int threads = bufferedImage.getHeight();
                int size = bufferedImage.getWidth();

                cuMemAlloc(input, size * threads* Sizeof.INT);


                cuMemAlloc(out, size * threads* Sizeof.INT);

                int blockSizeX = 16;
                int blockSizeY = 16;
                int gridSizeX = (int) Math.ceil((double) size / blockSizeX);
                int gridSizeY = (int) Math.ceil((double) threads / blockSizeY);


                int[] o_array = new int[threads * size];



                int[] int_array = bufferedImage.getRGB(0, 0, size, threads, null, 0, size);
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
                DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();


                DataBufferInt buffer = new DataBufferInt(o_array, size * threads);
                WritableRaster raster = Raster.createPackedRaster(buffer, size, threads, size, cm.getMasks(), null);
                BufferedImage out1 = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);


               label.setIcon(new ImageIcon(out1));
                panel.repaint();


                cuMemFree(input);
                cuMemFree(out);

            }
        });
        timer.start();


        System.in.read();





    }

}

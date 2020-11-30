package it.unifi;

import java.awt.*;
import java.awt.image.BufferedImage;

public class KIPBuilder extends Thread {
    final private BufferedImage input, output;
    int kernelIndex, rowStart, rowEnd;

    static KernelMatrix[] kernels = {
            new KernelMatrix(new Integer[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, (float) 1 / 9),
            new KernelMatrix(new Integer[][]{{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}}, 1),
            new KernelMatrix(new Integer[][]{{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}}, 1),
            new KernelMatrix(new Integer[][]{{-2, -1, 0}, {-1, 1, 1}, {0, 1, 2}}, 1)
    };

    public KIPBuilder(BufferedImage input, BufferedImage output, int kernelIndex, int rowStart, int rowEnd) {
        this.input = input;
        this.output = output;
        this.kernelIndex = kernelIndex;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
    }

    @Override
    public void run() {
        int width = input.getWidth();
        int height = input.getHeight();

        Integer[][] matrix = kernels[kernelIndex].getMatrix();
        float multFactor = kernels[kernelIndex].getMultFactor();
        int matrixOrder = matrix.length;

        for (int y = rowStart; y < rowEnd; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int i = 0; i < matrixOrder; i++) {
                    for (int j = 0; j < matrixOrder; j++) {

                        int tmpX = x - matrixOrder / 2 + i;
                        int tmpY = y - matrixOrder / 2 + j;

                        if (tmpX >= 0 && tmpX < width && tmpY >= 0 && tmpY < height) {
                            int RGB = input.getRGB(tmpX, tmpY);
                            int R = (RGB >> 16) & 0xff;
                            int G = (RGB >> 8) & 0xff;
                            int B = (RGB) & 0xff;

                            red += (R * matrix[i][j]);
                            green += (G * matrix[i][j]);
                            blue += (B * matrix[i][j]);
                        }
                    }
                }

                int outR = Math.min(Math.max((int) (red * multFactor), 0), 255);
                int outG = Math.min(Math.max((int) (green * multFactor), 0), 255);
                int outB = Math.min(Math.max((int) (blue * multFactor), 0), 255);

                output.setRGB(x, y, new Color(outR, outG, outB).getRGB());
            }
        }
    }
}
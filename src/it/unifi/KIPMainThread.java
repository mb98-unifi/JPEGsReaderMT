package it.unifi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class KIPMainThread extends Thread {
    final private ImageContainer imageContainer;
    private int THREADS_NUM;
    final private JTextField textField;
    final private JComboBox<String> modeComboBox;

    public KIPMainThread(ImageContainer imageContainer, int THREAD_NUM, JTextField textField, JComboBox<String> modeComboBox) {
        this.imageContainer = imageContainer;
        this.THREADS_NUM = THREAD_NUM;
        this.textField = textField;
        this.modeComboBox = modeComboBox;
    }

    @Override
    public void run() {
        BufferedImage input = null;
        try {
            input = ImageIO.read(new File(imageContainer.getPath()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (input != null) {
            BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
            int rowNumber = input.getHeight() / THREADS_NUM;

            if (rowNumber < 10) {
                THREADS_NUM = 1;
                rowNumber = input.getHeight();
            }

            //Creating Threads
            Thread[] threads = new Thread[THREADS_NUM];
            for (int i = 0; i < THREADS_NUM - 1; i++) {
                threads[i] = new KIPBuilder(input, output, modeComboBox.getSelectedIndex(), rowNumber * i, rowNumber * (i + 1));
            }
            threads[THREADS_NUM - 1] = new KIPBuilder(input, output, modeComboBox.getSelectedIndex(), rowNumber * (THREADS_NUM - 1), input.getHeight());

            //Starting Threads
            long start = System.currentTimeMillis();

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println(end - start + "ms");

            //Writing Image
            try {
                ImageIO.write(output, "JPG", new File(textField.getText() + "/kernel-" + imageContainer.getName()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("File saved as kernel-" + imageContainer.getName());
        }
    }
}

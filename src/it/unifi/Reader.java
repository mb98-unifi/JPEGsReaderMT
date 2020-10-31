package it.unifi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Reader extends SwingWorker<Void, ImageContainer> {
    final private File[] files;
    final private int start, end;
    final private DefaultListModel<ImageContainer> listModel;

    public Reader(File[] files, int start, int end, DefaultListModel<ImageContainer> listModel) {
        this.files = files;
        this.start = start;
        this.end = end;
        this.listModel = listModel;
    }

    @Override
    protected Void doInBackground() {
        for (int i = start; i < end; i++) {
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(files[i]);
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }

            if (bufferedImage != null) {
                Image image;
                if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
                    image = bufferedImage.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
                } else {
                    image = bufferedImage.getScaledInstance(-1, 400, Image.SCALE_SMOOTH);
                }
                ImageContainer imageContainer = new ImageContainer(image, files[i].getName());
                publish(imageContainer);
            }
        }
        return null;
    }

    @Override
    protected void process(List<ImageContainer> chunks) {
        for (ImageContainer imageContainer : chunks) {
            listModel.addElement(imageContainer);
        }
    }
}


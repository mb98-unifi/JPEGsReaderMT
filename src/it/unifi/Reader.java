package it.unifi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Reader extends SwingWorker<Void, ImageContainer> {
    final private File[] files;
    final private ConcurrentLinkedQueue<File> filesConcurrentQueue;
    final private Queue<File> filesQueue;
    final private int start, end;
    final private DefaultListModel<ImageContainer> listModel;

    public Reader(File[] files, int start, int end, DefaultListModel<ImageContainer> listModel) {
       this(files, null, null, start, end, listModel);
    }

    public Reader(ConcurrentLinkedQueue<File> filesConcurrentQueue, DefaultListModel<ImageContainer> listModel) {
       this(null, filesConcurrentQueue, null, -1, -1, listModel);
    }

    public Reader(Queue<File> filesQueue, DefaultListModel<ImageContainer> listModel) {
        this(null, null, filesQueue, -1, -1, listModel);
    }

    private Reader(File[] files, ConcurrentLinkedQueue<File> filesConcurrentQueue, Queue<File> filesQueue, int start, int end, DefaultListModel<ImageContainer> listModel){
        this.files = files;
        this.filesConcurrentQueue = filesConcurrentQueue;
        this.filesQueue = filesQueue;
        this.start = start;
        this.end = end;
        this.listModel = listModel;
    }

    @Override
    protected Void doInBackground() {
        if (files != null) {//NO QUEUE
            for (int i = start; i < end; i++) {
                load(files[i]);
            }
        } else if (filesConcurrentQueue != null) {//CONCURRENT QUEUE
            File file = filesConcurrentQueue.poll();
            while (file != null) {
                load(file);
                file = filesConcurrentQueue.poll();
            }
        } else if (filesQueue != null) {//QUEUE WITH SYNCHRONIZATION
            File file;
            synchronized (filesQueue) {
                file = ((LinkedList<File>) filesQueue).pollLast();
            }
            while (file != null) {
                load(file);
                synchronized (filesQueue) {
                    file = ((LinkedList<File>) filesQueue).pollLast();
                }
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

    public void load(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
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
            ImageContainer imageContainer = new ImageContainer(image, file.getName(), file.getAbsolutePath());
            publish(imageContainer);
        }
    }
}


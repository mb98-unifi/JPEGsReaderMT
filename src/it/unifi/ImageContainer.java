package it.unifi;

import java.awt.*;

public class ImageContainer {
    final private Image image;
    final private String name;

    public ImageContainer(Image image, String name) {
        this.image = image;
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public String toString() {
        return name;
    }
}

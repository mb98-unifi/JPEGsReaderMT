package it.unifi;

import java.awt.*;

public class ImageContainer {
    final private Image image;
    final private String name;
    final private String path;

    public ImageContainer(Image image, String name, String path) {
        this.image = image;
        this.name = name;
        this.path = path;
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return getName();
    }
}

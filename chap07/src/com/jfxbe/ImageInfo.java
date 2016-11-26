package com.jfxbe;

import javafx.scene.effect.ColorAdjust;

/**
 * This class has a url to the image file and a degrees value
 * to rotate the image view based on the angle of incre
 */
public class ImageInfo {
    private String url;
    private double degrees;
    private ColorAdjust colorAdjust;

    public ImageInfo(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public void addDegrees(double degrees) {
        setDegrees(this.degrees + degrees);
    }

    public ColorAdjust getColorAdjust() {
        if (colorAdjust == null) {
            colorAdjust = new ColorAdjust();
        }
        return colorAdjust;
    }

    public void setColorAdjust(ColorAdjust colorAdjust) {
        this.colorAdjust = colorAdjust;
    }
}

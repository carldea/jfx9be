package com.jfxbe;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cpdea on 11/12/16.
 */
public class ImageViewButtons extends Pane {
    /** The current index into the IMAGE_FILES list. */
    private int currentIndex = -1;

    /** Enumeration of next and previous button directions */
    public enum ButtonMove {NEXT, PREV}

    /** List of ImageInfo instances. */
    private List<ImageInfo> imageFiles = new ArrayList<>();

    private Pane leftButton;
    private Pane rightButton;

    public ImageViewButtons(List<ImageInfo> imageFiles) {
        this.imageFiles = imageFiles;

        // create button panel
        Pane buttonStackPane = new StackPane();
        buttonStackPane.getStyleClass().add("button-pane");

        // left arrow button
        leftButton = new Pane();
        Arc leftButtonArc = new Arc(0,12, 15, 15, -30, 60);
        leftButton.getChildren().add(leftButtonArc);

        leftButtonArc.setType(ArcType.ROUND);
        leftButtonArc.getStyleClass().add("left-arrow");

        // Right arrow button
        rightButton = new Pane();
        Arc rightButtonArc = new Arc(15, 12, 15, 15, 180-30, 60);
        rightButton.getChildren().add(rightButtonArc);
        rightButtonArc.setType(ArcType.ROUND);
        rightButtonArc.getStyleClass().add("right-arrow");

        HBox buttonHbox = new HBox();
        buttonHbox.getStyleClass().add("button-panel");
        HBox.setHgrow(leftButton, Priority.ALWAYS);
        HBox.setHgrow(rightButton, Priority.ALWAYS);
        HBox.setMargin(leftButton, new Insets(0,5,0,5));
        HBox.setMargin(rightButton, new Insets(0,5,0,5));
        buttonHbox.getChildren().addAll(leftButton, rightButton);

        buttonStackPane.getChildren().addAll(buttonHbox);

        getChildren().add(buttonStackPane);
    }
    public boolean isAtBeginning() {
        return currentIndex == 0;
    }
    public boolean isAtEnd() {
        return currentIndex == imageFiles.size()-1;
    }

    public void goPrevious() {
        currentIndex = gotoImageIndex(ButtonMove.PREV);
    }
    public void goNext() {
        currentIndex = gotoImageIndex(ButtonMove.NEXT);
    }
    private int gotoImageIndex(ButtonMove direction) {
        int size = imageFiles.size();
        if (size == 0) {
            currentIndex = -1;
        } else if (direction == ButtonMove.NEXT
                && size > 1
                && currentIndex < size - 1) {
            currentIndex += 1;
        } else if (direction == ButtonMove.PREV
                && size > 1
                && currentIndex > 0) {
            currentIndex -= 1;
        }

        return currentIndex;
    }
    public int getCurrentIndex() {
        return currentIndex;
    }
    public ImageInfo getCurrentImageInfo() {
        return imageFiles.get(getCurrentIndex());
    }

    /**
     * Adds the URL string representation of the path to the image file.
     * Based on a URL the method will check if it matches supported
     * image format.
     * @param url string representation of the path to the image file.
     */
    public void addImage(String url) {
        currentIndex +=1;
        imageFiles.add(currentIndex, new ImageInfo(url));
    }
    public void setLeftButtonAction(EventHandler<MouseEvent> eventHandler) {
        leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }
    public void setRightButtonAction(EventHandler<MouseEvent> eventHandler) {
        rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }

}

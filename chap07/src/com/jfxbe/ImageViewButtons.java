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

import java.util.List;

/**
 * Created by cpdea on 11/12/16.
 */
public class ImageViewButtons extends Pane {
    /** The current index into the IMAGE_FILES list. */
    private int _currentIndex = -1;

    /** Enumeration of next and previous button directions */
    public enum ButtonMove {NEXT, PREV}

    /** List of ImageInfo instances. */
    private List<ImageInfo> _imageFiles;

    private Pane _leftButton;
    private Pane _rightButton;

    public ImageViewButtons(List<ImageInfo> imageFiles) {
        _imageFiles = imageFiles;

        // create button panel
        Pane buttonStackPane = new StackPane();
        buttonStackPane.getStyleClass().add("button-pane");

        // left arrow button
        _leftButton = new Pane();
        Arc leftButtonArc = new Arc(0,12, 15, 15, -30, 60);
        _leftButton.getChildren().add(leftButtonArc);

        leftButtonArc.setType(ArcType.ROUND);
        leftButtonArc.getStyleClass().add("left-arrow");

        // Right arrow button
        _rightButton = new Pane();
        Arc rightButtonArc = new Arc(15, 12, 15, 15, 180-30, 60);
        _rightButton.getChildren().add(rightButtonArc);
        rightButtonArc.setType(ArcType.ROUND);
        rightButtonArc.getStyleClass().add("right-arrow");

        HBox buttonHbox = new HBox();
        buttonHbox.getStyleClass().add("button-panel");
        HBox.setHgrow(_leftButton, Priority.ALWAYS);
        HBox.setHgrow(_rightButton, Priority.ALWAYS);
        HBox.setMargin(_leftButton, new Insets(0,5,0,5));
        HBox.setMargin(_rightButton, new Insets(0,5,0,5));
        buttonHbox.getChildren().addAll(_leftButton, _rightButton);

        buttonStackPane.getChildren().addAll(buttonHbox);

        getChildren().add(buttonStackPane);
    }
    public boolean isAtBeginning() {
        return _currentIndex == 0;
    }
    public boolean isAtEnd() {
        return _currentIndex == _imageFiles.size()-1;
    }

    public void goPrevious() {
        _currentIndex = gotoImageIndex(ButtonMove.PREV);
    }
    public void goNext() {
        _currentIndex = gotoImageIndex(ButtonMove.NEXT);
    }
    private int gotoImageIndex(ButtonMove direction) {
        int size = _imageFiles.size();
        if (size == 0) {
            _currentIndex = -1;
        } else if (direction == ButtonMove.NEXT
                && size > 1
                && _currentIndex < size - 1) {
            _currentIndex += 1;
        } else if (direction == ButtonMove.PREV
                && size > 1
                && _currentIndex > 0) {
            _currentIndex -= 1;
        }

        return _currentIndex;
    }
    public int getCurrentIndex() {
        return _currentIndex;
    }
    public ImageInfo getCurrentImageInfo() {
        return _imageFiles.get(getCurrentIndex());
    }

    /**
     * Adds the URL string representation of the path to the image file.
     * Based on a URL the method will check if it matches supported
     * image format.
     * @param url string representation of the path to the image file.
     */
    public void addImage(String url) {
        _currentIndex +=1;
        _imageFiles.add(_currentIndex, new ImageInfo(url));
    }
    public void setLeftButtonAction(EventHandler<MouseEvent> eventHandler) {
        _leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }
    public void setRightButtonAction(EventHandler<MouseEvent> eventHandler) {
        _rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }

}

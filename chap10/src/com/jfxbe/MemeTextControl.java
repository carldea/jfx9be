package com.jfxbe;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * StackPane     (This whole meme)
 *     TextField (Edit text)
 *     Label     (Display text)
 *     Path      (Resize triangle)
 * Possible issue with FontWeight - https://bugs.openjdk.java.net/browse/JDK-8090423
 * Created by cpdea on 12/3/16.
 */
public class MemeTextControl extends StackPane {
    private boolean isDragged = false;
    private static int count;
    /*
     StackPane     (This whole meme)
         TextField (Edit text)
         Label     (Display text)
         Path      (Resize triangle)
     */
    public MemeTextControl(Scene scene, int fontSize) {
        getStyleClass().add("text-container-off");

        TextField textFieldPhrase = new TextField("MEME TEXT " + count++);
        //textFieldPhrase.setPrefColumnCount(300);
        textFieldPhrase.getStyleClass().add("meme-text-field");
        textFieldPhrase.setMinWidth(200);
        textFieldPhrase.setStyle("-fx-font-size: " + (fontSize-20) + ";");

        Font memeTextFont = Font.font("Anton", FontWeight.EXTRA_BOLD, fontSize);
        System.out.println("font: " + memeTextFont);
        Text textPhrase = new Text();
        textPhrase.getStyleClass().add("meme-text");
        textPhrase.setStyle("-fx-font-size: " + fontSize + ";");
        textPhrase.textProperty().bind(textFieldPhrase.textProperty());


        Label textLabel = new Label(null, textPhrase);
        textLabel.getStyleClass().add("meme-text-label");

        //text.setStyle("-fx-control-inner-background: #ffff0020; -fx-background-color: #00000000;");

        getChildren().addAll(textFieldPhrase, textLabel);

        Path resizeCorner = new Path();
        MoveTo startPt = new MoveTo(15, 0);
        LineTo l1 = new LineTo(15, 15);
        LineTo l2 = new LineTo(0, 15);
        ClosePath closePath = new ClosePath();
        resizeCorner.getElements().addAll(startPt, l1, l2, closePath);
        resizeCorner.setFill(new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(.5, Color.ORANGE),
                new Stop(1, Color.ORANGERED)));
        StackPane.setAlignment(resizeCorner, Pos.BOTTOM_RIGHT);
        getChildren().addAll(resizeCorner);
        resizeCorner.setStrokeWidth(0.0);

        ObjectProperty<Point2D> resizeCornerAnchor = new SimpleObjectProperty<>(new Point2D(0, 0));
        DoubleProperty anchorWidth = new SimpleDoubleProperty();
        DoubleProperty widthOffset = new SimpleDoubleProperty();

        // Update this component based on the size of the Label text.
        textLabel.widthProperty().addListener( listener -> {
            double minWidth = textLabel.getBoundsInParent().getWidth();
            setPrefWidth(minWidth);
        });

        // Begin resize process safe current width
        resizeCorner.setOnMousePressed(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            double w = getBoundsInParent().getWidth();
            anchorWidth.set(w);
            widthOffset.set(0);
            resizeCornerAnchor.set(new Point2D(x, y));
            System.out.println("press resizer x: " + x + " y: " +y + " w: " + w);
            mouseEvent.consume();
        });

        // Resize width of this component
        resizeCorner.setOnMouseDragged(mouseEvent -> {
            isDragged = true;
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            double length = x - resizeCornerAnchor.get().getX();
            double textBoundsWidth = getBoundsInParent().getWidth();
            double calculatedWidth = anchorWidth.get() + length;
            if (calculatedWidth >= textLabel.getBoundsInParent().getWidth()) {
                //if (textFieldPhrase.isVisible() && calculatedWidth < textFieldPhrase.getWidth()) {
                    // avoid setting
                //} else {
                    setPrefWidth(calculatedWidth);
                //}
            }
//            System.out.println("dragged resizer x: " + x + " y: " +y + " length: " + length + " width: " + anchorWidth.get());
//            System.out.println("dragged resizer bounds in parent " + getBoundsInParent());
            mouseEvent.consume();
        });

        // After releasing resize update new width
        resizeCorner.setOnMouseReleased(mouseEvent -> {
            isDragged = false;
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
//            System.out.println("release resizer x: " + x + " y: " +y);
            resizeCornerAnchor.set(new Point2D(x, y));
            double w = getPrefWidth();
            anchorWidth.set(w);
            widthOffset.set(0);
            mouseEvent.consume();
        });

//        cursorProperty().addListener(listener -> System.out.println("cursor" + cursorProperty().get()));
        ObjectProperty<Point2D> anchor = new SimpleObjectProperty<>(new Point2D(0, 0));


        // Go to view meme mode
        textFieldPhrase.setOnAction( actionEvent -> {
            focusOff(textLabel, textFieldPhrase, resizeCorner);
        });

        // Escape key exits edit mode
        textFieldPhrase.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                focusOff(textLabel, textFieldPhrase, resizeCorner);
            }
        });

        // Clicking on the scene exits edit mode.
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED,  eventHandler ->{
            focusOff(textLabel, textFieldPhrase, resizeCorner);
        });

        // Go to edit meme mode
        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount()  == 2) {
                textLabel.setVisible(false);
                textFieldPhrase.setVisible(true);
                focusOn(textLabel, textFieldPhrase, resizeCorner);
                Platform.runLater(() -> {
                    textFieldPhrase.selectAll();
                    textFieldPhrase.requestFocus();

                });
            }
            mouseEvent.consume();
        });

        // Focus this component
        setOnMouseEntered(mouseEvent -> {
            if (!textFieldPhrase.isVisible()) {
                focusOn(textLabel, textFieldPhrase, resizeCorner);
            }
//            System.out.println("entered " + anchor.get());
        });

        // Lose Focus
        setOnMouseExited(mouseEvent -> {
            if (!textFieldPhrase.isVisible() && !isDragged) {
                focusOff(textLabel, textFieldPhrase, resizeCorner);
            }
//            System.out.println("exited " + anchor.get());
        });

        // Create anchor for drag operation also bring to front.
        setOnMousePressed(mouseEvent -> {
            this.toFront();
            double x = mouseEvent.getSceneX() - anchor.get().getX();
            double y = mouseEvent.getSceneY() - anchor.get().getY();
            anchor.set(new Point2D(x, y));
        });

        // Drag operation to move this component.
        setOnMouseDragged(mouseEvent -> {
            double x = mouseEvent.getSceneX() - anchor.get().getX();
            double y = mouseEvent.getSceneY() - anchor.get().getY();
            setLayoutX(x);
            setLayoutY(y);
        });

        // Release mouse to update new anchor point
        setOnMouseReleased(mouseEvent -> {
            double x = mouseEvent.getSceneX() - anchor.get().getX();
            double y = mouseEvent.getSceneY() - anchor.get().getY();
            anchor.set(new Point2D(x, y));
        });

        // Pressing the delete key will remove this component
        addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                focusOn(textLabel, textFieldPhrase, resizeCorner);
                Pane parent = (Pane) getParent();
                parent.getChildren().remove(this);
            }
        });

        // Default to off position
        focusOff(textLabel, textFieldPhrase, resizeCorner);
    }

    private void focusOn(Label textLabel, TextField textField, Path resizeCorner) {
        requestFocus();
        resizeCorner.setVisible(true);
        getStyleClass().clear();
        getStyleClass().add("text-container-on");
    }
    private void focusOff(Label textLabel, TextField textField, Path resizeCorner) {
        textLabel.setVisible(true);
        textField.setVisible(false);
        resizeCorner.setVisible(false);
        getStyleClass().clear();
        getStyleClass().add("text-container-off");
    }
}


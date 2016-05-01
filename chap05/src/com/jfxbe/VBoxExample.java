package com.jfxbe;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * VBoxExample demonstrates the VBox layout.
 * @author cdea
 */
public class VBoxExample extends Application {

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 300, 250);
        VBox vbox = new VBox(5);        // spacing between child nodes only.
        vbox.setPadding(new Insets(1)); // space between vbox border and child nodes column

        // The border is blue, dashed, 0% radius for all corners,
        // a width of 1 pixel
        BorderStroke [] borderStrokes = new BorderStroke[] {
                new BorderStroke(Color.BLUE,
                        BorderStrokeStyle.DASHED,
                        new CornerRadii(0.0, true),
                        new BorderWidths(1.0))
        };
        vbox.setBorder(new Border(borderStrokes));

        Rectangle r1 = new Rectangle(10, 10); // little square
        Rectangle r2 = new Rectangle(20, 20); // big square
        Rectangle r3 = new Rectangle(5, 20);  // vertical rectangle
        Rectangle r4 = new Rectangle(20, 5);  // horizontal rectangle

        VBox.setMargin(r1, new Insets(2,2,2,2)); // margin around r1

        vbox.getChildren().addAll(r1, r2, r3, r4);

        root.getChildren().add(vbox);
        primaryStage.setOnShown((WindowEvent we) -> {
            System.out.println("vbox width  " + vbox.getBoundsInParent().getWidth());
            System.out.println("vbox height " + vbox.getBoundsInParent().getHeight());
        });
        primaryStage.setTitle("VBox Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
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
 * HBoxExample demonstrates the HBox layout.
 *
 * @author cdea
 */
public class HBoxExample extends Application {

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 300, 250);

        // pixels space between child nodes
        HBox hbox = new HBox(5);

        // The border is blue, dashed,
        // 0% radius for all corners,
        // a width of 1 pixel
        BorderStroke[] borderStrokes = new BorderStroke[] {
                new BorderStroke(Color.BLUE,
                                 BorderStrokeStyle.DASHED,
                                 new CornerRadii(0.0, true),
                                 new BorderWidths(1.0))
        };
        hbox.setBorder(new Border(borderStrokes));

        // padding between child nodes only
        hbox.setPadding(new Insets(1));
        //hbox.setSpacing(1);
        // rectangles r1 to r4
        Rectangle r1 = new Rectangle(10, 10);
        Rectangle r2 = new Rectangle(20, 20);
        Rectangle r3 = new Rectangle(5, 20);
        Rectangle r4 = new Rectangle(20, 5);

        // margin of 2 pixels
        HBox.setMargin(r1, new Insets(2,2,2,2));

        hbox.getChildren().addAll(r1, r2, r3, r4);

        root.getChildren().add(hbox);

        // once shown display the dimensions all added up.
        primaryStage.setOnShown((WindowEvent we) -> {
            System.out.println("hbox width  " + hbox.getBoundsInParent().getWidth());
            System.out.println("hbox height " + hbox.getBoundsInParent().getHeight());
        });
        primaryStage.setTitle("HBox Example");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}

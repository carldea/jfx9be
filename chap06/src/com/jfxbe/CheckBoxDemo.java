package com.jfxbe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cpdea on 7/17/16.
 */
public class CheckBoxDemo extends Application{
    public static void main(String[] args) {
        Application.launch(args);
    }
    @Override
    public void start(Stage stage) {

        stage.setTitle("Check Box States");

        Pane root = new Pane();
        root.setId("background");

        Scene scene = new Scene(root, 900, 250);
        VBox vBox = new VBox(20);
        CheckBox checkBox1 = new CheckBox("Checked (Selected=true)");
        checkBox1.setSelected(true);
        CheckBox checkBox2 = new CheckBox("Unchecked (Selected=false)");
        checkBox2.setSelected(false);
        CheckBox checkBox3 = new CheckBox("Undefined (Indeterminate=true)");
        checkBox3.setAllowIndeterminate(true);
        checkBox3.setIndeterminate(true);
        vBox.getChildren().addAll(checkBox1, checkBox2, checkBox3);
        vBox.setLayoutX(30);
        vBox.setLayoutY(30);
        root.getChildren().add(vBox);

        stage.setScene(scene);
        stage.show();
    }
}

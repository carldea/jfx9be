package com.jfxbe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The FXMLContactForm application that loads an FXML view
 * to be displayed.
 */
public class FXMLContactForm extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("FXMLContactForm ");

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/ContactForm.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 380, 150, Color.WHITE);
        stage.setScene(scene);
        stage.setMinWidth(200);
        stage.setMinHeight(200);

        stage.show();
    }
}

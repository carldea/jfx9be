package com.jfxbe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * CustomFonts is an example of loading a custom font to be used in
 * labels, buttons, or any controls containing text.
 * Using the Kanit font with SIL OPEN FONT LICENSE by Cadson Demak
 * https://fonts.google.com/specimen/Kanit?query=kanit&selection.family=Kanit
 * @author carldea
 */
public class CustomFonts extends Application {

    @Override
    public void init() throws Exception {
        // load all icons
        Font.loadFont(CustomFonts.class.getResource("/Kanit-MediumItalic.ttf").openStream(), 10.0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("CustomFonts ");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 700, 150);

        // Create Title
        Label labelText = new Label("JavaFX 9 by Example ");
        labelText.setFont(Font.font("Kanit", FontWeight.MEDIUM, 60));
        HBox banner = new HBox(labelText);
        banner.setPadding(new Insets(10, 0, 10, 10));
        banner.setPrefHeight(70);

        root.setTop(banner);

        stage.setScene(scene);
        stage.show();


    }


}

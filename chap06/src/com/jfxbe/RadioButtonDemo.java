package com.jfxbe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cpdea on 7/17/16.
 */
public class RadioButtonDemo extends Application{
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Radio Buttons");

        BorderPane root = new BorderPane();
        root.setId("background");

        Scene scene = new Scene(root, 900, 400);
        VBox vBox = new VBox(20);
        Label questionLabel = new Label("What is your method of payment?");
        ToggleGroup group = new ToggleGroup();
        RadioButton visaButton = new RadioButton("Visa");
        visaButton.setUserData("Visa");
        visaButton.setSelected(true);

        RadioButton payPalButton = new RadioButton("PayPal");
        payPalButton.setUserData("PayPal");

        RadioButton masterCardButton = new RadioButton("Master Card");
        masterCardButton.setUserData("Master Card");

        RadioButton bitCoinButton = new RadioButton("BitCoin");
        bitCoinButton.setUserData("BitCoin");

        RadioButton cashButton = new RadioButton("Cash");
        cashButton.setUserData("Cash");

        visaButton.setToggleGroup(group);
        payPalButton.setToggleGroup(group);
        masterCardButton.setToggleGroup(group);
        bitCoinButton.setToggleGroup(group);
        cashButton.setToggleGroup(group);

        group.selectedToggleProperty().addListener( listener -> {
            System.out.println("Payment type: " + group.getSelectedToggle().getUserData());
        });
        vBox.getChildren().addAll(questionLabel, visaButton, payPalButton,
                masterCardButton, bitCoinButton, cashButton);
        BorderPane.setMargin(vBox, new Insets(25));
        root.setLeft(vBox);

        stage.setScene(scene);
        stage.show();
    }
}

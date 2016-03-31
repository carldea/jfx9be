package com.jfxbe;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Button Fun is an app showing typical buttons used in application development.
 * Car sprite image is from http://www.chasersgaming.co.uk
 * @author carldea
 */
public class ButtonFun extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Button Fun");
        BorderPane root = new BorderPane();
        root.setId("background");
        Scene scene = new Scene(root, 900, 250);

        // load JavaFX CSS style
        scene.getStylesheets()
                .add(getClass().getResource("/button-fun.css")
                               .toExternalForm());
        VBox leftControlPane = new VBox(10);

        leftControlPane.setPadding(new Insets(0, 10, 20, 15));

        // Create radio buttons for linear,
        // ease in and ease out
        ToggleGroup group = new ToggleGroup();

        RadioButton easeLinearBtn = new RadioButton("Linear");
        easeLinearBtn.setUserData(Interpolator.LINEAR);
        easeLinearBtn.getStyleClass().add("easing-option-button");
        easeLinearBtn.setSelected(true);
        easeLinearBtn.setToggleGroup(group);

        RadioButton easeInBtn = new RadioButton("Ease In");
        easeInBtn.setUserData(Interpolator.EASE_IN);
        easeInBtn.getStyleClass().add("easing-option-button");
        easeInBtn.setToggleGroup(group);

        RadioButton easeOutBtn = new RadioButton("Ease Out");
        easeOutBtn.setUserData(Interpolator.EASE_OUT);
        easeOutBtn.getStyleClass().add("easing-option-button");
        easeOutBtn.setToggleGroup(group);

        leftControlPane.setAlignment(Pos.BOTTOM_LEFT);
        leftControlPane.getChildren().addAll(easeLinearBtn, easeInBtn, easeOutBtn);
        root.setLeft(leftControlPane);

        // Create button controls to move car forward or backward.
        HBox hbox = new HBox(10);
        Button leftBtn = new Button("<");
        leftBtn.getStyleClass().add("nav-button");
        Button rightBtn = new Button(">");
        rightBtn.getStyleClass().add("nav-button");
        FlowPane controlPane = new FlowPane();
        FlowPane.setMargin(hbox, new Insets(0, 5, 10, 10));
        hbox.getChildren().addAll(leftBtn, rightBtn);
        controlPane.getChildren().add(hbox);
        root.setBottom(controlPane);

        // Draw the ground surface
        AnchorPane surface = new AnchorPane();
        root.setCenter(surface);

        Image carBackward = new Image("/spr_bluecar_0_0-backwards.png");
        Image carGoingForward = new Image("/spr_bluecar_0_0.png");

        Image sportCarBackward = new Image("/sportscar-backwards.png");
        Image sportCarGoingForward = new Image("/sportscar.png");

        int x1 = 20, x2 = 500;
        int y1 = 100, y2 = 100;

        // Linear car (regular)
        ImageView carView1 = new ImageView(carGoingForward);
        carView1.setPreserveRatio(true);
        carView1.setFitWidth(150);
        carView1.setX(x1);

        AnchorPane.setBottomAnchor(carView1, 100.0);
        surface.getChildren().add(carView1);

        // Easing car (sports car)
        ImageView carView2 = new ImageView(sportCarGoingForward);
        carView2.setPreserveRatio(true);
        carView2.setFitWidth(150);
        carView2.setX(x1);

        AnchorPane.setBottomAnchor(carView2, 20.0);
        surface.getChildren().add(carView2);

        // Linear animation of the car to compare against
        TranslateTransition animate1 = new TranslateTransition(Duration.millis(400), carView1);
        animate1.toXProperty().set(x2);
        animate1.setInterpolator(Interpolator.LINEAR);
        animate1.setDelay(Duration.millis(100));

        // The animation based on the currently selected radio buttons.
        TranslateTransition animate2 = new TranslateTransition(Duration.millis(400), carView2);
        animate2.setInterpolator(Interpolator.LINEAR);
        animate2.toXProperty().set(x2);
        animate2.setInterpolator((Interpolator) group.getSelectedToggle().getUserData());
        animate2.setDelay(Duration.millis(100));

        // Go forward (Left)
        leftBtn.setOnAction( ae -> {
            animate1.stop();
            carView1.setImage(carGoingForward);
            animate1.toXProperty().set(x1);
            animate1.playFromStart();

            animate2.stop();
            carView2.setImage(sportCarGoingForward);
            animate2.setInterpolator((Interpolator) group.getSelectedToggle().getUserData());
            animate2.toXProperty().set(x1);
            animate2.playFromStart();

        });

        // Go backward (Right)
        rightBtn.setOnAction( ae -> {
            animate1.stop();
            carView1.setImage(carBackward);
            animate1.toXProperty().set(x2);
            animate1.playFromStart();

            animate2.stop();
            carView2.setImage(sportCarBackward);
            animate2.setInterpolator((Interpolator) group.getSelectedToggle().getUserData());
            animate2.toXProperty().set(x2);
            animate2.playFromStart();
        });

        stage.setScene(scene);
        stage.show();
    }
}

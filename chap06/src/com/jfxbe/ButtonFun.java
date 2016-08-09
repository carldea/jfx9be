package com.jfxbe;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Button Fun is an app showing typical buttons used in application development.
 * Car sprites are from http://opengameart.org/users/chasersgaming
 * @author carldea
 */
public class ButtonFun extends Application {
    private Car[] myCars;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        myCars = new Car[3];

        Car workCar = buildCar("/spr_bluecar_0_0.png", "/spr_bluecar_0_0-backwards.png",
                "Select this car to drive to work.");
        Car sportsCar = buildCar("/sportscar.png", "/sportscar-backwards.png",
                "Select this car to drive to the theater.");
        Car travelVan = buildCar("/travel_vehicle.png", "/travel_vehicle.png",
                "Select this vehicle to go on vacation.");

        myCars[0] = workCar;
        myCars[1] = sportsCar;
        myCars[2] = travelVan;
    }

    private Car buildCar(String carForwardFile, String carBackwardFile, String description) {
        Image carGoingForward = new Image(carForwardFile);
        Image carBackward = new Image(carBackwardFile);
        return new Car(carGoingForward, carBackward, description);
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

        RadioButton easeLinearBtn = new RadioButton("Work Car");
        easeLinearBtn.setUserData(myCars[0]);
        easeLinearBtn.getStyleClass().add("option-button");
        easeLinearBtn.setSelected(true);
        easeLinearBtn.setToggleGroup(group);

        RadioButton easeInBtn = new RadioButton("Weekend Car");
        easeInBtn.setUserData(myCars[1]);
        easeInBtn.getStyleClass().add("option-button");
        easeInBtn.setToggleGroup(group);

        RadioButton easeOutBtn = new RadioButton("Travel Van");
        easeOutBtn.setUserData(myCars[2]);
        easeOutBtn.getStyleClass().add("option-button");
        easeOutBtn.setToggleGroup(group);


        // hyperlink
        Hyperlink carInfoLink = createHyperLink(group);

        leftControlPane.getChildren().add(carInfoLink);

        // Create check boxes to turn lights on or off.
        CheckBox headLightsCheckBox = new CheckBox("Headlights on");

        leftControlPane.getChildren().add(headLightsCheckBox);

        leftControlPane.setAlignment(Pos.BOTTOM_LEFT);
        leftControlPane.getChildren().addAll(easeLinearBtn, easeInBtn, easeOutBtn);


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

        root.setLeft(leftControlPane);
        int x1 = 20, x2 = 500;
        int y1 = 100, y2 = 100;

        ImageView carView = new ImageView(myCars[0].carForwards);
        carView.setPreserveRatio(true);
        carView.setFitWidth(150);
        carView.setX(x1);

        Arc carHeadlights = new Arc();
        carHeadlights.setId("car-headlights-1");
        carHeadlights.setCenterX(50.0f);
        carHeadlights.setCenterY(90.0f);
        carHeadlights.setRadiusX(300.0f);
        carHeadlights.setRadiusY(300.0f);
        carHeadlights.setStartAngle(170.0f);
        carHeadlights.setLength(15f);
        carHeadlights.setType(ArcType.ROUND);
        carHeadlights.visibleProperty().bind(headLightsCheckBox.selectedProperty());

        // Easing car (sports car)
        AnchorPane.setBottomAnchor(carView, 20.0);
        AnchorPane.setBottomAnchor(carHeadlights, 20.0);
        AnchorPane carPane = new AnchorPane(carHeadlights, carView);


        AnchorPane.setBottomAnchor(carPane, 20.0);
        surface.getChildren().add(carPane);

        // The animation based on the currently selected radio buttons.
        TranslateTransition animateCar = new TranslateTransition(Duration.millis(400), carPane);
        animateCar.setInterpolator(Interpolator.LINEAR);
        animateCar.toXProperty().set(x2);
        //animateCar.setInterpolator((Interpolator) group.getSelectedToggle().getUserData());
        animateCar.setDelay(Duration.millis(100));

        // Go forward (Left)
        leftBtn.setTooltip(new Tooltip("Drive forward"));
        leftBtn.setOnAction( ae -> {
            animateCar.stop();
            Car selectedCar = (Car) group.getSelectedToggle().getUserData();
            carView.setImage(selectedCar.carForwards);
            animateCar.toXProperty().set(x1);
            animateCar.playFromStart();

        });

        // Go backward (Right)
        rightBtn.setTooltip(new Tooltip("Drive backward"));
        rightBtn.setOnAction( ae -> {
            animateCar.stop();
            Car selectedCar = (Car) group.getSelectedToggle().getUserData();
            carView.setImage(selectedCar.carBackwards);
            animateCar.toXProperty().set(x2);
            animateCar.playFromStart();
        });
        group.selectedToggleProperty().addListener((ob, oldVal, newVal) -> {
            Car selectedCar = (Car) newVal.getUserData();
            System.out.println("selected car: " + selectedCar.carDescription);
            carView.setImage(selectedCar.carForwards);
        });

        stage.setScene(scene);
        stage.show();
    }

    private Hyperlink createHyperLink(ToggleGroup chosenCarToggle) {
        Hyperlink carInfoLink = new Hyperlink("Car Information");
        Popup carInfoPopup = new Popup();
        carInfoPopup.getScene().getStylesheets()
                .add(getClass().getResource("/button-fun.css")
                               .toExternalForm());

        carInfoPopup.setAutoHide(true);
        carInfoPopup.setHideOnEscape(true);
        Arc pointer = new Arc(0, 0, 20, 20, -20, 40);
        pointer.setType(ArcType.ROUND);
        Rectangle msgRect = new Rectangle( 18, -20, 200.5, 150);


        Shape msgBubble = Shape.union(pointer, msgRect);
        msgBubble.getStyleClass().add("message-bubble");

        TextFlow textMsg = new TextFlow();
        textMsg.setPrefWidth(msgRect.getWidth() -5);
        textMsg.setPrefHeight(msgRect.getHeight() -5);
        textMsg.setLayoutX(pointer.getBoundsInLocal().getWidth()+5);
        textMsg.setLayoutY(msgRect.getLayoutY() + 5);

        Text descr = new Text();
        descr.setFill(Color.ORANGE);
        textMsg.getChildren().add(descr);

        // whenever a selected car set the text.
        chosenCarToggle.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            Car selectedCar = (Car) newVal.getUserData();
            descr.setText(selectedCar.carDescription);
        });

        carInfoPopup.getContent().addAll(msgBubble, textMsg);

        carInfoLink.setOnAction(actionEvent -> {
            Bounds linkBounds = carInfoLink.localToScreen(carInfoLink.getBoundsInLocal());
            carInfoPopup.show(carInfoLink, linkBounds.getMaxX(), linkBounds.getMinY() -10);
        });
        return carInfoLink;
    }
}

class Car {

    String carDescription;
    Image carBackwards;
    Image carForwards;

    public Car(){};

    public Car(Image carForwards, Image carBackwards, String desc) {
        this.carForwards = carForwards;
        this.carBackwards = carBackwards;
        this.carDescription = desc;
    }
}

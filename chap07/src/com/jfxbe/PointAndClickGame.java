package com.jfxbe;

import javafx.animation.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 *     https://creativecommons.org/licenses/by-sa/3.0/
 *
 * AirPlane https://commons.wikimedia.org/wiki/File:Gulfstream_G500.svg#file
 * Cloud https://commons.wikimedia.org/wiki/File:W_cloud.svg
 * Windmill https://commons.wikimedia.org/wiki/File:Blason_ville_fr_Hauville_(Eure).svg
 *
 * @author cpdea
 */
public class PointAndClickGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Click And Point Game");
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 551, 400, Color.WHITE);

        SVGPath plane = createSVGPath("game-assets/plane-svg-path.txt");

        SVGPath windmill = createSVGPath("game-assets/windmill-svg-path.txt");
        AnchorPane.setBottomAnchor(windmill, 50.0);
        AnchorPane.setRightAnchor(windmill, 100.0);

        SVGPath rotorBlades = createSVGPath("game-assets/rotor-blades-svg-path.txt");
        AnchorPane.setBottomAnchor(rotorBlades, 58.0);
        AnchorPane.setRightAnchor(rotorBlades, 86.0);

        // create clouds
        SVGPath cloud1 = createSVGPath("game-assets/cloud-svg-path.txt");

        // Path Transition
        Path flightPath = new Path();
        PathElement startPath = new MoveTo(-200, 100);
        QuadCurveTo quadCurveTo = new QuadCurveTo(100, -50, 500, 100);
        flightPath.getElements()
                  .addAll(startPath, quadCurveTo);
        flightPath.setVisible(false);

        PathTransition flyPlane = new PathTransition(Duration.millis(8000),
                flightPath, plane);
        flyPlane.setCycleCount(Animation.INDEFINITE);
        flyPlane.setOrientation(
                PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        // Rotation Transition
        RotateTransition rotateBlade = new RotateTransition(Duration.millis(8000),
                rotorBlades);
        rotateBlade.setCycleCount(Animation.INDEFINITE);
        rotateBlade.setFromAngle(0);
        rotateBlade.setToAngle(360);

        // Scale Transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500),
                plane);
        scaleTransition.setCycleCount(4);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setByX(1.5);
        scaleTransition.setByY(1.5);

        // Translate Transition
        TranslateTransition moveCloud = new TranslateTransition(Duration.seconds(15),
                cloud1);
        moveCloud.setFromX(-200);
        moveCloud.setFromY(100);
        moveCloud.setCycleCount(Animation.INDEFINITE);
        moveCloud.setAutoReverse(true);
        moveCloud.setToX(scene.getWidth() + 200);

        // Fade Transition
        FadeTransition fadeCloud = new FadeTransition(Duration.millis(1000),
                cloud1);
        fadeCloud.setCycleCount(4);
        fadeCloud.setFromValue(1);
        fadeCloud.setToValue(0);
        fadeCloud.setOnFinished(actionEvent -> cloud1.setOpacity(1));

        // readjust the end points when the width of the screen changes.
        scene.widthProperty().addListener( observable -> {
            quadCurveTo.setControlX(scene.getWidth()/2);
            quadCurveTo.setX(scene.getWidth() + 200);
            flyPlane.playFromStart();

            moveCloud.setToX(scene.getWidth() + 200);
            moveCloud.playFromStart();
        });


        scene.setOnMouseClicked( mouseEvent -> {
            boolean isCloudClicked = cloud1.getBoundsInParent()
                                           .contains(mouseEvent.getX(),
                                                   mouseEvent.getY());
            if (isCloudClicked) {
                if (fadeCloud.getStatus() == Animation.Status.STOPPED) {
                    fadeCloud.playFromStart();
                }
            }

            boolean isPlaneClicked = plane.getBoundsInParent()
                                          .contains(mouseEvent.getX(),
                                                  mouseEvent.getY());
            if (isPlaneClicked) {
                if (scaleTransition.getStatus() == Animation.Status.STOPPED) {
                    scaleTransition.playFromStart();
                }
            }

        });

        root.getChildren()
            .addAll(flightPath,
                plane,
                cloud1,
                windmill,
                rotorBlades);

        primaryStage.setScene(scene);
        primaryStage.setOnShowing( windowEvent -> {
            quadCurveTo.setControlX(scene.getWidth()/2);
            quadCurveTo.setX(scene.getWidth() + 200);
            flyPlane.playFromStart();
            rotateBlade.playFromStart();
            moveCloud.playFromStart();
        });

        primaryStage.show();

    }

    private  SVGPath createSVGPath(String url) {
        SVGPath svgPath = new SVGPath();
        Task<String> svgLoadWorker = createSVGLoadWorker(url);
        svgLoadWorker.setOnSucceeded(stateEvent -> {
            // apply path info
            svgPath.setContent(svgLoadWorker.getValue());
        });
        new Thread(svgLoadWorker).start();
        return svgPath;
    }

    protected Task<String> createSVGLoadWorker(String pathDataUrl) {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                // On the worker thread...
                String pathData = null;

                InputStream in = this.getClass()
                        .getClassLoader()
                        .getResourceAsStream(pathDataUrl);

                pathData = new Scanner(in, "UTF-8")
                        .useDelimiter("\\A").next();

                return pathData;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}

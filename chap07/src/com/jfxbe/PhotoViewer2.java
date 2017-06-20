package com.jfxbe;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * A photo viewer application to demonstrate the JavaFX ImageView APIs.
 * Although you can use the menu options there are also keyboard
 * short-cuts.
 * <p>
 * Instructions:
 *    1. Drag and drop an image file onto the application window.
 *    1a. Alternatively, you may use the file chooser from the menu.
 *    2. Repeat step 1 so more than 2 images are loaded.
 *    3. Click the left and right arrow controls to advance.
 *    4. Rotate current image from the menu options.
 *    5. Adjust Color settings in the menu options.
 * </p>  
 * @author Carl Dea
 */
public class PhotoViewer2 extends PhotoViewer {

    private final static Logger LOGGER = Logger
            .getLogger(PhotoViewer2.class.getName());


    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Creates a task to load an image in the background. During
     * the load process the progress indicator is displayed. Once
     * the image is successfully loaded the image will be displayed.
     * Also, various image attributes will be applied to the
     * current image view node such as rotation and color adjustments.
     * @param progressIndicator node indicating load progress.
     */
    protected void loadAndDisplayImage(ProgressIndicator progressIndicator) {
        if (buttonPanel.getCurrentIndex() < 0) return;

        final ImageInfo imageInfo = buttonPanel.getCurrentImageInfo();

        // show spinner while image is loading
        progressIndicator.setVisible(true);

        Task<Image> loadImage = createWorker(imageInfo.getUrl());

        // after loading has succeeded apply image info
        loadImage.setOnSucceeded(workerStateEvent -> {

            try {
                Image nextImage = loadImage.get();
                SequentialTransition fadeIntoNext = transitionByFading(nextImage, imageInfo);
                fadeIntoNext.playFromStart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                // hide progress indicator
                progressIndicator.setVisible(false);
            }

        });

        // any failure turn off spinner
        loadImage.setOnFailed(workerStateEvent ->
                progressIndicator.setVisible(false));

        executorService.submit(loadImage);
    }

    private SequentialTransition transitionByFading(Image nextImage,
                                                    ImageInfo imageInfo) {
        // fade out image view node
        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(500), currentViewImage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(actionEvent -> {
            currentViewImage.setImage(nextImage);
            // Rotate image view
            rotateImageView(imageInfo.getDegrees());

            // Apply color adjust
            colorAdjust = imageInfo.getColorAdjust();
            currentViewImage.setEffect(colorAdjust);

            // update the menu items containing slider controls
            updateSliders();
        });
        // fade in image view node
        FadeTransition fadeIn =
                new FadeTransition(Duration.millis(500), currentViewImage);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // fade out image view, swap image and fade in image view
        SequentialTransition seqTransition =
                new SequentialTransition(fadeOut, fadeIn);
        return seqTransition;
    }
}


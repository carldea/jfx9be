package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
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
public class PhotoViewer extends Application {
    /** Standard Logger. */
    private final static Logger LOGGER = Logger
            .getLogger(PhotoViewer.class.getName());

    /** Current image view display */
    protected ImageView _currentViewImage;

    /** Rotation of the image view */
    protected Rotate _rotate = new Rotate();

    /** Color adjustment */
    protected ColorAdjust _colorAdjust = new ColorAdjust();

    /** A mapping of color adjustment type to a bound slider */
    protected Map<String, Slider> _sliderLookupMap = new HashMap<>();

    /** Custom Button panel to view previous and next images */
    protected ImageViewButtons _buttonPanel;

    /** Single threaded service for loading an image */
    protected ExecutorService _executorService =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Photo Viewer");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.BLACK);
        scene.getStylesheets()
                .add(getClass()
                        .getClassLoader()
                        .getResource("photo-viewer.css")
                        .toExternalForm());
        primaryStage.setScene(scene);

        // Anchor Pane
        AnchorPane mainContentPane = new AnchorPane();

        // Group is a container to hold the image view
        Group imageGroup = new Group();
        AnchorPane.setTopAnchor(imageGroup, 0.0);
        AnchorPane.setLeftAnchor(imageGroup, 0.0);

        // Current image view
        _currentViewImage = createImageView(_rotate);
        imageGroup.getChildren().add(_currentViewImage);

        // Custom ButtonPanel (Next, Previous)
        List<ImageInfo> IMAGE_FILES = new ArrayList<>();
        _buttonPanel = new ImageViewButtons(IMAGE_FILES);

        // Create a progress indicator
        ProgressIndicator progressIndicator = createProgressIndicator();

        // layer items. Items that are last are on top
        mainContentPane.getChildren().addAll(imageGroup,
                _buttonPanel, progressIndicator);

        // Create menus File, Rotate, Color adjust menus
        Menu fileMenu = createFileMenu(primaryStage, progressIndicator);
        Menu rotateMenu = createRotateMenu();
        Menu colorAdjustMenu = createColorAdjustMenu();
        MenuBar menuBar = new MenuBar(
                fileMenu, rotateMenu, colorAdjustMenu);
        root.setTop(menuBar);

        // Create the center content of the root pane (Border)
        // Make sure the center content is under the menu bar
        BorderPane.setAlignment(mainContentPane, Pos.TOP_CENTER);
        root.setCenter(mainContentPane);

        // When nodes are visible they can be repositioned.
        primaryStage.setOnShown( event ->
                wireupUIBehavior(primaryStage, progressIndicator));

        primaryStage.show();

    }


    @Override
    public void stop() throws Exception {
        super.stop();
        // Shutdown thread service
        _executorService.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * A factory function returning an ImageView instance to
     * preserve the aspect ratio and bind the width
     * of the scene to resize the image.
     * @param rotate A Transform to rotate the image view node.
     * @return ImageView A newly created image view for current
     * display.
     */
    private ImageView createImageView(Rotate rotate) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.getTransforms().addAll(rotate);
        return imageView;
    }

    /**
     * Create a progress indicator control shown when loading images.
     * @return ProgressIndicator a new progress indicator.
     */
    private ProgressIndicator createProgressIndicator() {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(false);
        progress.setMaxSize(100d, 100d);
        return progress;
    }

    /**
     * Returns a newly created file menu having three menu items.
     * The three options are Open, Save As and Quit.
     *
     * @param stage The primary stage window to place file
     *              chooser in the center.
     * @param progressIndicator The indicator shown during the load
     *                          process.
     * @return Menu A File menu containing Open, Save As and
     * Quit menu items respectively.
     */
    private Menu createFileMenu(Stage stage,
                                ProgressIndicator progressIndicator) {
        Menu fileMenu = new Menu("File");

        MenuItem loadImagesMenuItem = new MenuItem("_Open");
        loadImagesMenuItem.setMnemonicParsing(true);
        loadImagesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O,
                KeyCombination.SHORTCUT_DOWN));

        // file chooser to open a file
        wireupLoadMenuItem(loadImagesMenuItem, stage, progressIndicator);

        MenuItem saveAsMenuItem = new MenuItem("Save _As");
        saveAsMenuItem.setMnemonicParsing(true);

        // file chooser to save image as file
        wireupSaveMenuItem(saveAsMenuItem, stage);

        // Quit application
        MenuItem exitMenuItem = new MenuItem("_Quit");
        exitMenuItem.setMnemonicParsing(true);
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.SHORTCUT_DOWN));

        // exiting
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(loadImagesMenuItem,
                saveAsMenuItem, exitMenuItem);

        return fileMenu;
    }

    /**
     * An action to launch a file chooser to allow the user to load
     * an image from the file system.
     * @param menuItem The Open menu item
     * @param primaryStage
     * @param progressIndicator The indicator shown during the load
     *                          process.
     */
    protected void wireupLoadMenuItem(MenuItem menuItem,
                                      Stage primaryStage,
                                      ProgressIndicator progressIndicator) {
        // A file chooser is launched with a filter based
        // on image file formats
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images",
                        "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
        menuItem.setOnAction( actionEvt -> {
            List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
            if (list != null) {
                for (File file : list) {
                    //openFile(file);
                    try {
                        String url = file.toURI().toURL().toString();
                        if (isValidImageFile(url)) {
                            _buttonPanel.addImage(url);
                            loadAndDisplayImage(progressIndicator);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * An action to launch a file chooser to allow the user to save
     * an image.
     * @param menuItem The save menu item.
     * @param primaryStage
     */
    protected void wireupSaveMenuItem(MenuItem menuItem,
                                      Stage primaryStage) {
        menuItem.setOnAction( actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File fileSave = fileChooser.showSaveDialog(primaryStage);
            if (fileSave != null) {

                WritableImage image = _currentViewImage.snapshot(
                        new SnapshotParameters(), null);

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null),
                            "png", fileSave);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Returns a menu having two menu items Rotate Left and
     * Rotate Right respectively.
     *
     * @return Menu A menu having two menu items Rotate Left
     * and Rotate Right respectively.
     */
    private Menu createRotateMenu() {
        Menu rotateMenu = new Menu("Rotate");
        // Menu item with a keyboard combo to rotate the image
        // left 90 degrees
        MenuItem rotateLeft = new MenuItem("Rotate 90° Left");
        rotateLeft.setAccelerator(new KeyCodeCombination(KeyCode.LEFT,
                KeyCombination.SHORTCUT_DOWN));

        wireupRotateAngleBy(rotateLeft, -90);

        // Menu item with a keyboard combo to rotate the image
        // right 90 degrees
        MenuItem rotateRight = new MenuItem("Rotate 90° Right");
        rotateRight.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT,
                KeyCombination.SHORTCUT_DOWN));

        wireupRotateAngleBy(rotateRight, 90);

        rotateMenu.getItems().addAll(rotateLeft, rotateRight);
        return rotateMenu;
    }

    /**
     * Action code to be attached to the target menu item to
     * rotate the image view by an angle in degrees.
     * @param menuItem
     * @param angleDegrees
     */
    protected void wireupRotateAngleBy(MenuItem menuItem, double angleDegrees) {
        // rotate options
        menuItem.setOnAction(actionEvent -> {
            ImageInfo imageInfo = _buttonPanel.getCurrentImageInfo();
            imageInfo.addDegrees(angleDegrees);
            rotateImageView(imageInfo.getDegrees());
        });
    }

    /**
     * Rotates the ImageView based on angle in degrees.
     * The pivot point is based on the current width and
     * height of the image view node.
     * @param degrees
     */
    protected void rotateImageView(double degrees) {
        _rotate.setPivotX(_currentViewImage.getFitWidth()/2);
        _rotate.setPivotY(_currentViewImage.getFitHeight()/2);
        _rotate.setAngle(degrees);
    }

    /**
     * Creates menu items for color adjustments using sliders for
     * Hue, Saturation, Brightness and Contrast.
     * @return Menu having menu items for adjusting color adjustments.
     */
    private Menu createColorAdjustMenu() {
        Menu colorAdjustMenu = new Menu("Color Adjust");
        Consumer<Double> hueConsumer = (value) ->
            _colorAdjust.hueProperty().set(value);
        MenuItem hueMenuItem = createSliderMenuItem("Hue", hueConsumer);

        Consumer<Double> saturationConsumer = (value) ->
                _colorAdjust.setSaturation(value);

        MenuItem saturateMenuItem = createSliderMenuItem("Saturation",
                saturationConsumer);

        Consumer<Double> brightnessConsumer = (value) ->
                _colorAdjust.setBrightness(value);

        MenuItem brightnessMenuItem = createSliderMenuItem("Brightness",
                brightnessConsumer);

        Consumer<Double> contrastConsumer = (value) ->
                _colorAdjust.setContrast(value);

        MenuItem contrastMenuItem = createSliderMenuItem("Contrast",
                contrastConsumer);

        MenuItem resetMenuItem = new MenuItem("Restore to Original");

        resetMenuItem.setOnAction(actionEvent -> {
            _colorAdjust.setHue(0);
            _colorAdjust.setContrast(0);
            _colorAdjust.setBrightness(0);
            _colorAdjust.setSaturation(0);
            updateSliders();
        });

        colorAdjustMenu.getItems()
                .addAll(hueMenuItem, saturateMenuItem,
                        brightnessMenuItem, contrastMenuItem,
                        resetMenuItem);

        return colorAdjustMenu;
    }
    /**
     * Creates menu items containing slider controls for
     * color adjustments.
     * @param name Name of the color adjustment
     * @param c A closure from the caller to alter a
     *          color adjustment.
     * @return MenuItem A label with a slider.
     */
    private MenuItem createSliderMenuItem(String name,
                                          Consumer<Double> c) {

        Slider slider = new Slider(-1, 1, 0);
        _sliderLookupMap.put(name, slider);
        slider.valueProperty().addListener(ob ->
                c.accept(slider.getValue()));

        Label label = new Label(name, slider);
        label.setContentDisplay(ContentDisplay.LEFT);
        MenuItem menuItem = new CustomMenuItem(label);
        return menuItem;
    }

    /**
     * When a picture is loaded or currently displayed the
     * sliders will take on the color adjustment values.
     */
    protected void updateSliders() {
        _sliderLookupMap.forEach( (id, slider) -> {
            switch (id) {
                case "Hue":
                    slider.setValue(_colorAdjust.getHue());
                    break;
                case "Brightness":
                    slider.setValue(_colorAdjust.getBrightness());
                    break;
                case "Saturation":
                    slider.setValue(_colorAdjust.getSaturation());
                    break;
                case "Contrast":
                    slider.setValue(_colorAdjust.getContrast());
                    break;
                default:
                    slider.setValue(0);
            }
        });
    }

    /** Wireup behavior of UI elements and actions.
     * This method is called after the stage and scene is shown.
     * @param primaryStage Main application window.
     * @param progressIndicator node indicating load progress.
     */
    private void wireupUIBehavior(Stage primaryStage,
                                  ProgressIndicator progressIndicator) {
        Scene scene = primaryStage.getScene();

        // make the custom button panel float bottom right
        Runnable repositionButtonPanel = () -> {
            // update buttonPanel's x
            _buttonPanel.setTranslateX(scene.getWidth() - 75);
            // update buttonPanel's y
            _buttonPanel.setTranslateY(scene.getHeight() - 75);
        };

        // make the progress indicator float in the center
        Runnable repositionProgressIndicator = () -> {
            // update progress x
            progressIndicator.setTranslateX(
                    scene.getWidth()/2 - (progressIndicator.getWidth()/2));
            progressIndicator.setTranslateY(
                    scene.getHeight()/2 - (progressIndicator.getHeight()/2));
        };

        // invoking both to repositioning closures.
        Runnable repositionCode = () -> {
            repositionButtonPanel.run();
            repositionProgressIndicator.run();
        };

        // Anytime the window is resized reposition the button panel
        scene.widthProperty().addListener(observable ->
                repositionCode.run());
        scene.heightProperty().addListener(observable ->
                repositionCode.run());

        // Go ahead and reposition now.
        repositionCode.run();

        // resize image view when scene is resized.
        _currentViewImage.fitWidthProperty()
                        .bind(scene.widthProperty());

        // view previous image action
        Runnable viewPreviousAction = () -> {
            // if no previous image or currently loading.
            if (_buttonPanel.isAtBeginning()) return;
            else _buttonPanel.goPrevious();
            loadAndDisplayImage(progressIndicator);
        };

        // attach left button action
        _buttonPanel.setLeftButtonAction( mouseEvent ->
                viewPreviousAction.run());

        // Left arrow key stroke pressed action
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.LEFT
                    && !keyEvent.isShortcutDown()) {
                viewPreviousAction.run();
            }
        });

        // view next image action
        Runnable viewNextAction = () -> {
            // if no next image or currently loading.
            if (_buttonPanel.isAtEnd()) return;
            else _buttonPanel.goNext();
            loadAndDisplayImage(progressIndicator);
        };

        // attach right button action
        _buttonPanel.setRightButtonAction( mouseEvent ->
                viewNextAction.run());

        // Right arrow key stroke pressed action
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.RIGHT
                    && !keyEvent.isShortcutDown()) {
                viewNextAction.run();
            }
        });

        // Setup drag and drop file capabilities
        setupDragNDrop(primaryStage, progressIndicator);
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
        if (_buttonPanel.getCurrentIndex() < 0) return;

        final ImageInfo imageInfo = _buttonPanel.getCurrentImageInfo();

        // show spinner while image is loading
        progressIndicator.setVisible(true);

        Task<Image> loadImage = createWorker(imageInfo.getUrl());

        // after loading has succeeded apply image info
        loadImage.setOnSucceeded(workerStateEvent -> {

            try {

                _currentViewImage.setImage(loadImage.get());

                // Rotate image view
                rotateImageView(imageInfo.getDegrees());

                // Apply color adjust
                _colorAdjust = imageInfo.getColorAdjust();
                _currentViewImage.setEffect(_colorAdjust);

                // update the menu items containing slider controls
                updateSliders();

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

        _executorService.submit(loadImage);
    }

    /**
     * Returns a worker task (Task) which will off-load the image
     * on a separate thread when finished; the current image will
     * be displayed on the JavaFX application thread.
     * @param imageUrl ImageInfo instance containing a url string
     *                  representation of the path to the image file.
     *                  The imageInfo also has the degrees in rotation.
     * @return Task worker task to load image and display into ImageView
     * control.
     */
    protected Task<Image> createWorker(String imageUrl) {
        return new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                // On the worker thread...
                Image image = new Image(imageUrl, false);
                return image;
            }
        };
    }

    /**
     * Sets up the drag and drop capability for files and URLs to be 
     * dragged and dropped onto the scene. This will load the image into 
     * the current image view area.
     */
    private void setupDragNDrop(Stage primaryStage,
                                ProgressIndicator progressIndicator) {
        Scene scene = primaryStage.getScene();

        // Dragging over surface
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if ( db.hasFiles() 
                    || (db.hasUrl()
                    && isValidImageFile(db.getUrl()))) {

                LOGGER.log(Level.INFO, "url " + db.getUrl());
                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.consume();
            }
        });
        
        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard(); 
            // image from the local file system.
            if (db.hasFiles() && !db.hasUrl()) {
                db.getFiles().forEach( file -> {
                    try {
                        String url = file.toURI().toURL().toString();
                        if (isValidImageFile(url)) {
                            _buttonPanel.addImage(url);
                        }

                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                  });
            } else {
                String url = db.getUrl();
                LOGGER.log(Level.                        FINE, "dropped url: "+ db.getUrl());
                if (isValidImageFile(url)) {
                    _buttonPanel.addImage(url);
                }
            }

            loadAndDisplayImage(progressIndicator);


            event.setDropCompleted(true);
            event.consume();
        });
    }
    
    /**
     * Returns true if URL's file extensions match jpg, jpeg,
     * png, gif and bmp.
     * @param url standard URL path to image file.
     * @return boolean returns true if URL's extension matches
     * jpg, jpeg, png, bmp and gif.
     */
    private boolean isValidImageFile(String url) {
        List<String> imgTypes = Arrays.asList(".jpg", ".jpeg",
                ".png", ".gif", ".bmp");
        return imgTypes.stream()
                       .anyMatch(t -> url.toLowerCase().endsWith(t));
    }

}


package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A photo viewer application to demonstrate the JavaFX ImageView node.
 * <p>
 * Instructions:
 *    1. Drag and drop an image file onto the application window.
 *    1a. Alternatively, you may use the file chooser from the menu.
 *    2. Repeat step 1 so more than 2 images are loaded.
 *    3. Click the left and right arrow controls to advance.
 * </p>  
 * @author Carl Dea
 */
public class PhotoViewer extends Application {
    /** Standard Logger. */
    private final static Logger LOGGER = Logger
            .getLogger(PhotoViewer.class.getName());

    /** List of ImageInfo instances. */
    private final static List<ImageInfo> IMAGE_FILES = new ArrayList<>();

    /** The current index into the IMAGE_FILES list. */
    private int currentIndex = -1;

    /** Enumeration of next and previous button directions */
    public enum ButtonMove {NEXT, PREV}

    /** Current image view display */
    private ImageView currentImageView;

    public static final String MAIN_IMAGE_VIEW = "main-image-view";
    public static final String ROTATE_LEFT = "rotate-left-keystroke";
    public static final String ROTATE_RIGHT = "rotate-right-keystroke";

    private Menu rotateMenu;

    /** Loading progress indicator */
    private ProgressIndicator progressIndicator;

    /** Used to indicate a task is still working */
    private AtomicBoolean loading = new AtomicBoolean();

    /** A file chooser for the user to select image files to open. */
    private FileChooser fileChooser = new FileChooser();

    private ObjectProperty<ColorAdjust> colorAdjustProperty = new SimpleObjectProperty<>();
    enum COLOR_ADJ {
        HUE, SATURATION, BRIGHTNESS, CONTRAST
    }
    private Map<COLOR_ADJ, Slider> SLIDER_MAP = new HashMap<>();

    private ImageViewButtons buttonPanel;

    private Stage primaryStage;

    private Rotate rotate = new Rotate();
    private MenuBar menuBar;
    private ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private void wireupUIBehavior() {
        Scene scene = primaryStage.getScene();

        // resize image view when scene is resized.
        //ImageView imageView = (ImageView) scene.lookup("#" + MAIN_IMAGE_VIEW);
        currentImageView.fitWidthProperty().bind(scene.widthProperty());
        // wire-up menu options
        // rotate options
        MenuItem rotateLeft = rotateMenu.getItems()
                                        .stream()
                                        .filter( predicate ->
                                                ROTATE_LEFT.equals(predicate.getId()))
                                        .findFirst()
                                        .get();
        rotateLeft.setOnAction(actionEvent -> {
            if (currentIndex > -1) {
                ImageInfo imageInfo = IMAGE_FILES.get(currentIndex);
                imageInfo.addDegrees(-90);
                rotateImageView(imageInfo.getDegrees());
            }
        });

        MenuItem rotateRight = rotateMenu.getItems()
                .stream()
                .filter( predicate ->
                        ROTATE_RIGHT.equals(predicate.getId()))
                .findFirst()
                .get();

        rotateRight.setOnAction(actionEvent -> {
            if (currentIndex > -1) {
                    ImageInfo imageInfo = IMAGE_FILES.get(currentIndex);
                    imageInfo.addDegrees(90);
                    rotateImageView(imageInfo.getDegrees());
            }
        });


        // wire-up button panel

        // view previous image action
        Runnable viewPreviousAction = () -> {
            LOGGER.log(Level.INFO, "busy loading? " + loading.get());
            // if no previous image or currently loading.
            if (currentIndex == 0 || loading.get()) return;
            currentIndex = gotoImageIndex(ButtonMove.PREV);
            loadAndDisplayImage();
        };

        buttonPanel.setLeftButtonAction( mouseEvent -> viewPreviousAction.run());
        // Left arrow key pressed will display the previous image
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.LEFT
                    && !keyEvent.isShortcutDown()) {
                viewPreviousAction.run();
            }
        });

        Runnable viewNextAction = () -> {
            LOGGER.log(Level.INFO, "busy loading? " + loading.get());
            // if no next image or currently loading.
            if (currentIndex == IMAGE_FILES.size()-1
                    || loading.get()) return;

            currentIndex = gotoImageIndex(ButtonMove.NEXT);
            loadAndDisplayImage();
        };

        buttonPanel.setRightButtonAction( mouseEvent -> viewNextAction.run());

        // Right arrow key pressed will display the next image
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.RIGHT
                    && !keyEvent.isShortcutDown()) {
                viewNextAction.run();
            }
        });

        // Setup drag and drop file capabilities
        setupDragNDrop();
    }

    private void rotateImageView(double degrees) {
        rotate.setPivotX(currentImageView.getFitWidth()/2);
        rotate.setPivotY(currentImageView.getFitHeight()/2);
        rotate.setAngle(degrees);
    }
    private void loadAndDisplayImage() {
        if (currentIndex < 0) return;

        final ImageInfo imageInfo = IMAGE_FILES.get(currentIndex);
        progressIndicator.setVisible(true);
        progressIndicator.progressProperty().unbind();

        Task<Image> loadImage = createWorker(imageInfo.getUrl());
        progressIndicator.progressProperty().bind(loadImage.progressProperty());
        loadImage.setOnSucceeded(workerStateEvent -> {

            try {
                currentImageView.setImage(loadImage.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            // Rotate image view
            rotateImageView(imageInfo.getDegrees());

            // Apply color adjust
            currentImageView.setEffect(imageInfo.getColorAdjust());
            colorAdjustProperty.setValue(imageInfo.getColorAdjust());
            updateSliders(imageInfo.getColorAdjust());
            progressIndicator.setVisible(false);
            loading.set(false); // free lock
        });
        executorService.submit(loadImage);
    }
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Photo Viewer");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.BLACK);
        scene.getStylesheets()
                .add(getClass()
                .getResource("/photo-viewer.css")
                .toExternalForm());
        primaryStage.setScene(scene);

        // Anchor Pane
        AnchorPane mainContentPane = new AnchorPane();

        //   SubScene
        //      Group
        Group imageGroup = new Group();
        AnchorPane.setTopAnchor(imageGroup, 0.0);
        AnchorPane.setLeftAnchor(imageGroup, 0.0);

        //        imageView
        currentImageView = createImageView();

        //   ButtonPanel
        buttonPanel = new ImageViewButtons();


        imageGroup.getChildren().add(currentImageView);

        // Create a progress indicator
        progressIndicator = createProgressIndicator();

        // put subscene into anchor pane.
        // put Button panel
        // progress indicator
        mainContentPane.getChildren().addAll(imageGroup, buttonPanel, progressIndicator);

        // Create a button panel control having
        // left & right arrows buttons

        // Create menus File and Rotate
        Menu fileMenu = createFileMenu(primaryStage);
        Menu rotateMenu = createRotateMenu();
        Menu colorAdjustMenu = createColorAdjustMenu();
        menuBar = new MenuBar(fileMenu, rotateMenu, colorAdjustMenu);
        root.setTop(menuBar);

        // Create the center content of the root pane (Border)
//        mainContentPane.getChildren().addAll(currentImageView, progressIndicator, buttonPanel);

        // Make sure the center content is under the menu bar
        BorderPane.setAlignment(mainContentPane, Pos.TOP_CENTER);
        root.setCenter(mainContentPane);

        Runnable positionButtonPanel = () -> {
            // update buttonPanel's x
            buttonPanel.setTranslateX(scene.getWidth() - 75);
            // update buttonPanel's y
            buttonPanel.setTranslateY(scene.getHeight() - 75);
        };

        Runnable repositionProgressIndicator = () -> {
            // update progress x
            progressIndicator.setTranslateX(scene.getWidth()/2 - (progressIndicator.getWidth()/2));
            progressIndicator.setTranslateY(scene.getHeight()/2 - (progressIndicator.getHeight()/2));
        };

        scene.widthProperty().addListener(observable -> {
            positionButtonPanel.run();
            repositionProgressIndicator.run();
        });

        scene.heightProperty().addListener(observable -> {
            positionButtonPanel.run();
            repositionProgressIndicator.run();
        });


        // When nodes are visible they can be repositioned.
        primaryStage.setOnShown( event -> {
            wireupUIBehavior();
            positionButtonPanel.run();
            repositionProgressIndicator.run();
        });

        primaryStage.show();
        // after nodes are realized update button panel.
    }

    /**
     * Returns a newly created file menu having two menu items.
     * The first is Open menu item for loading images from a file chooser.
     * Secondly, Quit menu item exist the application.
     * @param stage The primary stage window to place file chooser in the center.
     * @return Menu A File menu contain Open and Quit menu items respectively.
     */
    private Menu createFileMenu(Stage stage) {
        Menu fileMenu = new Menu("File");

        // Open files
        MenuItem loadImagesMenuItem = new MenuItem("_Open");
        loadImagesMenuItem.setMnemonicParsing(true);
        loadImagesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O,
                KeyCombination.SHORTCUT_DOWN));

        // A file chooser is launched with a filter based
        // on image file formats
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
        loadImagesMenuItem.setOnAction( actionEvt -> {
            List<File> list = fileChooser.showOpenMultipleDialog(stage);
            if (list != null) {
                for (File file : list) {
                    //openFile(file);
                    try {
                        addImage(file.toURI().toURL().toString());
                        if (currentIndex > -1) {
                            //loadImage(IMAGE_FILES.get(currentIndex));
                            loadAndDisplayImage();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        MenuItem saveAsMenuItem = new MenuItem("Save _As");
        saveAsMenuItem.setMnemonicParsing(true);
        saveAsMenuItem.setOnAction( actionEvent -> {
            File fileSave = fileChooser.showSaveDialog(stage);
            if (fileSave != null) {

                WritableImage image = currentImageView.snapshot(new SnapshotParameters(), null);

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fileSave);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Quit application
        MenuItem exitMenuItem = new MenuItem("_Quit");
        exitMenuItem.setMnemonicParsing(true);
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.SHORTCUT_DOWN));
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(loadImagesMenuItem, saveAsMenuItem, exitMenuItem);

        return fileMenu;
    }

    /**
     * Returns a menu having two menu items Rotate Left and
     * Rotate Right respectively.
     *
     * @return Menu A menu having two menu items Rotate Left
     * and Rotate Right respectively.
     */
    private Menu createRotateMenu() {
        rotateMenu = new Menu("Rotate");
        // Menu item with a keyboard combo to rotate the image left 90 degrees
        MenuItem rotateLeft = new MenuItem("Rotate 90° Left");
        rotateLeft.setAccelerator(new KeyCodeCombination(KeyCode.LEFT,
                KeyCombination.SHORTCUT_DOWN));
        rotateLeft.setId(ROTATE_LEFT);

        // Menu item with a keyboard combo to rotate the image right 90 degrees
        MenuItem rotateRight = new MenuItem("Rotate 90° Right");
        rotateRight.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT,
                KeyCombination.SHORTCUT_DOWN));
        rotateRight.setId(ROTATE_RIGHT);

        rotateMenu.getItems().addAll(rotateLeft, rotateRight);
        return rotateMenu;
    }

    /**
     * When a picture is loaded or currently displayed the sliders will take on
     * the color adjustment values.
     * @param colorAdjust
     */
    private void updateSliders(ColorAdjust colorAdjust) {
        SLIDER_MAP.forEach( (k,slider) -> {
            switch (k) {
                case HUE:
                    slider.setValue(colorAdjust.getHue());
                    break;
                case BRIGHTNESS:
                    slider.setValue(colorAdjust.getBrightness());
                    break;
                case SATURATION:
                    slider.setValue(colorAdjust.getSaturation());
                    break;
                case CONTRAST:
                    slider.setValue(colorAdjust.getContrast());
                    break;
                default:
                    slider.setValue(0);
            }
        });
    }

    /**
     * Creates menu items containing slider controls for color adjustments.
     * @param name Name of the color adjustment
     * @param id the id or key for slider to be looked up.
     * @param c A closure from the caller to alter a color adjustment.
     * @return MenuItem A label with a slider.
     */
    private MenuItem createSliderMenuItem(String name, COLOR_ADJ id, Consumer<Double> c) {
        Slider slider = new Slider(-1, 1, 0);
        SLIDER_MAP.put(id, slider);
        slider.valueProperty().addListener((ob, ov, nv) -> {
            c.accept(nv.doubleValue());
        });
        Label label = new Label(name, slider);
        label.setContentDisplay(ContentDisplay.LEFT);
        MenuItem menuItem = new CustomMenuItem(label);
        return menuItem;
    }

    /**
     * Creates menu items for color adjustments using sliders for
     * Hue, Saturation, Brightness and Contrast.
     * @return Menu having menu items for adjusting color adjustments.
     */
    private Menu createColorAdjustMenu() {
        Menu colorAdjustMenu = new Menu("Color Adjust");
        Consumer<Double> hueConsumer = (value) -> {
            colorAdjustProperty.get().hueProperty().set(value);
        };
        MenuItem hueMenuItem = createSliderMenuItem("Hue", COLOR_ADJ.HUE, hueConsumer);

        Consumer<Double> saturationConsumer = (value) ->
            colorAdjustProperty.get().setSaturation(value);

        MenuItem saturateMenuItem = createSliderMenuItem("Saturation", COLOR_ADJ.SATURATION, saturationConsumer);

        Consumer<Double> brightnessConsumer = (value) ->
            colorAdjustProperty.get().setBrightness(value);

        MenuItem brightnessMenuItem = createSliderMenuItem("Brightness", COLOR_ADJ.BRIGHTNESS, brightnessConsumer);

        Consumer<Double> contrastConsumer = (value) ->
            colorAdjustProperty.get().setContrast(value);

        MenuItem contrastMenuItem = createSliderMenuItem("Contrast", COLOR_ADJ.CONTRAST, contrastConsumer);
        MenuItem resetMenuItem = new MenuItem("Restore to Original");
        resetMenuItem.setOnAction( actionEvent -> {
            ColorAdjust colorAdjust = colorAdjustProperty.get();
            colorAdjust.setHue(0);
            colorAdjust.setContrast(0);
            colorAdjust.setBrightness(0);
            colorAdjust.setSaturation(0);
            updateSliders(colorAdjust);
        });
        colorAdjustMenu.getItems().addAll(hueMenuItem, saturateMenuItem,
                brightnessMenuItem, contrastMenuItem, resetMenuItem);

        return colorAdjustMenu;
    }

    /**
     * A factory function returning an ImageView instance to 
     * preserve the aspect ratio and bind the width
     * of the scene to resize the image.
     * 
     * @return ImageView A newly created image view for current display.
     */
    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setId(MAIN_IMAGE_VIEW);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.getTransforms().addAll(rotate);
        return imageView;
    }
    
    /**
     * Sets up the drag and drop capability for files and URLs to be 
     * dragged and dropped onto the scene. This will load the image into 
     * the current image view area.
     */
    private void setupDragNDrop() {
        Scene scene = primaryStage.getScene();

        // Dragging over surface
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if ( db.hasFiles() 
                    || (db.hasUrl() && isValidImageFile(db.getUrl()))) {
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
                db.getFiles()
                  .stream()
                  .forEach( file -> {
                    try {
                        LOGGER.log(Level.INFO, "dropped file: "+
                                file.toURI().toURL().toString());
                        addImage(file.toURI().toURL().toString());
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                  });
            } else {
                LOGGER.log(Level.INFO, "dropped url: "+ db.getUrl());
                addImage(db.getUrl());
            }
            if (currentIndex > -1) {
                loadAndDisplayImage();
            }

            event.setDropCompleted(true);
            event.consume();
        });
    }

    /**
     * Create a progress indicator control shown when loading images.
     * @return ProgressIndicator a new progress indicator.
     */
    private ProgressIndicator createProgressIndicator() {
        ProgressIndicator progress = new ProgressIndicator(0);
        progress.setVisible(false);
        progress.setMaxSize(100d, 100d);
        return progress;
    }
    
    /**
     * Returns true if URL's file extensions match jpg, jpeg,
     * png, gif and bmp.
     * @param url standard URL path to image file.
     * @return boolean returns true if URL's extension matches
     * jpg, jpeg, png, bmp and gif.
     */
    private boolean isValidImageFile(String url) {
        List<String> imgTypes = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
        return imgTypes.stream()
                       .anyMatch(t -> url.toLowerCase().endsWith(t));
    }
    
    /**
     * Adds the URL string representation of the path to the image file.
     * Based on a URL the method will check if it matches supported 
     * image format.
     * @param url string representation of the path to the image file.
     */
    private void addImage(String url) {
        if (isValidImageFile(url)) {
            currentIndex +=1;
            IMAGE_FILES.add(currentIndex, new ImageInfo(url));
        }
    }
    
    /**
     * Returns the next index in the list of files to go to next.
     * 
     * @param direction PREV and NEXT to move backward or forward 
     * in the list of pictures.
     * @return int the index to the previous or next picture to be shown.
     */
    private int gotoImageIndex(ButtonMove direction) {
        int size = IMAGE_FILES.size();
        if (size == 0) {
            currentIndex = -1;
        } else if (direction == ButtonMove.NEXT 
                && size > 1 
                && currentIndex < size - 1) {
            currentIndex += 1;
        } else if (direction == ButtonMove.PREV
                && size > 1 
                && currentIndex > 0) {
            currentIndex -= 1;
        }

        return currentIndex;
    }
    
    /**
     * Returns a worker task (Task) which will off-load the image 
     * on a separate thread when finished; the current image will
     * be displayed on the JavaFX application thread.
     * @param imageUrl ImageInfo instance containing a url string
     *                  representation of the path to the image file.
     *                  The imageInfo also has the degrees in rotation.
     * @return Task worker task to load image and display into ImageView control.
     */
    private Task<Image> createWorker(String imageUrl) {
        return new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                // On the worker thread...
                Image image = new Image(imageUrl, false);
                return image;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }  
}

class ImageViewButtons extends Pane {

    private Pane leftButton;
    private Pane rightButton;

    public ImageViewButtons() {

        // create button panel
        Pane buttonStackPane = new StackPane();
        buttonStackPane.getStyleClass().add("button-pane");

        // left arrow button
        leftButton = new Pane();
        Arc leftButtonArc = new Arc(0,12, 15, 15, -30, 60);
        leftButton.getChildren().add(leftButtonArc);

        leftButtonArc.setType(ArcType.ROUND);
        leftButtonArc.getStyleClass().add("left-arrow");

        // Right arrow button
        rightButton = new Pane();
        Arc rightButtonArc = new Arc(15, 12, 15, 15, 180-30, 60);
        rightButton.getChildren().add(rightButtonArc);
        rightButtonArc.setType(ArcType.ROUND);
        rightButtonArc.getStyleClass().add("right-arrow");

        HBox buttonHbox = new HBox();
        buttonHbox.getStyleClass().add("button-panel");
        HBox.setHgrow(leftButton, Priority.ALWAYS);
        HBox.setHgrow(rightButton, Priority.ALWAYS);
        HBox.setMargin(leftButton, new Insets(0,5,0,5));
        HBox.setMargin(rightButton, new Insets(0,5,0,5));
        buttonHbox.getChildren().addAll(leftButton, rightButton);

        buttonStackPane.getChildren().addAll(buttonHbox);

        getChildren().add(buttonStackPane);
    }

    public void setLeftButtonAction(EventHandler<MouseEvent> eventHandler) {
        leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }
    public void setRightButtonAction(EventHandler<MouseEvent> eventHandler) {
        rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
    }

}


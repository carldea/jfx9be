package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
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
    private final static List<ImageInfo> IMAGE_FILES = new Vector<>();

    /** The current index into the IMAGE_FILES list. */
    private int currentIndex = -1;

    /** Enumeration of next and previous button directions */
    public enum ButtonMove {NEXT, PREV};

    /** Current image view display */
    private ImageView currentImageView;

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

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Photo Viewer");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.BLACK);
        scene.getStylesheets()
                .add(getClass()
                .getResource("/photo-viewer.css")
                .toExternalForm());
        primaryStage.setScene(scene);

        Group mainContentPane = new Group();

        // Setup the current image view area
        currentImageView = createImageView(scene.widthProperty());

        // sizing and positioning container
        StackPane.setAlignment(currentImageView, Pos.TOP_CENTER);
        StackPane imageFrame = new StackPane(currentImageView);
        //imageFrame.prefWidthProperty().bind(scene.widthProperty());
        //imageFrame.prefHeightProperty().bind(scene.heightProperty());

        // Setup drag and drop file capabilities
        setupDragNDrop(scene);

        // Create a progress indicator
        progressIndicator = createProgressIndicator();

        // Create a button panel control having
        // left & right arrows buttons
        Pane buttonPanel = createButtonPanel(scene);

        double padding = 15d;
        Runnable repositionButtonPanel = () -> {

            // Calculates the top of the scene's height minus the menu bar height,
            // minus the height of the button panel and lastly add the imagesBoundsMinY.
            // If imagesBoundsMinY is negative move button panel up otherwise its zero.
            //
            // When a transform occurs such as a 90 degrees rotation of a image view
            // node it will be outside of the stack pane's bounding region (BoundsInParent)
            // however the Group node will shift the stack pane node below the menu bar (Center border pane).
            // This will shift the button panel down on the Y axis. The topAnchor variable will calculate
            // the amount or difference to raise or lower the button panel.
            double adjustY = imageFrame.getBoundsInParent().getMinY();
            double adjustX = imageFrame.getBoundsInParent().getMinX();

            double topAnchor =
                scene.getHeight() - root.getInsets().getTop() -
                root.getTop().getBoundsInLocal().getHeight() -
                buttonPanel.getLayoutBounds().getHeight() - padding + adjustY;

            double leftAnchor = scene.getWidth() - root.getInsets().getRight() -
                    buttonPanel.getLayoutBounds().getWidth() - padding + adjustX;

            //LOGGER.log(Level.INFO, "(leftAnchor, topAnchor): (" + leftAnchor + ", " + topAnchor + ")");

            buttonPanel.setLayoutX(leftAnchor);
            buttonPanel.setLayoutY(topAnchor);

            // progress indicator
            double topAnchorPi =
                    scene.getHeight() -
                            root.getInsets().getTop() -
                            root.getTop().getBoundsInLocal().getHeight();

            topAnchorPi = topAnchorPi /2 - (progressIndicator.getLayoutBounds().getHeight()/2) + adjustY;
            double leftAnchorPi = scene.getWidth() - root.getInsets().getRight();
            leftAnchorPi = leftAnchorPi/2 - (progressIndicator.getLayoutBounds().getWidth()/2) + adjustX;

            //LOGGER.log(Level.INFO, "(leftAnchorPInd, topAnchorPInd): (" + leftAnchorPi + ", " + topAnchorPi + ")");

            progressIndicator.setLayoutX(leftAnchorPi);
            progressIndicator.setLayoutY(topAnchorPi);
        };

        // Reposition the top anchor value for the button panel
        // when the scene is being resized.
        scene.heightProperty().addListener(obs -> repositionButtonPanel.run());
        scene.widthProperty().addListener(obs -> repositionButtonPanel.run());

        // when an image is being loaded
        currentImageView.imageProperty().addListener(obs -> repositionButtonPanel.run());

        // when a rotation occurs.
        currentImageView.rotateProperty().addListener(obs -> repositionButtonPanel.run());

        // Create menus File and Rotate
        Menu fileMenu = createFileMenu(primaryStage);
        Menu rotateMenu = createRotateMenu();
        Menu colorAdjustMenu = createColorAdjustMenu();
        root.setTop(new MenuBar(fileMenu, rotateMenu, colorAdjustMenu));

//        colorAdjustProperty.addListener( (ob, ov, nv) -> {
//            updateSliders(nv);
//            currentImageView.setEffect(nv);
//        });
        // Create the center content of the root pane (Border)
        mainContentPane.getChildren().addAll(imageFrame, progressIndicator, buttonPanel);

        // Make sure the center content is under the menu bar
        BorderPane.setAlignment(mainContentPane, Pos.TOP_CENTER);
        root.setCenter(mainContentPane);

        primaryStage.show();
        // after nodes are realized update button panel.
        repositionButtonPanel.run();
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
                            loadImage(IMAGE_FILES.get(currentIndex));
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
        Menu rotateMenu = new Menu("Rotate");
        // Menu item with a keyboard combo to rotate the image left 90 degrees
        MenuItem rotateLeft = new MenuItem("Rotate 90° Left");
        rotateLeft.setAccelerator(new KeyCodeCombination(KeyCode.LEFT,
                KeyCombination.SHORTCUT_DOWN));
        rotateLeft.setOnAction(actionEvent -> {
            if (currentIndex > -1) {
                ImageInfo imageInfo = IMAGE_FILES.get(currentIndex);
                imageInfo.addDegrees(-90);
                currentImageView.setRotate(imageInfo.getDegrees());
            }
        });

        // Menu item with a keyboard combo to rotate the image right 90 degrees
        MenuItem rotateRight = new MenuItem("Rotate 90° Right");
        rotateRight.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT,
                KeyCombination.SHORTCUT_DOWN));
        rotateRight.setOnAction(actionEvent -> {
            if (currentIndex > -1) {
                ImageInfo imageInfo = IMAGE_FILES.get(currentIndex);
                imageInfo.addDegrees(90);

                currentImageView.setRotate(imageInfo.getDegrees());
            }
        });

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
            //System.out.println("slider " + k + " " + slider.getValue());
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
    private MenuItem createSliderMenuItem(String name, COLOR_ADJ id, Consumer c) {
        Slider slider = new Slider(-1, 1, 0);
        SLIDER_MAP.put(id, slider);
        slider.valueProperty().addListener((ob, ov, nv) -> {
            c.accept(nv.doubleValue());
        });
        Label label = new Label(name, slider);
        label.setContentDisplay(ContentDisplay.RIGHT);
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
        MenuItem hueMenuItem = createSliderMenuItem("H", COLOR_ADJ.HUE, hueConsumer);

        Consumer<Double> saturationConsumer = (value) -> {
            colorAdjustProperty.get().setSaturation(value);
        };
        MenuItem saturateMenuItem = createSliderMenuItem("S", COLOR_ADJ.SATURATION, saturationConsumer);

        Consumer<Double> brightnessConsumer = (value) -> {
            colorAdjustProperty.get().setBrightness(value);
        };
        MenuItem brightnessMenuItem = createSliderMenuItem("B", COLOR_ADJ.BRIGHTNESS, brightnessConsumer);

        Consumer<Double> contrastConsumer = (value) -> {
            colorAdjustProperty.get().setContrast(value);
        };
        MenuItem contrastMenuItem = createSliderMenuItem("C", COLOR_ADJ.CONTRAST, contrastConsumer);
        MenuItem resetMenuItem = new MenuItem("Reset");
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
     * @param widthProperty is the Scene's read only width property.
     * @return ImageView A newly created image view for current display.
     */
    private ImageView createImageView(ReadOnlyDoubleProperty widthProperty) {
        // maintain aspect ratio
        ImageView imageView = new ImageView();

        // set aspect ratio
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        // resize based on the scene
        imageView.fitWidthProperty().bind(widthProperty);
        return imageView;
    }
    
    /**
     * Sets up the drag and drop capability for files and URLs to be 
     * dragged and dropped onto the scene. This will load the image into 
     * the current image view area.
     * @param scene The primary application scene.
     */
    private void setupDragNDrop(Scene scene) {
        
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
                // image from some host
                addImage(db.getUrl());                     
            }
            if (currentIndex > -1) {
                loadImage(IMAGE_FILES.get(currentIndex));
            }

            event.setDropCompleted(true);
            event.consume();
        });
    }

    /**
     * Returns a custom created button panel 
     * containing left and right buttons to 
     * see the previous and next image displayed.
     * @param scene The main application scene
     * @return Group A custom button panel with 
     *  previous and next buttons
     */
    private Pane createButtonPanel(Scene scene){
        // create button panel
        Pane buttonStackPane = new StackPane();
        buttonStackPane.getStyleClass().add("button-pane");

        // left arrow button
        Pane leftButton = new Pane();
        Arc leftButtonArc = new Arc(0,12, 15, 15, -30, 60);
        leftButton.getChildren().add(leftButtonArc);

        leftButtonArc.setType(ArcType.ROUND);
        leftButtonArc.getStyleClass().add("left-arrow");

        // The action code to load the previous
        // image to be displayed.
        Runnable viewPreviousAction = () -> {
            LOGGER.log(Level.INFO, "busy loading? " + loading.get());
            // if no previous image or currently loading.
            if (currentIndex == 0 || loading.get()) return;
            int indx = gotoImageIndex(ButtonMove.PREV);
            if (indx > -1) {
                loadImage(IMAGE_FILES.get(indx));
            }
        };

        // Left arrow key pressed will display the previous image
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
           if (keyEvent.getCode() == KeyCode.LEFT
                   && !keyEvent.isShortcutDown()) {
               viewPreviousAction.run();
           }
        });

        // Mouse press event on left button will display the previous image
        leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent ->
                viewPreviousAction.run());

        // The action code to load the next
        // image to be displayed.
        Runnable viewNextAction = () -> {
            LOGGER.log(Level.INFO, "busy loading? " + loading.get());
            // if no next image or currently loading.
            if (currentIndex == IMAGE_FILES.size()-1
                    || loading.get()) return;

            int indx = gotoImageIndex(ButtonMove.NEXT);
            if (indx > -1) {
                loadImage(IMAGE_FILES.get(indx));
            }
        };

        // Right arrow button
        Pane rightButton = new Pane();
        Arc rightButtonArc = new Arc(15, 12, 15, 15, 180-30, 60);
        rightButton.getChildren().add(rightButtonArc);
        rightButtonArc.setType(ArcType.ROUND);
        rightButtonArc.getStyleClass().add("right-arrow");

        // Right arrow key pressed will display the next image
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.RIGHT
                    && !keyEvent.isShortcutDown()) {
                viewNextAction.run();
            }
        });

        // Mouse press event on left button will display the next image
        rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED,
                mouseEvent -> viewNextAction.run());

        HBox buttonHbox = new HBox();
        buttonHbox.getStyleClass().add("button-panel");
        HBox.setHgrow(leftButton, Priority.ALWAYS);
        HBox.setHgrow(rightButton, Priority.ALWAYS);
        HBox.setMargin(leftButton, new Insets(0,5,0,5));
        HBox.setMargin(rightButton, new Insets(0,5,0,5));
        buttonHbox.getChildren().addAll(leftButton, rightButton);

        buttonStackPane.getChildren().addAll(buttonHbox);

        return buttonStackPane;
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
     * @param imageInfo ImageInfo instance containing a url string
     *                  representation of the path to the image file.
     *                  The imageInfo also has the degrees in rotation.
     * @return Task worker task to load image and display into ImageView control.
     */
    private Task createWorker(ImageInfo imageInfo) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                // On the worker thread...
                Image image = new Image(imageInfo.getUrl(), false);
                Platform.runLater(() -> {
                    // On the JavaFX Application Thread....
                    LOGGER.log(Level.INFO, "done loading image "
                            + imageInfo.getUrl());
                    currentImageView.setImage(image);
                    currentImageView.setRotate(imageInfo.getDegrees());
                    currentImageView.setEffect(imageInfo.getColorAdjust());
                    colorAdjustProperty.setValue(imageInfo.getColorAdjust());
                    updateSliders(imageInfo.getColorAdjust());
                    progressIndicator.setVisible(false);
                    loading.set(false); // free lock
                });
                return true;
            }
        };
    }
    
    /**
     * This method loads an image,
     * updates a progress bar and spawns a new thread.
     * If another process is already loading 
     * the method will return without loading.
     * @param imageInfo ImageInfo instance containing a url string
     *                  representation of the path to the image file.
     *                  The imageInfo also has the degrees in rotation.
     */
    private void loadImage(ImageInfo imageInfo) {
        // do not begin task until current 
        // task is finished loading (atomic)
        if (!loading.getAndSet(true)) { 
            LOGGER.log(Level.INFO, "loadImage spawned ");
            Task loadImage = createWorker(imageInfo);
            progressIndicator.setVisible(true);
            progressIndicator.progressProperty().unbind();
            progressIndicator.progressProperty()
                             .bind(loadImage.progressProperty());
            new Thread(loadImage).start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }  
}


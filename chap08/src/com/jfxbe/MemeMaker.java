package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A MemeMaker Application that allows the user to
 * Create a Meme using images and text. This application
 * demonstrates the JavaFX printing APIs.
 * @author carldea
 */
public class MemeMaker extends Application {
    /** Standard Logger. */
    private final static Logger LOGGER = Logger
            .getLogger(MemeMaker.class.getName());

    /** Current image view display */
    protected ImageView _currentViewImage;

    private int _fontSize = 80;
    /** Single threaded service for loading an image */
    protected ExecutorService _executorService =
            Executors.newSingleThreadScheduledExecutor();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Shutdown thread service
        _executorService.shutdown();
    }

    @Override
    public void init() {
//        try {
//            Font.getFamilies()
//                .forEach( font ->
//                  System.out.println(font.toString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("MemeMaker");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.WHITE);
        scene.getStylesheets()
                .add(getClass()
                        .getClassLoader()
                        .getResource("meme-maker.css")
                        .toExternalForm());
        primaryStage.setScene(scene);

        // Anchor Pane
        AnchorPane mainContentPane = new AnchorPane();

        // Meme as a container to hold the image view and Text
        Pane memeContent = new Pane();
        AnchorPane.setTopAnchor(memeContent, 0.0);
        AnchorPane.setLeftAnchor(memeContent, 0.0);

        // Current image view
        _currentViewImage = createImageView();
        memeContent.getChildren().add(_currentViewImage);

        // Create a progress indicator
        ProgressIndicator progressIndicator = createProgressIndicator();

        // layer items. Items that are last are on top
        mainContentPane.getChildren()
                       .addAll(memeContent, progressIndicator);

        // Create a new meme text
        // Create menus File, Rotate, Color adjust menus
        Menu fileMenu = createFileMenu(primaryStage, progressIndicator,
                memeContent);
        // Create a meme menu, with font size, Add text
        Menu memeMenu = createMemeMenu(memeContent);

        MenuBar menuBar = new MenuBar(fileMenu, memeMenu);
        root.setTop(menuBar);

        // Create the center content of the root pane (Border)
        // Make sure the center content is under the menu bar
        BorderPane.setAlignment(mainContentPane, Pos.TOP_LEFT);
        StackPane centerArea = new StackPane(mainContentPane);

        // Red box denoting the print region
        Node printRegion = generatePrintRegion();
        centerArea.getChildren().add(printRegion);
        StackPane.setAlignment(printRegion, Pos.TOP_LEFT);
        root.setCenter(centerArea);

        // When nodes are visible they can be repositioned.
        primaryStage.setOnShown( event ->
                wireupUIBehavior(primaryStage, progressIndicator));

        primaryStage.show();
    }

    private Node generatePrintRegion() {
        Path printPerimeter = new Path();
        Printer printer = Printer.getDefaultPrinter();
        double printWidth = printer.getDefaultPageLayout().getPrintableWidth();
        double printHeight = printer.getDefaultPageLayout().getPrintableHeight();
        PathElement[] corners = {
                new MoveTo(0,0),
                new LineTo(printWidth, 0),
                new LineTo(printWidth, printHeight),
                new LineTo(0, printHeight),
                new ClosePath()
        };
        printPerimeter.getElements().addAll(corners);
        printPerimeter.setStroke(Color.RED);
        return printPerimeter;
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
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

    private Menu createMemeMenu(Pane memeContent) {
        Menu memeMenu = new Menu("Meme");

        Menu fontSizeMenu = new Menu("Font Size");
        ToggleGroup tGroup = new ToggleGroup();

        RadioMenuItem fontSize20MenuItem = new RadioMenuItem("20");
        fontSize20MenuItem.setUserData(20);
        fontSize20MenuItem.setToggleGroup(tGroup);

        RadioMenuItem fontSize30MenuItem = new RadioMenuItem("30");
        fontSize30MenuItem.setUserData(30);
        fontSize30MenuItem.setToggleGroup(tGroup);

        RadioMenuItem fontSize40MenuItem = new RadioMenuItem("40");
        fontSize40MenuItem.setUserData(40);
        fontSize40MenuItem.setToggleGroup(tGroup);

        RadioMenuItem fontSize50MenuItem = new RadioMenuItem("50");
        fontSize50MenuItem.setUserData(50);
        fontSize50MenuItem.setToggleGroup(tGroup);

        RadioMenuItem fontSize60MenuItem = new RadioMenuItem("60");
        fontSize60MenuItem.setUserData(60);
        fontSize60MenuItem.setToggleGroup(tGroup);

        RadioMenuItem fontSize80MenuItem = new RadioMenuItem("80");
        fontSize80MenuItem.setUserData(80);
        fontSize80MenuItem.setToggleGroup(tGroup);

        tGroup.selectToggle(fontSize80MenuItem);
        fontSizeMenu.getItems().addAll(fontSize20MenuItem,
                fontSize30MenuItem,
                fontSize40MenuItem,
                fontSize50MenuItem,
                fontSize60MenuItem,
                fontSize80MenuItem);

        tGroup.selectedToggleProperty().addListener(listener -> {
            if (tGroup.getSelectedToggle() != null) {
                _fontSize = (int) tGroup.getSelectedToggle().getUserData();
                System.out.println(tGroup.getSelectedToggle().getUserData());
            }
        });

        // create a meme text
        MenuItem addMemeTextItem = new MenuItem("Add Meme Text");
        addMemeTextItem.setOnAction( actionEvent -> {
            MemeTextControl memeText = new MemeTextControl(
                    memeContent.getScene(), _fontSize);
            memeContent.getChildren()
                       .add(memeText);
        });
        memeMenu.getItems().addAll(fontSizeMenu, addMemeTextItem);

        return memeMenu;
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
                                ProgressIndicator progressIndicator,
                                Pane memeContent) {
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
        wireupSaveMenuItem(saveAsMenuItem, stage, memeContent);

        // Print the current screen
        MenuItem printMenuItem = new MenuItem("_Print");
        printMenuItem.setMnemonicParsing(true);
        printMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P,
                KeyCombination.SHORTCUT_DOWN));
        // launch print dialog
        wireupPrintMenuItem(printMenuItem, memeContent);

        // Quit application
        MenuItem exitMenuItem = new MenuItem("_Quit");
        exitMenuItem.setMnemonicParsing(true);
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.SHORTCUT_DOWN));

        // exiting
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(loadImagesMenuItem,
                saveAsMenuItem, printMenuItem, exitMenuItem);

        return fileMenu;
    }

    private void wireupPrintMenuItem(MenuItem printMenuItem, Pane memeContent ) {
        // On a Mac go to Settings -> Printers & Scanners
        //    Make a printer default
        //    (check the box) Share this printer on the network.

        printMenuItem.setOnAction( actionEvent -> {

            PrinterJob job = PrinterJob.createPrinterJob();
            job.jobStatusProperty().addListener(listener -> {
                System.out.println("status " + job.getJobStatus());
            });
            if (job != null &&
                    job.showPrintDialog(memeContent.getScene().getWindow())){
                if (job.getJobStatus() == PrinterJob.JobStatus.NOT_STARTED) {
                    System.out.println("canceled");
                }
                boolean success = job.printPage(memeContent);
                if (success) {
                    job.endJob();
                    return;
                }
            }
        });
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
                        "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif")
        );

        menuItem.setOnAction( actionEvt -> {
            File imageFile = fileChooser.showOpenDialog(primaryStage);
            if (imageFile != null) {
                try {
                    String url = imageFile.toURI().toURL().toString();
                    if (isValidImageFile(url)) {
                        loadAndDisplayImage(progressIndicator, url);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
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
                                      Stage primaryStage,
                                      Pane memeContent) {
        menuItem.setOnAction( actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File fileSave = fileChooser.showSaveDialog(primaryStage);
            if (fileSave != null) {

                WritableImage image = memeContent.snapshot(
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
    /** Wireup behavior of UI elements and actions.
     * This method is called after the stage and scene is shown.
     * @param primaryStage Main application window.
     * @param progressIndicator node indicating load progress.
     */
    private void wireupUIBehavior(Stage primaryStage,
                                  ProgressIndicator progressIndicator) {
        Scene scene = primaryStage.getScene();

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
     * @param url String of the url point to the image file.
     */
    protected void loadAndDisplayImage(ProgressIndicator progressIndicator, String url) {


        // show spinner while image is loading
        progressIndicator.setVisible(true);

        //String urlStr = String.valueOf(_currentViewImage.getUserData());
        Task<Image> loadImage = createWorker(url);

        // after loading has succeeded apply image info
        loadImage.setOnSucceeded(workerStateEvent -> {

            try {
                _currentViewImage.setImage(loadImage.get());
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

                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.consume();
            }
        });

        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();

            String file = null;
            // image from the local file system.
            if (db.hasFiles() && !db.hasUrl()) {
                try {
                    file = db.getFiles()
                             .get(0)
                             .toURI()
                             .toURL()
                             .toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                file = db.getUrl();
            }
            LOGGER.log(Level.FINE, "dropped file: "+ file);
            if (isValidImageFile(file)) {
                loadAndDisplayImage(progressIndicator, file);
            }

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

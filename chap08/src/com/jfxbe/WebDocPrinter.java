package com.jfxbe;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Allows the user to enter a URL to display an HTML page to be
 * sent to a default printer. Also the application allows the
 * display node to be resize to fit onto the printed page.
 */
public class WebDocPrinter extends Application{
    private static String PRINT_MODE_MENU = "Print Mode";
    private static String NODE_ONLY = "Node Only";
    private static String WHOLE_WEB_DOC = "Whole Web Document";
    public static void main(String[] args) {
        //System.setProperty("jsse.enableSNIExtension", "false");
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the root pane and scene
        primaryStage.setTitle("WebDocPrinter");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.WHITE);
        primaryStage.setScene(scene);

        // Create a menu
        MenuBar menuBar = new MenuBar();
        Menu printModeMenu = new Menu("Print Mode");
        ToggleGroup printModeGroup = new ToggleGroup();

        // Node only print mode
        RadioMenuItem printOnePage = new RadioMenuItem(NODE_ONLY);
        printOnePage.setUserData(NODE_ONLY);
        printOnePage.setToggleGroup(printModeGroup);
        printModeGroup.selectToggle(printOnePage);
        printModeMenu.setText(PRINT_MODE_MENU + " (" + NODE_ONLY + ")");

        // Whole web document print mode
        RadioMenuItem multiPages = new RadioMenuItem("Whole Web Document");
        multiPages.setUserData(WHOLE_WEB_DOC);
        multiPages.setToggleGroup(printModeGroup);

        printModeMenu.getItems().addAll(printOnePage, multiPages);
        menuBar.getMenus().add(printModeMenu);
        root.setTop(menuBar);

        BorderPane contentPane = new BorderPane();
        root.setCenter(contentPane);

        // Create display area
        WebView browserDisplay = new WebView();

        // Create a slider to control zoom
        Slider zoomSlider = new Slider(.05, 3.0,1.0);
        zoomSlider.setBlockIncrement(0.05);
        zoomSlider.valueProperty().addListener( listener -> {
            System.out.println("zoom " + browserDisplay.getZoom());
            browserDisplay.setZoom(zoomSlider.getValue());
        });

        // Label representing the zoom size percentage
        Label zoomValueLabel = new Label();
        StringConverter sc = new StringConverter<Double>(){

            @Override public Double fromString(String value) {
                // If the value is null or empty string return null
                if (value == null) {
                    return null;
                }

                value = value.trim();
                value.replace("%", "");
                if (value.length() < 1) {
                    return null;
                }

                return Double.valueOf(value)/100;
            }

            @Override public String toString(Double value) {
                //If the value is null, return empty string
                if (value == null) {
                    return "";
                }
                double percent = value.doubleValue() * 100;
                return String.format("%.2f", percent) + "%";
            }
        };

        // Bind Label's text and the Slider's value property.
        Bindings.bindBidirectional(zoomValueLabel.textProperty(),
                zoomSlider.valueProperty(), sc);

        // debug information
        browserDisplay.widthProperty().addListener( listener -> {
            Printer printer = Printer.getDefaultPrinter();
            System.out.println("printer width: " +
                    printer.getDefaultPageLayout().getPrintableWidth());
            System.out.println("width: " +
                    browserDisplay.widthProperty().get() );
        });

        WebEngine webEngine = browserDisplay.getEngine();
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener( (obsValue, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        System.out.println("finished loading webpage: " +
                                webEngine.getLocation());
                    }
                });

        // Create an address bar
        TextField urlAddressField = new TextField();
        urlAddressField.setPromptText("Enter URL of a page to print");
        urlAddressField.setOnAction( actionEvent ->
                webEngine.load(urlAddressField.getText()));

        // Create the print button
        Button printButton = new Button("Print");
        printButton.setOnAction(actionEvent -> {

           PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                System.out.println("starting print job");
                Toggle selected = printModeGroup.getSelectedToggle();
                if (selected != null) {

                    String mode = (String) selected.getUserData();
                    if (NODE_ONLY.equals(mode)) {
                        boolean printIt = job.printPage(browserDisplay);
                        if (printIt) {
                            job.endJob();
                        }
                    } else {
                        // WHOLE_WEB_DOC
                        boolean success = job.showPrintDialog(primaryStage);
                        if (success) {
                            webEngine.print(job);
                            job.endJob();
                        }
                    }
                }
            }
        });

        // Assemble print button, zoom slider, zoom label
        VBox vBox = new VBox();
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(printButton, zoomSlider, zoomValueLabel);
        vBox.getChildren().addAll(urlAddressField, hBox);

        contentPane.setTop(vBox);

        // Center WebView area
        StackPane centerArea = new StackPane(browserDisplay);

        // Create the red box denoting print area.
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
        StackPane.setAlignment(printPerimeter, Pos.TOP_LEFT);
        centerArea.getChildren().add(printPerimeter);
        contentPane.setCenter(centerArea);

        printModeGroup.selectedToggleProperty()
                .addListener((observableValue) -> {

            Toggle selected = printModeGroup.getSelectedToggle();
            if (selected != null) {
                String mode = String.valueOf(selected.getUserData());
                printModeMenu.setText(PRINT_MODE_MENU + " (" + mode + ")");
                if (NODE_ONLY.equals(mode)) {
                    printPerimeter.setVisible(true);
                } else {
                    printPerimeter.setVisible(false);
                }
            }
        });

        primaryStage.setOnShown( eventHandler -> {
            printButton.requestFocus();
        });

        primaryStage.show();
    }
}

package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 */
public class RectanglePrinterTest extends Application{

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("WebDocPrinter");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 551, 400, Color.WHITE);
        primaryStage.setScene(scene);

        //Canvas rowHeader = new Canvas(50, 50);
        Pane centerContent = new Pane();
        Rectangle rectangle300 = new Rectangle(0, 0, 300, 300);
        Rectangle rectangle111 = new Rectangle(0, 0, 111, 111);
        Rectangle rectangle96 = new Rectangle(0, 0, 96, 96);
        Rectangle rectangle72 = new Rectangle(0, 0, 72, 72);
        Rectangle[] rects =  {rectangle300, rectangle111, rectangle96, rectangle72};
        for(Rectangle r:rects) {
            r.setStroke(Color.BLACK);
            r.setFill(Color.TRANSPARENT);
            centerContent.getChildren().add(r);
        }
        Button print = new Button("Print");
        print.setOnAction(actionEvent -> {
            Printer printer = Printer.getDefaultPrinter();
            //PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
//            double scaleX = pageLayout.getPrintableWidth() / centerContent.getBoundsInParent().getWidth();
//            double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
//            centerContent.getTransforms().add(new Scale(scaleX, scaleY));

            PrinterJob job = PrinterJob.createPrinterJob();
            JobSettings jobSettings = job.getJobSettings();
            jobSettings.setPrintColor(PrintColor.MONOCHROME);
            jobSettings.setPrintSides(PrintSides.DUPLEX);
            jobSettings.setPrintQuality(PrintQuality.LOW);
            PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

            job.getJobSettings().setPageLayout(pageLayout);

            if (job != null) {
                boolean success = job.printPage(centerContent);
                if (success) {
                    job.endJob();
                }
            }
        });
        root.setTop(print);
        root.setCenter(centerContent);
        Platform.runLater(() -> {
            double dpi =javafx.stage.Screen.getPrimary().getDpi();
            System.out.println("dpi = " + dpi);
        });
        primaryStage.show();
    }

}

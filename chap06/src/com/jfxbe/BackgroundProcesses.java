package com.jfxbe;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author cdea
 */
public class BackgroundProcesses extends Application {
    
    static Task<Boolean> copyWorker;
    final int numFiles = 30;
    private ExecutorService threadPool;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        threadPool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        threadPool.shutdown();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BackgroundProcesses: Background Processes");
        Group root = new Group();
        Scene scene = new Scene(root, 330, 120, Color.WHITE);

        BorderPane mainPane = new BorderPane();
        mainPane.layoutXProperty().bind(scene.widthProperty().subtract(mainPane.widthProperty()).divide(2));
        root.getChildren().add(mainPane);

        final Label label = new Label("Files Transfer:");
        final ProgressBar progressBar = new ProgressBar(0);
        final ProgressIndicator progressIndicator = new ProgressIndicator(0);

        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(label, progressBar, progressIndicator);
        mainPane.setTop(hb);


        final Button startButton = new Button("Start");
        final Button cancelButton = new Button("Cancel");
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(200, 70);
        final HBox hb2 = new HBox();
        hb2.setSpacing(5);
        hb2.setAlignment(Pos.CENTER);
        hb2.getChildren().addAll(startButton, cancelButton, textArea);
        mainPane.setBottom(hb2);

        // wire up start button
        startButton.setOnAction((ActionEvent event) -> {
            startButton.setDisable(true);
            progressBar.setProgress(0);
            progressIndicator.setProgress(0);
            textArea.setText("");
            cancelButton.setDisable(false);
            copyWorker = createWorker(numFiles);

            // wire up progress bar
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(copyWorker.progressProperty());
            progressIndicator.progressProperty().unbind();
            progressIndicator.progressProperty().bind(copyWorker.progressProperty());

            // append to text area box
            copyWorker.messageProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                textArea.appendText(newValue + "\n");
            });

            threadPool.submit(copyWorker);
        });

        // cancel button will kill worker and reset.
        cancelButton.setOnAction((ActionEvent event) -> {
            startButton.setDisable(false);
            cancelButton.setDisable(true);
            copyWorker.cancel(true);

            // reset
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            progressIndicator.progressProperty().unbind();
            progressIndicator.setProgress(0);
            textArea.appendText("File transfer was cancelled.");
        });
        cancelButton.setDisable(true);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Task<Boolean> createWorker(final int numFiles) {
        return new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {
                for (int i = 0; i < numFiles; i++) {
                    long elapsedTime = System.currentTimeMillis();
                    copyFile("some file", "some dest file");
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    String status = elapsedTime + " milliseconds";

                    // queue up status
                    updateMessage(status);
                    updateProgress(i + 1, numFiles); // (progress, max)
                }
                return true;
            }
        };
    }

    private void copyFile(String src, String dest) throws InterruptedException {
        // simulate a long time
        Random rnd = new Random(System.currentTimeMillis());
        long millis = rnd.nextInt(1000);
        Thread.sleep(millis);
    }
    
}

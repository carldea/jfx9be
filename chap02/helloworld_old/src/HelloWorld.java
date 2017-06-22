package com.jfxbe.helloworld;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.sun.net.httpserver.HttpServer;
/**
 * A JavaFX Hello World
 * @author carldea
 */
public class HelloWorld extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	try {
        	HttpServer http = HttpServer.create();
	} catch(Throwable th) {
 		th.printStackTrace();
        }
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World");
        Group root = new Group();
        Scene scene = new Scene(root, 300, 250);
        Button btn = new Button();
        btn.setLayoutX(100);
        btn.setLayoutY(80);
        btn.setText("Hello World");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                System.out.println("Hello World");
            }
        });
        root.getChildren().add(btn);
        stage.setScene(scene);
        stage.show();
    }
}

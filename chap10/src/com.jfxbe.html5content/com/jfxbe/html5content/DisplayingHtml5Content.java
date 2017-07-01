package com.jfxbe.html5content;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;

/** An SVG analog clock rendered using the WebView node.
 * @author cdea
 */
public class DisplayingHtml5Content extends Application {

    @Override
    public void start(Stage primaryStage) throws MalformedURLException {
        primaryStage.setTitle("Displaying Html5 Content");
        WebView browser = new WebView();
        Scene scene = new Scene(browser,320,250, Color.rgb(0, 0, 0, .80));
        primaryStage.setScene(scene);

        URL url = getClass().getResource("clock.svg");
        browser.getEngine().load(url.toExternalForm());
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

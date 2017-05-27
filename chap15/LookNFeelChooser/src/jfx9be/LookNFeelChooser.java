package jfx9be;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author cdea
 */
public class LookNFeelChooser extends Application {

    @Override public void init() {
        Font.loadFont(LookNFeelChooser.class.getResourceAsStream("Roboto-Thin.ttf"), 10).getName();
        Font.loadFont(LookNFeelChooser.class.getResourceAsStream("Roboto-Light.ttf"), 10).getName();
    }

    @Override public void start(Stage primaryStage) throws IOException {
        BorderPane root    = new BorderPane();
        Parent     content = FXMLLoader.load(getClass().getResource("lnf_demo.fxml"));
        Scene      scene   = new Scene(root, 650, 550, Color.WHITE);
        root.setCenter(content);

        // Menu bar
        MenuBar menuBar = new MenuBar();

        // file menu
        Menu     fileMenu = new Menu("_File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        exitItem.setOnAction(ae -> Platform.exit());

        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);

        // Look and feel menu
        Menu lookNFeelMenu = new Menu("_Look 'N' Feel");
        lookNFeelMenu.setMnemonicParsing(true);
        menuBar.getMenus().add(lookNFeelMenu);
        root.setTop(menuBar);

        // Look and feel selection
        MenuItem caspianMenuItem = new MenuItem("Caspian");
        caspianMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            //setUserAgentStylesheet(null);
            setUserAgentStylesheet(STYLESHEET_CASPIAN);
        });
        MenuItem modenaMenuItem = new MenuItem("Modena");
        modenaMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            //setUserAgentStylesheet(null);
            setUserAgentStylesheet(STYLESHEET_MODENA);
        });
        MenuItem style1MenuItem = new MenuItem("Control Style 1");
        style1MenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                 .add(getClass()
                 .getResource("controlStyle1.css")
                 .toExternalForm());
        });
        MenuItem style2MenuItem = new MenuItem("Control Style 2");
        style2MenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                 .add(getClass()
                 .getResource("controlStyle2.css")
                 .toExternalForm());
        });

        MenuItem skyMenuItem = new MenuItem("Sky LnF");
        skyMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                 .add(getClass()
                 .getResource("sky.css")
                 .toExternalForm());
        });

        MenuItem flatRedMenuItem = new MenuItem("FlatRed");
        flatRedMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                 .add(getClass()
                 .getResource("flatred.css")
                 .toExternalForm());
        });

        lookNFeelMenu.getItems()
                     .addAll(caspianMenuItem,
                             modenaMenuItem,
                             style1MenuItem,
                             style2MenuItem,
                             skyMenuItem,
                             flatRedMenuItem);

        primaryStage.setTitle("Look N Feel Chooser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

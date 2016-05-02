package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author cdea
 */
public class KeyCombinationsAndContextMenus extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 530, 350, Color.WHITE);

        final StringProperty statusProperty = new SimpleStringProperty();

        InnerShadow iShadow = new InnerShadow();
        iShadow.setOffsetX(3.5f);
        iShadow.setOffsetY(3.5f);
        final Text status = new Text();
        status.setEffect(iShadow);
        status.setX(100);
        status.setY(50);
        status.setFill(Color.LIME);
        status.setFont(Font.font(null, FontWeight.BOLD, 35));
        //status.setTranslateY(50);

        status.textProperty().bind(statusProperty);
        statusProperty.set("Keyboard Shortcuts\n"
                + "Ctrl-N, \n"
                + "Ctrl-S, \n"
                + "Ctrl-X \n"
                + "Ctrl-Shift-E");
        root.setCenter(status);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty()
               .bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        Menu fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        menuBar.getMenus().add(fileMenu);

        MenuItem newItem = new MenuItem("_New");       
        newItem.setMnemonicParsing(true);

        newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, 
                KeyCombination.SHORTCUT_DOWN));
        newItem.setOnAction(actionEvent -> statusProperty.set("Ctrl-N"));
        fileMenu.getItems().add(newItem);

        MenuItem saveItem = new MenuItem("_Save");
        saveItem.setMnemonicParsing(true);
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, 
                KeyCombination.SHORTCUT_DOWN));
        saveItem.setOnAction(actionEvent -> statusProperty.set("Ctrl-S"));
        fileMenu.getItems().add(saveItem);

        fileMenu.getItems().add(new SeparatorMenuItem());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.X, 
                KeyCombination.SHORTCUT_DOWN));

        exitItem.setOnAction(actionEvent -> {
            statusProperty.set("Ctrl-X");
            Platform.exit();
        });

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.E, 
                                       KeyCombination.SHORTCUT_DOWN, 
                                       KeyCombination.SHIFT_DOWN),
                () -> statusProperty.set("Ctrl-Shift-E")
        );

        fileMenu.getItems().add(exitItem);

        ContextMenu contextFileMenu = new ContextMenu(exitItem);

        primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY) {
                System.out.println("Right mouse button click");
                contextFileMenu.show(root, me.getScreenX(), me.getScreenY());
            } else {
                contextFileMenu.hide();
            }
        });


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}

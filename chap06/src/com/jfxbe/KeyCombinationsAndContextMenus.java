package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.*;
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

    String osKeyAccel = "";
    @Override
    public void start(Stage primaryStage) {


        if (System.getProperties()
                  .get("os.name")
                  .toString()
                  .toLowerCase()
                  .startsWith("mac")){
            osKeyAccel = "⌘";
        } else {
            // Linux or Windows will be using the Ctrl key
            osKeyAccel = "Ctrl";
        }
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
        status.textProperty().bind(statusProperty);

        statusProperty.set("Keyboard Shortcuts\n"
                + osKeyAccel + "-N, \n"
                + osKeyAccel + "-S, \n"
                + osKeyAccel + "-X \n"
                + osKeyAccel + "-Shift-E");
        root.setCenter(status);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty()
               .bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // File
        //   New
        //   Save
        //   Erase
        //   Exit

        Menu fileMenu = new Menu("_File");
        fileMenu.setMnemonicParsing(true);
        menuBar.getMenus().add(fileMenu);

        MenuItem newItem = createMenuItem("_New",
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
                actionEvent -> statusProperty.set(osKeyAccel + "-N"));

        MenuItem saveItem = createMenuItem("_Save",
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                actionEvent -> statusProperty.set(osKeyAccel + "-S"));

        KeyCodeCombination eraseKeyCombo = new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN);
        MenuItem eraseItem = createMenuItem("_Erase",
                eraseKeyCombo,
                actionEvent -> statusProperty.set(osKeyAccel + "-Shift-E"));

        MenuItem exitItem = createMenuItem("E_xit",
                new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN),
                (actionEvent) -> {
                    statusProperty.set(osKeyAccel + "-X");
                    Platform.exit();
                });

        fileMenu.getItems().addAll(newItem, saveItem, eraseItem, new SeparatorMenuItem(), exitItem);

        Runnable eraseAction = () -> statusProperty.set(osKeyAccel + "-Shift-E");
        scene.getAccelerators().put(eraseKeyCombo, eraseAction);

        MenuItem contextNewItem = createMenuItem("_New", newItem.getAccelerator(), newItem.getOnAction());
        MenuItem contextSaveItem = createMenuItem("_Save", saveItem.getAccelerator(), saveItem.getOnAction());
        MenuItem contextEraseItem = createMenuItem("_Erase", eraseItem.getAccelerator(), eraseItem.getOnAction());

        MenuItem contextExitItem = createMenuItem("E_xit", exitItem.getAccelerator(), exitItem.getOnAction());

        ContextMenu contextFileMenu = new ContextMenu();
        contextFileMenu.getItems().addAll(contextNewItem,
                contextSaveItem, contextEraseItem,
                new SeparatorMenuItem(), contextExitItem);

        primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY) {
                contextFileMenu.show(root, me.getScreenX(), me.getScreenY());
            } else {
                contextFileMenu.hide();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuItem createMenuItem(String name,
                                    KeyCombination keyCombination,
                                    EventHandler<ActionEvent> handler) {

        MenuItem menuItem = new MenuItem(name);
        menuItem.setMnemonicParsing(true);
        menuItem.setAccelerator(keyCombination);
        menuItem.setOnAction(handler);

        return menuItem;
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}

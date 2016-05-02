package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * MenusExample simulates an security alarm application. This
 * demonstrates various menu and menu item selections.
 * 
 * @author cdea
 */
public class MenusExample extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MenusExample: Menus Example");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 300, 250, Color.WHITE);

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar); 

        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit() );

        fileMenu.getItems().addAll(newMenuItem, 
                saveMenuItem, 
                new SeparatorMenuItem(), 
                exitMenuItem
        );
        
        // Cameras menu - camera 1, camera 2
        Menu cameraMenu = new Menu("Cameras");
        CheckMenuItem cam1MenuItem = new CheckMenuItem("Show Camera 1");
        cam1MenuItem.setSelected(true);
        cameraMenu.getItems().add(cam1MenuItem);

        CheckMenuItem cam2MenuItem = new CheckMenuItem("Show Camera 2");
        cam2MenuItem.setSelected(true);
        cameraMenu.getItems().add(cam2MenuItem);


        // Alarm menu
        Menu alarmMenu = new Menu("Alarm");

        // sound or turn alarm off
        ToggleGroup tGroup = new ToggleGroup();
        RadioMenuItem soundAlarmItem = new RadioMenuItem("Sound Alarm"); 
        soundAlarmItem.setToggleGroup(tGroup);

        RadioMenuItem stopAlarmItem = new RadioMenuItem("Alarm Off");
        stopAlarmItem.setToggleGroup(tGroup);
        stopAlarmItem.setSelected(true);

        alarmMenu.getItems().addAll(
                soundAlarmItem, 
                stopAlarmItem, 
                new SeparatorMenuItem());

        Menu contingencyPlans = new Menu("Contingent Plans");
        contingencyPlans.getItems().addAll(
                new CheckMenuItem("Self Destruct in T minus 50"),
                new CheckMenuItem("Turn off the coffee machine "),
                new CheckMenuItem("Run for your lives! "));

        alarmMenu.getItems().add(contingencyPlans);

        menuBar.getMenus().addAll(fileMenu, cameraMenu, alarmMenu);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

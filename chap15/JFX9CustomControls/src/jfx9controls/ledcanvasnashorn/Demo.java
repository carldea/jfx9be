/*
 * Copyright (c) 2013. by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jfx9controls.ledcanvasnashorn;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by
 * User: hansolo
 * Date: 18.09.13
 * Time: 08:20
 */
public class Demo extends Application {
    private static final String SCRIPT_FILE_NAME = "Led.js";
    private static int          noOfNodes = 0;

    private ScriptEngineManager manager;
    private ScriptEngine        engine;
    private String              script;
    private Invocable           inv;
    private Object              scriptObject;
    private Pane                control;


    @Override public void init() {
        manager = new ScriptEngineManager();
        engine  = manager.getEngineByName("nashorn");
        script  = getScript();
        try {
            engine.eval(script);
            inv = (Invocable) engine;
            scriptObject = engine.get("led");
        } catch(ScriptException exception) {
            System.out.println(exception);
        }
    }

    private String getScript() {
        StringBuilder scriptContent = new StringBuilder();
        try {                                    
            Path         path  = Paths.get((Demo.class.getResource(SCRIPT_FILE_NAME).toString().replace("file:", "")));            
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            lines.forEach(line -> scriptContent.append(line));
            System.out.println("Script: " + SCRIPT_FILE_NAME + " successfully loaded");
        } catch (IOException exception) {
            System.out.println("Error loading " + SCRIPT_FILE_NAME + " file");
        }
        return scriptContent.toString();
    }

    @Override public void start(Stage stage) {
        try {
            control = (Pane) inv.invokeMethod(scriptObject, "getInstance");
            inv.invokeMethod(scriptObject, "setBlinking", true);
        } catch(ScriptException | NoSuchMethodException exception) {
            System.out.println(exception);
        }

        StackPane pane = new StackPane();
        pane.getChildren().setAll(control);

        Scene scene = new Scene(pane, 100, 100);

        stage.setTitle("JavaFX Led Canvas Nashorn");
        stage.setScene(scene);
        stage.show();

        calcNoOfNodes(scene.getRoot());
        System.out.println(noOfNodes + " Nodes in SceneGraph");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


    // ******************** Misc **********************************************
    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                for (Node n : tempChildren) {
                    calcNoOfNodes(n);
                    //System.out.println(n.getStyleClass().toString());
                }
            }
        }
    }
}

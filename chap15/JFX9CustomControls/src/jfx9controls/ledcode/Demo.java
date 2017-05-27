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

package jfx9controls.ledcode;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jfx9controls.ledcss.LedBuilder;


public class Demo extends Application {
    private static int noOfNodes = 0;

    @Override public void start(Stage stage) {
        jfx9controls.ledcss.Led control = LedBuilder.create()
                                       .prefWidth(200)
                                       .prefHeight(200)
                                       .build();

        StackPane pane = new StackPane();
        pane.getChildren().setAll(control);

        Scene scene = new Scene(pane);

        stage.setTitle("JavaFX Led Code");
        stage.setScene(scene);
        stage.show();

        control.setBlinking(true);

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


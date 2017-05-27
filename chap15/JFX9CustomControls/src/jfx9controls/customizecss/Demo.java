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

package jfx9controls.customizecss;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Created by
 * User: hansolo
 * Date: 30.08.13
 * Time: 07:55
 */

public class Demo extends Application {

    @Override public void start(Stage stage) {
        Button styledButton = new Button("My Button");
        styledButton.getStyleClass().add("my-button");

        Button stdButton = new Button("Std. Button");

        VBox pane = new VBox();
        pane.setSpacing(20);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.getChildren().addAll(styledButton, stdButton);

        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("JavaFX Custom CSS style");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(final String[] args) {
        Application.launch(args);
    }
}

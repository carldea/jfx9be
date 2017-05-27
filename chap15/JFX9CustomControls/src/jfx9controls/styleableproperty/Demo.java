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

package jfx9controls.styleableproperty;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfx9controls.csspseudoclass.MyCtrl;


/**
 * Created by
 * User: hansolo
 * Date: 25.07.13
 * Time: 15:10
 */

public class Demo extends Application {
    private MyCtrl myCtrl;
    private ToggleButton buttonStyleable;

    @Override public void init() {
        myCtrl           = new MyCtrl();
        buttonStyleable = new ToggleButton("LIME");
        registerListeners();
    }

    private void registerListeners() {
        buttonStyleable.setOnAction(actionEvent -> {
            if (buttonStyleable.isSelected()) {
                //myCtrl.setAreaColor(Color.LIME);
                myCtrl.setStyle("-area-color: #00FF00");
                buttonStyleable.setText("BLUE");
            } else {
                //myCtrl.setAreaColor(Color.BLUE);
                myCtrl.setStyle("-area-color: #0000FF");
                buttonStyleable.setText("LIME");
            }
        });
    }

    @Override public void start(Stage stage) throws Exception {
        VBox pane = new VBox();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(10);
        pane.getChildren().addAll(myCtrl, buttonStyleable);
        VBox.setMargin(myCtrl, new Insets(10, 10, 10, 10));

        Scene scene = new Scene(pane);

        stage.setTitle("StyleableProperty");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {

    }

    public static void main(final String[] args) {
        Application.launch(args);
    }
}

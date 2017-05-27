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

package jfx9controls.csspseudoclass.skin;


import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import jfx9controls.csspseudoclass.MyCtrl;


/**
 * Created by
 * User: hansolo
 * Date: 28.08.13
 * Time: 17:09
 */

public class MyCtrlSkin extends SkinBase<MyCtrl> implements Skin<MyCtrl> {
    private static final double PREFERRED_WIDTH  = 100;
    private static final double PREFERRED_HEIGHT = 100;
    private static final double MINIMUM_WIDTH    = 8;
    private static final double MINIMUM_HEIGHT   = 8;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    private double              width;
    private double              height;
    private Region              region;
    private Pane                pane;


    // ******************** Constructors **************************************
    public MyCtrlSkin(final MyCtrl CONTROL) {
        super(CONTROL);
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() > 0 && getSkinnable().getPrefHeight() > 0) {
                getSkinnable().setPrefSize(getSkinnable().getPrefWidth(), getSkinnable().getPrefHeight());
            } else {
                getSkinnable().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        region = new Region();
        region.getStyleClass().add("area");

        pane = new Pane();
        pane.getChildren().setAll(region);
        getChildren().setAll(pane);
        resize();
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().areaColorProperty().addListener(observable -> handleControlPropertyChanged("AREA_COLOR"));                
    }
        

    // ******************** Methods *******************************************
    protected void handleControlPropertyChanged(final String PROPERTY) {
        if ("RESIZE".equals(PROPERTY)) {
            resize();
        } else if ("AREA_COLOR".equals(PROPERTY)) {
            region.setStyle("-area-color: " + getSkinnable().getAreaColor().toString().replace("0x", "#"));
        }
    }
   

    // ******************** Private Methods ***********************************
    private void resize() {
        width  = getSkinnable().getWidth();
        height = getSkinnable().getHeight();

        if (width > 0 && height > 0) {
            region.setPrefSize(width, height);
        }
    }
}

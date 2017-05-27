/*
 * Copyright (c) 2014. by Gerrit Grunwald
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

package jfx9controls.templates.region;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;


/**
 * User: hansolo
 * Date: 13.02.14
 * Time: 11:16
 */
public class Template extends Region {
    private static final double PREFERRED_WIDTH  = 150;
    private static final double PREFERRED_HEIGHT = 150;
    private static final double MINIMUM_WIDTH    = 5;
    private static final double MINIMUM_HEIGHT   = 5;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    
    private        double  size;    // current smallest dimension
    private        double  width;   // current width
    private        double  height;  // current height
    private        Region  sample;  // sample region
    private        Pane    pane;    // main layout container that holds all your nodes


    // ******************** Constructors **************************************
    public Template() {
        /* LOAD THE APPROPRIATE STYLE SHEET FILE */
        getStylesheets().add(getClass().getResource("template.css").toExternalForm());
        
        /* ADD THE MAIN STYLE CLASS */ 
        getStyleClass().add("template");
        
        /* INITIALZE THE CONTROL */
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }
        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }
        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }        
    }

    private void initGraphics() {
        // Setup the scene graph of your control here
        
        sample = new Region();
        sample.getStyleClass().setAll("sample-style");
        
        pane = new Pane(sample /* ADD ALL YOUR NODES TO THIS PANE */);
        
        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize()); 
        /* ADD LISTENERS TO YOUR PROPERTIES BELOW THIS */
    }


    // ******************** Methods *******************************************
    
    

    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth();
        height = getHeight();
        size   = width < height ? width : height;        
        
        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.relocate((width - size) * 0.5, (height - size) * 0.5);
            
            /* RESIZE AND RELOCATE YOUR NODES BELOW THIS */
            sample.setPrefSize(0.5 * size, 0.5 * size);
            sample.relocate(0.25 * size, 0.25 * size);
        }
    }
}

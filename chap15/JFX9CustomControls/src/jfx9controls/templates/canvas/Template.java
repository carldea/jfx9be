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

package jfx9controls.templates.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * User: hansolo
 * Date: 13.02.14
 * Time: 11:50
 */
public class Template extends Region {
    private static final double PREFERRED_WIDTH  = 150;
    private static final double PREFERRED_HEIGHT = 150;
    private static final double MINIMUM_WIDTH    = 5;
    private static final double MINIMUM_HEIGHT   = 5;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    
    private Paint           sampleRegionFill;
    private Canvas          canvas;
    private GraphicsContext ctx;


    // ******************** Constructors **************************************
    public Template() {        
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
        canvas = new Canvas();
        ctx = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> recalc());
        heightProperty().addListener(observable -> recalc()); 
        /* ADD LISTENERS TO YOUR PROPERTIES BELOW THIS */
    }


    // ******************** Methods *******************************************   

    
    
    // ******************** Resize/Redraw *************************************
    private void recalc() {        
        sampleRegionFill = Color.RED;
        draw();
    }

    private void draw() {
        double width  = getWidth();
        double height = getHeight();
        if (width <= 0 || height <= 0) return;

        double size   = width < height ? width : height;

        canvas.setWidth(size);
        canvas.setHeight(size);
        if (width > height) {
            canvas.relocate(0.5 * (width - size), 0);
        } else if (height > width) {
            canvas.relocate(0, 0.5 * (height - size));
        }

        ctx.clearRect(0, 0, size, size);
        
        /* RESIZE AND RELOCATE YOUR CANVAS SHAPES BELOW THIS */
        ctx.setFill(sampleRegionFill);            
        ctx.fillRect(0.25 * size, 0.25 * size, 0.5 * size, 0.5 * size);
    }
}


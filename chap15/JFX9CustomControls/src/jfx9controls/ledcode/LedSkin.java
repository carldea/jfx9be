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

import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;


public class LedSkin extends SkinBase<Led> implements Skin<Led> {
    private static final double PREFERRED_SIZE = 16;
    private static final double MINIMUM_SIZE   = 8;
    private static final double MAXIMUM_SIZE   = 1024;
    private double              size;
    private Circle              frame;
    private Paint               frameFill;
    private Circle              main;
    private Paint               mainOnFill;
    private Paint               mainOffFill;
    private Circle              highlight;
    private Paint               highlightFill;
    private InnerShadow         innerShadow;
    private DropShadow          glow;


    // ******************** Constructors **************************************
    public LedSkin(final Led CONTROL) {
        super(CONTROL);
        init();
        initGraphics();
        registerListeners();
    }

    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getSkinnable().getWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() > 0 && getSkinnable().getPrefHeight() > 0) {
                getSkinnable().setPrefSize(getSkinnable().getPrefWidth(), getSkinnable().getPrefHeight());
            } else {
                getSkinnable().setPrefSize(PREFERRED_SIZE, PREFERRED_SIZE);
            }
        }

        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_SIZE, MINIMUM_SIZE);
        }

        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_SIZE, MAXIMUM_SIZE);
        }
    }

    private void initGraphics() {
        size   = getSkinnable().getPrefWidth() < getSkinnable().getPrefHeight() ? getSkinnable().getPrefWidth() : getSkinnable().getPrefHeight();

        frame = new Circle(0.5 * size, 0.5 * size, 0.5 * size);
        frame.setStroke(null);
        frame.setVisible(getSkinnable().isFrameVisible());

        main = new Circle(0.5 * size, 0.5 * size, 0.36 * size);
        main.setStroke(null);

        innerShadow = new InnerShadow();
        innerShadow.setRadius(0.090 * main.getLayoutBounds().getWidth());
        innerShadow.setColor(Color.BLACK);
        innerShadow.setBlurType(BlurType.GAUSSIAN);
        innerShadow.setInput(null);

        glow = new DropShadow();
        glow.setRadius(0.45 * main.getLayoutBounds().getWidth());
        glow.setColor((Color) getSkinnable().getLedColor());
        glow.setBlurType(BlurType.GAUSSIAN);
        glow.setInput(innerShadow);

        highlight = new Circle(0.5 * size, 0.5 * size, 0.29 * size);
        highlight.setStroke(null);

        getChildren().setAll(frame, main, highlight);
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().ledColorProperty().addListener(observable -> handleControlPropertyChanged("COLOR"));
        getSkinnable().onProperty().addListener(observable -> handleControlPropertyChanged("ON"));
        getSkinnable().frameVisibleProperty().addListener(observable -> handleControlPropertyChanged("FRAME_VISIBLE"));
    }


    // ******************** Methods *******************************************
    protected void handleControlPropertyChanged(final String PROPERTY) {
        if ("RESIZE".equals(PROPERTY)) {
            resize();
        } else if ("COLOR".equals(PROPERTY)) {
            resize();
        } else if ("ON".equals(PROPERTY)) {
            if (getSkinnable().isOn()) {
                main.setFill(mainOnFill);
                main.setEffect(glow);
            } else {
                main.setFill(mainOffFill);
                main.setEffect(innerShadow);
            }
        } else if ("FRAME_VISIBLE".equals(PROPERTY)) {
            frame.setOpacity(getSkinnable().isFrameVisible() ? 1.0 : 0.0);
        }
    }
   

    // ******************** Resizing ******************************************
    private void resize() {
        size   = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();

        frameFill = new LinearGradient(0.14 * size, 0.14 * size,
                                       0.84 * size, 0.84 * size,
                                       false, CycleMethod.NO_CYCLE,
                                       new Stop(0.0, Color.color(0.0784313725, 0.0784313725, 0.0784313725, 0.6470588235)),
                                       new Stop(0.15, Color.color(0.0784313725, 0.0784313725, 0.0784313725, 0.6470588235)),
                                       new Stop(0.26, Color.color(0.1607843137, 0.1607843137, 0.1607843137, 0.6470588235)),
                                       new Stop(0.2600001, Color.color(0.1607843137, 0.1607843137, 0.1607843137, 0.6431372549)),
                                       new Stop(0.85, Color.color(0.7843137255, 0.7843137255, 0.7843137255, 0.4039215686)),
                                       new Stop(1.0, Color.color(0.7843137255, 0.7843137255, 0.7843137255, 0.3450980392)));

        frame.setRadius(0.5 * size);
        frame.setCenterX(0.5 * size);
        frame.setCenterY(0.5 * size);
        frame.setFill(frameFill);

        mainOnFill = new LinearGradient(0.25 * size, 0.25 * size,
                                        0.74 * size, 0.74 * size,
                                        false, CycleMethod.NO_CYCLE,
                                        new Stop(0.0, Color.color(0.7098039216, 0, 0, 1)),
                                        new Stop(0.49, Color.color(0.4392156863, 0, 0, 1)),
                                        new Stop(1.0, Color.color(0.9843137255, 0, 0, 1)));

        mainOffFill = new LinearGradient(0.25 * size, 0.25 * size,
                                         0.74 * size, 0.74 * size,
                                         false, CycleMethod.NO_CYCLE,
                                         new Stop(0.0, Color.color(0.2039215686, 0.0588235294, 0.0784313725, 1)),
                                         new Stop(0.49, Color.color(0.1450980392, 0, 0, 1)),
                                         new Stop(1.0, Color.color(0.2039215686, 0.0588235294, 0.0784313725, 1)));

        innerShadow.setRadius(0.07 * size);
        glow.setRadius(0.36 * size);
        glow.setColor((Color) getSkinnable().getLedColor());

        main.setRadius(0.36 * size);
        main.setCenterX(0.5 * size);
        main.setCenterY(0.5 * size);
        if (getSkinnable().isOn()) {
            main.setFill(mainOnFill);
            main.setEffect(glow);
        } else {
            main.setFill(mainOffFill);
            main.setEffect(innerShadow);
        }

        highlightFill = new RadialGradient(0, 0,
                                           0.3 * size, 0.3 * size,
                                           0.29 * size,
                                           false, CycleMethod.NO_CYCLE,
                                           new Stop(0.0, Color.WHITE),
                                           new Stop(1.0, Color.TRANSPARENT));

        highlight.setRadius(0.29 * size);
        highlight.setCenterX(0.5 * size);
        highlight.setCenterY(0.5 * size);
        highlight.setFill(highlightFill);
    }
}

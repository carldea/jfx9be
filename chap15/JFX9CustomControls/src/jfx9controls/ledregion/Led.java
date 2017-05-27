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

package jfx9controls.ledregion;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


public class Led extends Region {
    private static final double      PREFERRED_SIZE    = 16;
    private static final double      MINIMUM_SIZE      = 8;
    private static final double      MAXIMUM_SIZE      = 1024;
    private static final PseudoClass ON_PSEUDO_CLASS   = PseudoClass.getPseudoClass("on");
    private static final long        SHORTEST_INTERVAL = 50_000_000l;
    private static final long        LONGEST_INTERVAL  = 5_000_000_000l;
    // Model/Controller related
    private ObjectProperty<Color>    ledColor;
    private BooleanProperty          on;
    private boolean                  _blinking = false;
    private BooleanProperty          blinking;
    private boolean                  _frameVisible = true;
    private BooleanProperty          frameVisible;
    private long                     lastTimerCall;
    private long                     _interval = 500_000_000l;
    private LongProperty             interval;
    private AnimationTimer           timer;
    // View related
    private double                   size;
    private Region                   frame;
    private Region                   led;
    private Region                   highlight;
    private InnerShadow              innerShadow;
    private DropShadow               glow;


    // ******************** Constructors **************************************
    public Led() {
        getStylesheets().add(getClass().getResource("led.css").toExternalForm());
        getStyleClass().add("led");
                
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(final long NOW) {
                if (NOW > lastTimerCall + getInterval()) {                    
                    setOn(!isOn());
                    lastTimerCall = NOW;
                }
            }
        };
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getWidth(), 0) <= 0 || Double.compare(getHeight(), 0) <= 0 ||
            Double.compare(getPrefWidth(), 0) <= 0 || Double.compare(getPrefHeight(), 0) <= 0) {
            setPrefSize(PREFERRED_SIZE, PREFERRED_SIZE);
        }
        if (Double.compare(getMinWidth(), 0) <= 0 || Double.compare(getMinHeight(), 0) <= 0) {
            setMinSize(MINIMUM_SIZE, MINIMUM_SIZE);
        }
        if (Double.compare(getMaxWidth(), 0) <= 0 || Double.compare(getMaxHeight(), 0) <= 0) {
            setMaxSize(MAXIMUM_SIZE, MAXIMUM_SIZE);
        }
    }

    private void initGraphics() {
        frame = new Region();
        frame.getStyleClass().setAll("frame");
        frame.setOpacity(isFrameVisible() ? 1 : 0);

        led = new Region();
        led.getStyleClass().setAll("main");
        led.setStyle("-led-color: " + (getLedColor()).toString().replace("0x", "#") + ";");

        innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 8, 0d, 0d, 0d);

        glow = new DropShadow(BlurType.TWO_PASS_BOX, getLedColor(), 20, 0d, 0d, 0d);
        glow.setInput(innerShadow);

        highlight = new Region();
        highlight.getStyleClass().setAll("highlight");

        // Add all nodes
        getChildren().addAll(frame, led, highlight);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize());
        frameVisibleProperty().addListener(observable -> frame.setOpacity(isFrameVisible() ? 1 : 0));
        onProperty().addListener(observable -> led.setEffect(isOn() ? glow : innerShadow));
        ledColorProperty().addListener(observable -> {
            led.setStyle("-led-color: " + (getLedColor()).toString().replace("0x", "#") + ";");
            resize();
        });
    }


    // ******************** Methods *******************************************   
    public final boolean isOn() {
        return null == on ? false : on.get();
    }
    public final void setOn(final boolean ON) {
        onProperty().set(ON);
    }
    public final BooleanProperty onProperty() {
        if (null == on) {
            on = new BooleanPropertyBase(false) {
                @Override protected void invalidated() { pseudoClassStateChanged(ON_PSEUDO_CLASS, get()); }
                @Override public Object getBean() { return this; }
                @Override public String getName() { return "on"; }
            };
        }
        return on;
    }

    public final boolean isBlinking() {
        return null == blinking ? _blinking : blinking.get();
    }
    public final void setBlinking(final boolean BLINKING) {
        if (null == blinking) {
            _blinking = BLINKING;
            if (BLINKING) {
                timer.start();
            } else {
                timer.stop();
                setOn(false);
            }
        } else {
            blinking.set(BLINKING);
        }
    }
    public final BooleanProperty blinkingProperty() {
        if (null == blinking) {            
            blinking = new BooleanPropertyBase() {
                @Override public void set(final boolean BLINKING) {
                    super.set(BLINKING);
        if (BLINKING) {
            timer.start();
        } else {
            timer.stop();
            setOn(false);
        }
    }
                @Override public Object getBean() {
                    return Led.this;
                }
                @Override public String getName() {
                    return "blinking";
                }
            };
        }
        return blinking;
    }

    public final long getInterval() {
        return null == interval ? _interval : interval.get();
    }
    public final void setInterval(final long INTERVAL) {
        if (null == interval) {
            _interval = clamp(SHORTEST_INTERVAL, LONGEST_INTERVAL, INTERVAL);
        } else {
            interval.set(INTERVAL);
        }
    }
    public final LongProperty intervalProperty() {
        if (null == interval) {
            interval = new LongPropertyBase() {
                @Override public void set(final long INTERVAL) {
                    super.set(clamp(SHORTEST_INTERVAL, LONGEST_INTERVAL, INTERVAL));
                }
                @Override public Object getBean() {
                    return Led.this;
                }
                @Override public String getName() {
                    return "interval";
                }
            };
        }
        return interval;
    }

    public final boolean isFrameVisible() {
        return null == frameVisible ? _frameVisible : frameVisible.get();
    }
    public final void setFrameVisible(final boolean FRAME_VISIBLE) {
        if (null == frameVisible) {
            _frameVisible = FRAME_VISIBLE;
        } else {
            frameVisible.set(FRAME_VISIBLE);
        }
    }
    public final BooleanProperty frameVisibleProperty() {
        if (null == frameVisible) {
            frameVisible = new SimpleBooleanProperty(this, "frameVisible", _frameVisible);
        }
        return frameVisible;
    }

    public final Color getLedColor() {
        return null == ledColor ? Color.RED : ledColor.get();
    }
    public final void setLedColor(final Color LED_COLOR) {
        ledColorProperty().set(LED_COLOR);
    }
    public final ObjectProperty<Color> ledColorProperty() {
        if (null == ledColor) {
            ledColor = new SimpleObjectProperty<>(this, "ledColor", Color.RED);
        }
        return ledColor;
    }


    // ******************** Utility Methods ***********************************
    public static long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        size = getWidth() < getHeight() ? getWidth() : getHeight();
        if (size > 0) {
            if (getWidth() > getHeight()) {
                setTranslateX(0.5 * (getWidth() - size));
            } else if (getHeight() > getWidth()) {
                setTranslateY(0.5 * (getHeight() - size));
            }

            innerShadow.setRadius(0.07 * size);
            glow.setRadius(0.36 * size);
            glow.setColor(getLedColor());

            frame.setPrefSize(size, size);

            led.setPrefSize(0.72 * size, 0.72 * size);
            led.relocate(0.14 * size, 0.14 * size);
            led.setEffect(isOn() ? glow : innerShadow);

            highlight.setPrefSize(0.58 * size, 0.58 * size);
            highlight.relocate(0.21 * size, 0.21 * size);
        }
    }
}

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

import javafx.css.converter.PaintConverter;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Led extends Control {
    private static final Color       DEFAULT_LED_COLOR = Color.RED;
    private static final PseudoClass ON_PSEUDO_CLASS   = PseudoClass.getPseudoClass("on");
    private static final long        SHORTEST_INTERVAL = 50_000_000l;
    private static final long        LONGEST_INTERVAL  = 5_000_000_000l;

    // CSS pseudo classes
    private BooleanProperty       on;

    // CSS styleable properties
    private ObjectProperty<Paint> ledColor;

    // Properties
    private boolean               _blinking = false;
    private BooleanProperty       blinking;
    private boolean               _frameVisible = true;
    private BooleanProperty       frameVisible;
    private long                  lastTimerCall;
    private long                  _interval = 500_000_000l;
    private LongProperty          interval;
    private AnimationTimer        timer;


    // ******************** Constructors **************************************
    public Led() {
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


    // ******************** CSS Stylable Properties ***************************
    public final Paint getLedColor() {
        return null == ledColor ? DEFAULT_LED_COLOR : ledColor.get();
    }
    public final void setLedColor(Paint value) {
        ledColorProperty().set(value);
    }
    public final ObjectProperty<Paint> ledColorProperty() {
        if (null == ledColor) {
            ledColor = new StyleableObjectProperty<Paint>(DEFAULT_LED_COLOR) {
                @Override public CssMetaData getCssMetaData() { return StyleableProperties.LED_COLOR; }
                @Override public Object getBean() { return Led.this; }
                @Override public String getName() { return "ledColor"; }
            };
        }
        return ledColor;
    }


    // ******************** Utility Methods ***********************************
    public static long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }


    // ******************** Style related *************************************
    @Override protected Skin createDefaultSkin() {
        return new LedSkin(this);
    }

    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("led.css").toExternalForm();
    }

    private static class StyleableProperties {
        private static final CssMetaData<Led, Paint> LED_COLOR =
            new CssMetaData<Led, Paint>("-led-color", PaintConverter.getInstance(), DEFAULT_LED_COLOR) {

                @Override public boolean isSettable(Led led) {
                    return null == led.ledColor || !led.ledColor.isBound();
                }

                @Override public StyleableProperty<Paint> getStyleableProperty(Led led) {
                    return (StyleableProperty) led.ledColorProperty();
                }

                @Override public Color getInitialValue(Led led) {
                    return (Color) led.getLedColor();
                }
            };


        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                LED_COLOR
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}

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

package jfx9controls.csspseudoclass;

import javafx.css.converter.PaintConverter;
import jfx9controls.csspseudoclass.skin.MyCtrlSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
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


public class MyCtrl extends Control {
    // CSS Pseudo Class
    private static final PseudoClass INTERACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("interactive");
    private BooleanProperty          interactive;

    // CSS StyleableProperty
    public static final Color        DEFAULT_AREA_COLOR = Color.YELLOW;
    private ObjectProperty<Paint>    areaColor;


    // ******************** Constructors **************************************
    public MyCtrl() {
        getStyleClass().add("my-control");
    }


    // ******************** CSS Pseudo Class **********************************
    public final boolean isInteractive() {
        return null == interactive ? false : interactive.get();
    }
    public final void setInteractive(final boolean interactive) {
        interactiveProperty().set(interactive);
    }
    public final BooleanProperty interactiveProperty() {
        if (null == interactive) {
            interactive = new BooleanPropertyBase() {
                @Override protected void invalidated() { pseudoClassStateChanged(INTERACTIVE_PSEUDO_CLASS, get()); }
                @Override public Object getBean() { return this; }
                @Override public String getName() { return "interactive"; }
            };
        }
        return interactive;
    }


    // ******************** CSS Stylable Properties ***************************
    public final Paint getAreaColor() {
        return null == areaColor ? DEFAULT_AREA_COLOR : areaColor.get();
    }
    public final void setAreaColor(Paint value) {
        areaColorProperty().set(value);
    }
    public final ObjectProperty<Paint> areaColorProperty() {
        if (null == areaColor) {
            areaColor = new StyleableObjectProperty<Paint>(DEFAULT_AREA_COLOR) {
                @Override public CssMetaData getCssMetaData() { return StyleableProperties.AREA_COLOR; }
                @Override public Object getBean() { return MyCtrl.this; }
                @Override public String getName() { return "areaColor"; }
            };
        }
        return areaColor;
    }


    // ******************** CSS Meta Data *************************************
    private static class StyleableProperties {
        private static final CssMetaData<MyCtrl, Paint> AREA_COLOR =
            new CssMetaData<MyCtrl, Paint>("-area-color", PaintConverter.getInstance(), DEFAULT_AREA_COLOR) {

                @Override public boolean isSettable(MyCtrl node) {
                    return null == node.areaColor || !node.areaColor.isBound();
                }

                @Override public StyleableProperty<Paint> getStyleableProperty(MyCtrl node) {
                    return (StyleableProperty) node.areaColorProperty();
                }

                @Override public Paint getInitialValue(MyCtrl node) {
                    return node.getAreaColor();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                               AREA_COLOR);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }


    // ******************** Style related *************************************
    @Override protected Skin createDefaultSkin() {
        return new MyCtrlSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("csspseudoclass.css").toExternalForm();
    }
}

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

var System                 = Java.type("java.lang.System");
var AnimationTimer         = Java.type("javafx.animation.AnimationTimer");
var InvalidationListener   = Java.type("javafx.beans.InvalidationListener");
var BooleanProperty        = Java.type("javafx.beans.property.BooleanProperty");
var LongProperty           = Java.type("javafx.beans.property.LongProperty");
var ObjectProperty         = Java.type("javafx.beans.property.ObjectProperty");
var SimpleBooleanProperty  = Java.type("javafx.beans.property.SimpleBooleanProperty");
var SimpleLongProperty     = Java.type("javafx.beans.property.SimpleLongProperty");
var SimpleObjectProperty   = Java.type("javafx.beans.property.SimpleObjectProperty");
var Canvas                 = Java.type("javafx.scene.canvas.Canvas");
var GraphicsContext        = Java.type("javafx.scene.canvas.GraphicsContext");
var BlurType               = Java.type("javafx.scene.effect.BlurType");
var DropShadow             = Java.type("javafx.scene.effect.DropShadow");
var InnerShadow            = Java.type("javafx.scene.effect.InnerShadow");
var Pane                   = Java.type("javafx.scene.layout.StackPane");
var Color                  = Java.type("javafx.scene.paint.Color");
var CycleMethod            = Java.type("javafx.scene.paint.CycleMethod");
var LinearGradient         = Java.type("javafx.scene.paint.LinearGradient");
var Paint                  = Java.type("javafx.scene.paint.Paint");
var RadialGradient         = Java.type("javafx.scene.paint.RadialGradient");
var Stop                   = Java.type("javafx.scene.paint.Stop");

var Led = function() {
    var PREFERRED_SIZE     = 64;
    var MINIMUM_SIZE       = 8;
    var MAXIMUM_SIZE       = 1024;
    var SHORTEST_INTVERVAL = 50000000;
    var LONGEST_INTERVAL   = 5000000000;
    var size;

    var region             = new Pane();
    var canvas;
    var ctx;
    var ledColor           = new SimpleObjectProperty(Color.RED);
    var ledOn              = new SimpleBooleanProperty(false);
    var blinking           = new SimpleBooleanProperty(false);
    var frameVisible       = new SimpleBooleanProperty(true);    
    var lastTimerCall      = System.nanoTime();
    var interval           = new SimpleLongProperty(500000000);
    var timer              = new AnimationTimer() {
        handle: function handle(now) {
            if (now > lastTimerCall + interval.get()) {                
                ledOn.set(!ledOn.get());
                lastTimerCall = now;
            }
        }
    };

    var init = function() {
        if (region.width <= 0 || region.height <= 0 || region.prefWidth <= 0 || region.prefHeight <= 0) {
            region.prefWidth  = PREFERRED_SIZE;
            region.prefHeight = PREFERRED_SIZE;
        }
        if (region.minWidth <= 0 || region.minHeight <= 0) {
            region.minWidth  = MINIMUM_SIZE;
            region.minHeight = MINIMUM_SIZE;
        }
        if (region.maxWidth <= 0 || region.maxHeight <= 0) {
            region.maxWidth  = MAXIMUM_SIZE;
            region.maxHeight = MAXIMUM_SIZE;
        }
    };
    var initGraphics = function() {
        canvas = new Canvas();
        ctx    = canvas.graphicsContext2D;
        region.getChildren().add(canvas);
    };
    var registerListeners = function() {
        region.widthProperty().addListener(new InvalidationListener() {invalidated: function invalidated(observable) {draw();}});
        region.heightProperty().addListener(new InvalidationListener() {invalidated: function invalidated(observable) {draw();}});
        frameVisible.addListener(new InvalidationListener() { invalidated: function invalidated(observable) { draw(); }});
        ledOn.addListener(new InvalidationListener() { invalidated: function invalidated(observable) { draw(); }});
        ledColor.addListener(new InvalidationListener() { invalidated: function invalidated(observable) { draw(); }});
    };

    this.getInstance = function() {
        return region;
    };

    this.isBlinking = function() {
        return blinking.get();
    };
    this.setBlinking = function(isBlinking) {
        blinking.set(isBlinking);
        if (isBlinking) {
            timer.start();
        } else {
            timer.stop();
        }
    };
    this.blinkingProperty = function() {
        return blinking;
    };

    this.getInterval = function() {
        return interval.get();
    };
    this.setInterval = function(newInterval) {
        interval.set(clamp(SHORTEST_INTVERVAL, LONGEST_INTERVAL, newInterval));
    };
    this.intervalProperty = function() {
        return interval;
    };

    this.isLedOn  = function() {
        return ledOn.get();
    };
    this.setLedOn = function(isLedOn) {
        ledOn.set(isLedOn);
    };
    this.ledOnProperty = function() {
        return ledOn;
    };

    this.isFrameVisible = function() {
        return frameVisible.get();
    };
    this.setFrameVisible = function(isFrameVisible) {
        frameVisible.set(isFrameVisible);
    };
    this.frameVisibleProperty = function() {
        return frameVisible;
    };

    this.getLedColor = function() {
        return ledColor.get();
    };
    this.setLedColor = function(newLedColor) {
        ledColor.set(newLedColor);
    };
    this.ledColorProperty = function() {
        return ledColor;
    };

    var clamp = function(min, max, value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    };

    var draw = function() {
        size = region.getWidth() < region.getHeight() ? region.getWidth() : region.getHeight();

        canvas.width  = size;
        canvas.height = size;

        ctx.clearRect(0, 0, size, size);

        if (frameVisible.get()) {
            var frame = new LinearGradient(0.14 * size, 0.14 * size,
                0.84 * size, 0.84 * size,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(20, 20, 20, 0.65)),
                new Stop(0.15, Color.rgb(20, 20, 20, 0.65)),
                new Stop(0.26, Color.rgb(41, 41, 41, 0.65)),
                new Stop(0.26, Color.rgb(41, 41, 41, 0.64)),
                new Stop(0.85, Color.rgb(200, 200, 200, 0.41)),
                new Stop(1.0, Color.rgb(200, 200, 200, 0.35)));
            ctx.setFill(frame);
            ctx.fillOval(0, 0, size, size);
        }

        var innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), 0.07 * size, 0, 0, 0);

        if (ledOn.get()) {
            ctx.save();
            var on = new LinearGradient(0.25 * size, 0.25 * size,
                                        0.74 * size, 0.74 * size,
                                        false, CycleMethod.NO_CYCLE,
                                        new Stop(0.0, ledColor.get().deriveColor(0.0, 1.0, 0.77, 1.)),
                                        new Stop(0.49, ledColor.get().deriveColor(0.0, 1.0, 0.5, 1.0)),
                                        new Stop(1.0, ledColor.get()));
            innerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, ledColor.get(), 0.36 * size, 0, 0, 0));
            ctx.setEffect(innerShadow);
            ctx.setFill(on);
            ctx.fillOval(0.14 * size, 0.14 * size, 0.72 * size, 0.72 * size);
            ctx.restore();
        } else {
            ctx.save();
            var off = new LinearGradient(0.25 * size, 0.25 * size,
                                         0.74 * size, 0.74 * size,
                                         false, CycleMethod.NO_CYCLE,
                                         new Stop(0.0, ledColor.get().deriveColor(0.0, 1.0, 0.20, 1.0)),
                                         new Stop(0.49, ledColor.get().deriveColor(0.0, 1.0, 0.13, 1.0)),
                                         new Stop(1.0, ledColor.get().deriveColor(0.0, 1.0, 0.2, 1.0)));
            ctx.setEffect(innerShadow);
            ctx.setFill(off);
            ctx.fillOval(0.14 * size, 0.14 * size, 0.72 * size, 0.72 * size);
            ctx.restore();
        }

        var highlight = new RadialGradient(0, 0,
            0.3 * size, 0.3 * size,
            0.29 * size,
            false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.WHITE),
            new Stop(1.0, Color.TRANSPARENT));
        ctx.setFill(highlight);
        ctx.fillOval(0.21 * size, 0.21 * size, 0.58 * size, 0.58 * size);
    };

    init();
    initGraphics();
    registerListeners();
};

var led = new Led();

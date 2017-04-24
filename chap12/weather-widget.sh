#!/usr/bin/env bash
# Compile
javac -d mods/com.jfxbe.weatherwidget $(find src/com.jfxbe.weatherwidget -name "*.java")

# Copy resource
cp src/com.jfxbe.weatherwidget/com/jfxbe/weatherwidget/weather_template.html \
mods/com.jfxbe.weatherwidget/com/jfxbe/weatherwidget

# Run Application
java --module-path mods -m com.jfxbe.weatherwidget/com.jfxbe.weatherwidget.WeatherWidget

# This creates a jar file to be run
# jar --create --file=mlib/com.jfxbe.weatherwidget.jar --main-class=com.jfxbe.weatherwidget.WeatherWidget -C mods/com.jfxbe.weatherwidget .

#!/usr/bin/env bash
# Compile
javac -d mods/com.jfxbe.html5content $(find src/com.jfxbe.html5content -name "*.java")

# Copy resource
cp src/com.jfxbe.html5content/com/jfxbe/html5content/clock.svg \
mods/com.jfxbe.html5content/com/jfxbe/html5content

# Run Application
java --module-path mods -m \
com.jfxbe.html5content/com.jfxbe.html5content.DisplayingHtml5Content

# Creates a jar file to be run as a standalone app.
# mkdir mlib
# jar --create --file=mlib/com.jfxbe.html5content.jar --main-class=com.jfxbe.html5content.DisplayingHtml5Content -C mods/com.jfxbe.html5content .



# clean artifacts, compile code, copy and build, package jar into mlib
./clean
./compile
./build
./package

# Runs each individual main apps in the jar as a library.

java -cp mlib/chap05.jar com.jfxbe.GridPaneForm
java -cp mlib/chap05.jar com.jfxbe.FXMLContactForm
java -cp mlib/chap05.jar com.jfxbe.HBoxExample
java -cp mlib/chap05.jar com.jfxbe.VBoxExample

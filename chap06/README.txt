
# When packaging you must be in jdk 9. 

# Clean artifacts, compile code, copy and build, package jar into mlib
./clean
./compile
./build
./package

# Runs each individual main apps in the jar as a library.

java -cp mlib/chap06.jar:lib/fontawesomefx-8.9.jar com.jfxbe.LabelAwesome
java -cp classpath com.jfxbe.ButtonFun
java -cp mlib/chap06.jar com.jfxbe.KeyCombinationsAndContextMenus
java -cp mlib/chap06.jar com.jfxbe.BossesAndEmployees
java -cp mlib/chap06.jar com.jfxbe.BackgroundProcesses
java -cp mlib/chap06.jar com.jfxbe.HeroPicker
java -cp mlib/chap06.jar com.jfxbe.MenusExample
java -cp mlib/chap06.jar com.jfxbe.CheckBoxDemo
java -cp mlib/chap06.jar com.jfxbe.CustomFonts
java -cp mlib/chap06.jar com.jfxbe.RadioButtonDemo


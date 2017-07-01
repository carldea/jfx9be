
# When packaging you must be in jdk 9. 

# Clean artifacts, compile code, copy and build, package jar into mlib
./clean
./compile
./build
./package

# Runs each individual main apps in the jar as a library.
java -cp mlib/chap10.jar com.jfxbe.MemeMaker
java -cp mlib/chap10.jar com.jfxbe.QueryPrinterAttributes
java -cp mlib/chap10.jar com.jfxbe.RectanglePrinterTest
java -cp mlib/chap10.jar com.jfxbe.SaveInkAndTrees
java -cp mlib/chap10.jar com.jfxbe.WebDocPrinter

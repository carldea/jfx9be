
# When packaging you must be in jdk 9. 

# Clean artifacts, compile code, copy and build, package jar into mlib
./clean
./compile
./build
./package

# Runs each individual main apps in the jar as a library.
java -cp mlib/chap07.jar com.jfxbe.PhotoViewer
java -cp mlib/chap07.jar com.jfxbe.PointAndClickGame
java -cp mlib/chap07.jar com.jfxbe.PhotoViewer2

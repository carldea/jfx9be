
# When packaging you must be in jdk 9. 

# Clean artifacts, compile code, copy and build, package jar into mlib
./clean
./compile
./build
./package

# Runs each individual main apps in the jar as a library.
# Drag and drop an mp3 or wav file onto the surface of the PlayingAudio application
java -cp mlib/chap11.jar com.jfxbe.PlayingAudio

# Download a flv or mp4 file to be dragged on the surface of the PlayingVideo or 
# ClosedCaptionVideo application.
# http://www.mediacollege.com/video-gallery/testclips/20051210-w50s.flv
# http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv
java -cp mlib/chap11.jar com.jfxbe.PlayingVideo
java -cp mlib/chap11.jar com.jfxbe.ClosedCaptionVideo

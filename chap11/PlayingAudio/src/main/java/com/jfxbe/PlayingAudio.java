package com.jfxbe;

import java.net.MalformedURLException;
import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;

/**
 * Playing Audio using JavaFX media API.
 *
 * @author cdea
 */
public class PlayingAudio extends Application {

   private MediaPlayer mediaPlayer;
   private Point2D anchorPt;
   private Point2D previousLocation;
   private ChangeListener<Duration> progressListener;
   private Path chartArea = new Path();

   private static final String STOP_BUTTON_ID = "stop-button";
   private static final String PLAY_BUTTON_ID = "play-button";
   private static final String PAUSE_BUTTON_ID = "pause-button";
   private static final String CLOSE_BUTTON_ID = "close-button";
   private static final String VIS_CONTAINER_ID = "viz-container";
   private static final String SEEK_POS_SLIDER_ID = "seek-position-slider";

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      Application.launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      primaryStage.initStyle(StageStyle.TRANSPARENT);
      primaryStage.centerOnScreen();

      Pane root = new Pane();
      root.setId("app-area");

      Scene scene = new Scene(root, 551, 270, Color.rgb(0, 0, 0, 0));


      // load JavaFX CSS style
      scene.getStylesheets()
           .add(getClass().getResource("/playing-audio.css")
                          .toExternalForm());
      primaryStage.setScene(scene);

      // Initialize stage to be movable via mouse
      initMovablePlayer(primaryStage);

      // Create the button panel
      Node buttonPanel = createButtonPanel(scene);

      // allows the user to see the progress of the video playing
      Slider progressSlider = createSlider(root);

      // update slider as video is progressing (later removal)
      progressListener = (observable, oldValue, newValue) -> 
         progressSlider.setValue(newValue.toSeconds());

      // Initializing to accept files 
      // dragged over surface to load media

      initFileDragNDrop(scene);

      // Create the close button
      Node closeButton = createCloseButton(scene);
      
      root.getChildren()
          .addAll(chartArea,
                  buttonPanel,
                  progressSlider,
                  closeButton);
      
      primaryStage.show();

      // Reposition slider and button panel after CSS is applied and stage is shown.
      progressSlider.setTranslateX(root.getBorder().getInsets().getLeft() + 2 );
      progressSlider.setTranslateY(root.getHeight() - (root.getBorder().getInsets().getBottom() + progressSlider.getHeight()));
      buttonPanel.setTranslateX(root.getWidth() - (buttonPanel.getBoundsInLocal().getWidth() + root.getBorder().getInsets().getRight() + 2));
      buttonPanel.setTranslateY(root.getHeight() - (buttonPanel.getBoundsInLocal().getHeight() + root.getBorder().getInsets().getBottom() + 2));
   }

   /**
    * Initialize the stage to allow the mouse cursor to move the application
    * using dragging.
    *
    */
   private void initMovablePlayer(Stage primaryStage) {
      Scene scene = primaryStage.getScene();
      // starting initial anchor point
      scene.setOnMousePressed(mouseEvent ->
              anchorPt = new Point2D(mouseEvent.getScreenX(),
                      mouseEvent.getScreenY())
      );

      // dragging the entire stage
      scene.setOnMouseDragged(mouseEvent -> {
         if (anchorPt != null && previousLocation != null) {
            primaryStage.setX(previousLocation.getX()
                    + mouseEvent.getScreenX()
                    - anchorPt.getX());
            primaryStage.setY(previousLocation.getY()
                    + mouseEvent.getScreenY()
                    - anchorPt.getY());
         }
      });

      // set the current location
      scene.setOnMouseReleased(mouseEvent ->
              previousLocation = new Point2D(primaryStage.getX(),
                      primaryStage.getY())
      );

      // Initialize previousLocation after Stage is shown
      primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN,
              (WindowEvent t) -> {
                 previousLocation = new Point2D(primaryStage.getX(),
                         primaryStage.getY());
              });
   }


   /**
    * Initialize the Drag and Drop ability for media files.
    *
    */
   private void initFileDragNDrop(Scene scene) {

      scene.setOnDragOver(dragEvent -> {
         Dragboard db = dragEvent.getDragboard();
         if (db.hasFiles() || db.hasUrl()) {
            dragEvent.acceptTransferModes(TransferMode.LINK);
         } else {
            dragEvent.consume();
         }
      });
      // Dropping over surface
      scene.setOnDragDropped(dragEvent -> {
         Dragboard db = dragEvent.getDragboard();
         boolean success = false;
         String filePath = null;
         if (db.hasFiles()) {
            success = true;
            if (db.getFiles().size() > 0) {
               try {
                  filePath = db.getFiles()
                          .get(0)
                          .toURI().toURL().toString();
                  playMedia(filePath, scene);
               } catch (MalformedURLException ex) {
                  ex.printStackTrace();
               }
            }
         } else {
            // audio file from some host or jar
            playMedia(db.getUrl(), scene);
            success = true;
         }

         dragEvent.setDropCompleted(success);
         dragEvent.consume();
      }); // end of setOnDragDropped
   }

   /**
    * Creates a node containing the audio player's 
    *  stop, pause and play buttons.
    * 
    * @return Node A button panel having play, 
    *  pause and stop buttons.
    */
   private Node createButtonPanel(Scene scene) {

      // create button control panel
      Group buttonGroup = new Group();

      // Button area
      Rectangle buttonArea = new Rectangle(60, 30);
      buttonArea.setId("button-area");

      buttonGroup.getChildren()
              .add(buttonArea);
      
      // stop button control
      Node stopButton = new Rectangle(10, 10);
      stopButton.setId(STOP_BUTTON_ID);
      stopButton.setOnMousePressed(mouseEvent -> {
         if (mediaPlayer != null) {
            updatePlayAndPauseButtons(true, scene);
            if (mediaPlayer.getStatus() == Status.PLAYING) {
               mediaPlayer.stop();
            }
         }
      }); // setOnMousePressed()
      
      // play button
      Arc playButton = new Arc(12, // center x 
              16, // center y                 
              15, // radius x
              15, // radius y
              150, // start angle
              60);  // length
      playButton.setId(PLAY_BUTTON_ID);
      playButton.setType(ArcType.ROUND);
      playButton.setOnMousePressed(mouseEvent -> mediaPlayer.play());
      
      // pause control
      Group pauseButton = new Group();
      pauseButton.setId(PAUSE_BUTTON_ID);
      Node pauseBackground = new Circle(12, 16, 10);
      pauseBackground.getStyleClass().add("pause-circle");

      Node firstLine = new Line(6,  // start x 
                                6,  // start y  
                                6,  // end x 
                               14); // end y 
      firstLine.getStyleClass()
               .addAll("pause-line", "first-line");

      Node secondLine = new Line(6,   // start x 
                                 6,   // start y  
                                 6,   // end x 
                                 14); // end y 
      secondLine.getStyleClass().addAll("pause-line", "second-line");

      pauseButton.getChildren()
           .addAll(pauseBackground, firstLine, secondLine);
      
      pauseButton.setOnMousePressed(mouseEvent -> {
         if (mediaPlayer!=null) {
            updatePlayAndPauseButtons(true, scene);
            if (mediaPlayer.getStatus() == Status.PLAYING) {
               mediaPlayer.pause();
            }
         }
      });
      
      
      playButton.setOnMousePressed(mouseEvent -> {
         if (mediaPlayer != null) {
            updatePlayAndPauseButtons(false, scene);
            mediaPlayer.play();
         }
      });
      buttonGroup.getChildren()
                 .addAll(stopButton,
                         playButton, 
                         pauseButton); 

      return buttonGroup;
   }

   /**
    * The close button to exit application
    *
    * @return Node representing a close button.
    */
   private Node createCloseButton(Scene scene) {

      StackPane closeButton = new StackPane();
      closeButton.setId(CLOSE_BUTTON_ID);
      Node closeBackground = new Circle(5, 0, 7);
      closeBackground.setId("close-circle");
      Text closeXmark = new Text("X");
      closeButton.translateXProperty()
                 .bind(scene.widthProperty()
                            .subtract(closeBackground.getBoundsInLocal().getWidth() + 3));
      closeButton.setTranslateY(2);
      closeButton.getChildren()
                 .addAll(closeBackground, closeXmark);
      // exit app
      closeButton.setOnMouseClicked(mouseEvent -> {
         if (mediaPlayer != null){
            mediaPlayer.stop();
         }
         Platform.exit();
      });

      return closeButton;
   }

   /**
    * After a file is dragged onto the application a new MediaPlayer 
    * instance is created with a media file.
    *
    * @param url The URL pointing to an audio file
    */
   private void playMedia(String url, Scene scene) {

      if (mediaPlayer != null) {
         mediaPlayer.pause();
         mediaPlayer.setOnPaused(null);
         mediaPlayer.setOnPlaying(null);
         mediaPlayer.setOnReady(null);
         mediaPlayer.currentTimeProperty()
                    .removeListener(progressListener);
         mediaPlayer.setAudioSpectrumListener(null);
      }
      Media media = new Media(url);
      
      mediaPlayer = new MediaPlayer(media);
      
      // as the media is playing move the slider for progress
      mediaPlayer.currentTimeProperty()
                 .addListener(progressListener);

      mediaPlayer.setOnReady(() -> {
         // display media's metadata 
         media.getMetadata().forEach( (name, val) -> {
            System.out.println(name + ": " + val);
         });
         updatePlayAndPauseButtons(false, scene);
         Slider progressSlider = 
               (Slider) scene.lookup("#" + SEEK_POS_SLIDER_ID);
         progressSlider.setValue(0);
         progressSlider.setMax(mediaPlayer.getMedia()
                                          .getDuration()
                                          .toSeconds());
         mediaPlayer.play();
      }); // setOnReady()
      
      // back to the beginning
      mediaPlayer.setOnEndOfMedia( ()-> {
         updatePlayAndPauseButtons(true, scene);
         // change buttons to play and rewind 
         mediaPlayer.stop();
      });
      
      int chartPadding = 5;
      double freqAxisY = scene.getHeight() - 45;

      chartArea.setStrokeLineJoin(StrokeLineJoin.ROUND);
      chartArea.setStrokeWidth(1.5);
      chartArea.setSmooth(true);
      chartArea.setStroke(Color.WHITE);
      chartArea.setFill(
              new LinearGradient(chartPadding, chartPadding, chartPadding, freqAxisY, false, null,
                      new Stop((0.0), deriveColorAlpha(Color.RED, .70)),
                      new Stop(.40, deriveColorAlpha(Color.ORANGE, .70)),
                      new Stop(.70, deriveColorAlpha(Color.YELLOW, .70)),
                      new Stop(.80, deriveColorAlpha(Color.GREEN, .70)),
                      new Stop(.90, deriveColorAlpha(Color.BLUE, .70)),
                      new Stop(.95, deriveColorAlpha(Color.INDIGO, .70)),
                      new Stop(1, deriveColorAlpha(Color.VIOLET, .70))));
      double chartHeight = freqAxisY - chartPadding;
      Pane root = (Pane) scene.getRoot();
      /*
      -fx-border-insets: 6 6 6 6;
      -fx-border-width: 1.5;
       */
      double padding = root.getBorder().getInsets().getLeft(); // 7.5 (union of inset and border width)
      double chartWidth = root.getWidth() - ((2 * padding) + (2 * chartPadding));
      double scaleY = chartHeight / (60 * 60);
      double space = 5; // between each data point

      mediaPlayer.setAudioSpectrumListener(
         (double timestamp,
          double duration,
          float[] magnitudes,
          float[] phases) -> {

            double freqBarX = padding + chartPadding;

            double scaleX = chartWidth / (magnitudes.length * space);
            // check if data array created.
            // if not create the number of path components to be
            // added to the chartArea path.
            if (chartArea.getElements().size() -3 != magnitudes.length) {

               chartArea.getElements().clear();
               chartArea.getElements().add(new MoveTo(freqBarX, freqAxisY));

               for(float magnitude:magnitudes) {
                  double dB = magnitude * magnitude;
                  dB = chartHeight - dB * scaleY;
                  chartArea.getElements().add(new LineTo(freqBarX, freqAxisY - dB));
                  freqBarX+=(scaleX * space);
               }
               chartArea.getElements().add(new LineTo(freqBarX, freqAxisY));
               chartArea.getElements().add(new ClosePath());
            } else {
               // if already created go through and update path components
               int idx = 0;
               for(float magnitude:magnitudes) {
                  double dB = magnitude * magnitude;
                  dB = chartHeight - dB * scaleY;

                  // skip first MoveTo element in path.
                  idx++;
                  LineTo dataPoint = (LineTo) chartArea.getElements().get(idx);
                  dataPoint.setX(freqBarX);
                  dataPoint.setY(freqAxisY - dB);
                  freqBarX += (scaleX * space);
               }
            }
     });

   }
  
   /**
   * Sets play button visible and pause button not visible when 
   * playVisible is true otherwise the opposite.
   *
   * @param playVisible - value of true the play becomes visible
   * and pause non visible, otherwise the opposite.
   * @param scene - The scene graph
   */
   private void updatePlayAndPauseButtons(boolean playVisible, Scene scene) {
      Node playButton = scene.lookup("#" + PLAY_BUTTON_ID);
      Node pauseButton = scene.lookup("#" + PAUSE_BUTTON_ID);

      if (playVisible) {
         // show play button
         playButton.toFront();
         playButton.setVisible(playVisible);
         pauseButton.toBack();

      } else {
         // show pause button
         pauseButton.toFront();
         pauseButton.setVisible(!playVisible);
         playButton.toBack();
      }

   }
   /**
    * A position slider to seek backward and forward 
    * that is bound to a media player control.
    *
    * @return Slider control bound to media player.
    */
   private Slider createSlider(Pane root) {
   Slider slider = new Slider(0, 100, 1);
   slider.setId(SEEK_POS_SLIDER_ID);
   slider.valueProperty()
         .addListener((observable) -> {
             if (slider.isValueChanging()) {
               // must check if media is paused before seeking 
               if (mediaPlayer != null && 
                   mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                  
                  // convert seconds to millis
                  double dur = slider.getValue() * 1000;
                  mediaPlayer.seek(Duration.millis(dur));
               }
             }
         });
      return slider;
   }

   private Color deriveColorAlpha(Color color, double alpha) {
      return Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
   }
}
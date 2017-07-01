package com.jfxbe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.MalformedURLException;

/**
 * Playing Audio using the JavaFX MediaPlayer API.
 *
 * @author carldea
 */
public class PlayingAudio extends Application {

   private MediaPlayer mediaPlayer;
   private Point2D anchorPt;
   private Point2D previousLocation;
   private ChangeListener<Duration> progressListener;
   private BooleanProperty playAndPauseToggle = new SimpleBooleanProperty(true);
   private EventHandler<MouseEvent> mouseEventConsumer = event -> event.consume();


   /**
    * @param args he command line arguments
    */
   public static void main(String[] args) {
      Application.launch(args);
   }

   @Override
   public void start(Stage primaryStage) {
      // Remove native window borders and title bar
      primaryStage.initStyle(StageStyle.TRANSPARENT);

      // Create the application surface or background
      Pane root = new AnchorPane();
      root.setId("app-surface");

      Scene scene = new Scene(root, 551, 270, Color.rgb(0, 0, 0, 0));

      // load JavaFX CSS style
      scene.getStylesheets()
           .add(getClass().getResource("/playing-audio.css")
                          .toExternalForm());
      primaryStage.setScene(scene);

      // Initialize stage to be movable via mouse
      initMovablePlayer(primaryStage);

      // Create a Path instance for the area chart
      Path chartArea = new Path();
      chartArea.setId("chart-area");

      // Create the button panel (stop, play and pause)
      Node buttonPanel = createButtonPanel(root);
      AnchorPane.setRightAnchor(buttonPanel, 3.0);
      AnchorPane.setBottomAnchor(buttonPanel, 3.0);

      // Create a slider for our progress and seek control
      Slider progressSlider = createSlider();
      AnchorPane.setLeftAnchor(progressSlider, 2.0);
      AnchorPane.setBottomAnchor(progressSlider, 2.0);

      // Updates slider as audio/video is progressing (play)
      progressListener = (observable, oldValue, newValue) -> 
         progressSlider.setValue(newValue.toSeconds());

      // Initializing Scene to accept files
      // using drag and dropping over the surface to load media
      initFileDragNDrop(root);

      // Create the close button
      Node closeButton = createCloseButton();
      AnchorPane.setRightAnchor(closeButton, 2.0);
      AnchorPane.setTopAnchor(closeButton, 2.0);

      root.getChildren()
          .addAll(chartArea,
                  buttonPanel,
                  progressSlider,
                  closeButton);

      primaryStage.centerOnScreen();
      primaryStage.show();
   }

   /**
    * Initialize the stage to allow the mouse cursor to move the application
    * using dragging.
    * @param primaryStage - The applications primary Stage window.
    */
   private void initMovablePlayer(Stage primaryStage) {

      Scene scene = primaryStage.getScene();
      Pane root = (Pane) scene.getRoot();
      root.setPickOnBounds(true);
      // starting initial anchor point
      root.setOnMousePressed(mouseEvent ->
              anchorPt = new Point2D(mouseEvent.getScreenX(),
                      mouseEvent.getScreenY())
      );

      // Dragging the stage by moving its x,y
      // based on the previous location.
      root.setOnMouseDragged(mouseEvent -> {
         if (anchorPt != null && previousLocation != null) {
            primaryStage.setX(previousLocation.getX()
                    + mouseEvent.getScreenX()
                    - anchorPt.getX());
            primaryStage.setY(previousLocation.getY()
                    + mouseEvent.getScreenY()
                    - anchorPt.getY());
         }
      });

      // Set the new previous to the current mouse x,y coordinate
      root.setOnMouseReleased(mouseEvent ->
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
    * @param root - The Scene graph's root pane.
    */
   private void initFileDragNDrop(Pane root) {
      // Drag over surface
      root.setOnDragOver(dragEvent -> {
         Dragboard db = dragEvent.getDragboard();
         if (db.hasFiles() || db.hasUrl()) {
            dragEvent.acceptTransferModes(TransferMode.LINK);
         } else {
            dragEvent.consume();
         }
      });

      // Dropping over surface
      root.setOnDragDropped(dragEvent -> {
         Dragboard db = dragEvent.getDragboard();
         boolean success = false;
         String filePath = null;
         if (db.hasFiles()) {
            success = true;
            if (db.getFiles().size() > 0) {
               try {
                  // obtain file and play media
                  filePath = db.getFiles()
                          .get(0)
                          .toURI().toURL().toString();
                  playMedia(filePath, root);
               } catch (MalformedURLException ex) {
                  ex.printStackTrace();
               }
            }
         } else {
            // audio file from some host or jar
            playMedia(db.getUrl(), root);
            success = true;
         }

         dragEvent.setDropCompleted(success);
         dragEvent.consume();
      });
   }

   /**
    * Creates a node containing the audio player's 
    *  stop, pause and play buttons.
    *
    * @param root The Scene graphs root pane.
    * @return Node A button panel having play, 
    *  pause and stop buttons.
    */
   private Node createButtonPanel(Pane root) {

      // create button control panel
      FlowPane buttonPanel = new FlowPane();
      buttonPanel.setId("button-panel");

      // stop button control
      Node stopButton = new Rectangle(10, 10);
      stopButton.setId("stop-button");
      stopButton.setOnMouseClicked(mouseEvent -> {
         if (mediaPlayer != null) {
            updatePlayAndPauseButtons(true, root);
            if (mediaPlayer.getStatus() == Status.PLAYING ||
                mediaPlayer.getStatus() == Status.PAUSED) {

               mediaPlayer.stop();
            }
            playAndPauseToggle.set(false);
         }
      });

      // Toggle Button containing a Play and Pause button
      StackPane playPauseToggleButton = new StackPane();

      // Play button control
      Arc playButton = new Arc(12, // center x 
              16, // center y                 
              15, // radius x
              15, // radius y
              150, // start angle
              60);  // length
      playButton.setId("play-button");
      playButton.setType(ArcType.ROUND);
      playButton.setMouseTransparent(true);

      // Pause control
      Group pauseButton = new Group();
      pauseButton.setId("pause-button");
      Circle pauseBackground = new Circle(12, 16, 10);
      pauseBackground.getStyleClass()
                     .add("pause-circle");

      Line firstLine = new Line(6,  // start x
                                6,  // start y  
                                6,  // end x 
                               14); // end y 
      firstLine.getStyleClass()
               .addAll("pause-line", "first-line");

      Line secondLine = new Line(6,   // start x
                                 6,   // start y  
                                 6,   // end x 
                                 14); // end y 
      secondLine.getStyleClass()
                .addAll("pause-line", "second-line");

      pauseButton.getChildren()
                 .addAll(pauseBackground, firstLine, secondLine);
      pauseButton.setMouseTransparent(true);

      playPauseToggleButton.getChildren()
                           .addAll(playButton, pauseButton);
      playPauseToggleButton.setOnMouseEntered(mouseEvent -> {
         Color red = Color.rgb(255, 0, 0, .90);
         pauseBackground.setStroke(red);
         firstLine.setStroke(red);
         secondLine.setStroke(red);
         playButton.setStroke(red);
      });
      playPauseToggleButton.setOnMouseExited(mouseEvent -> {
         Color white = Color.rgb(255, 255, 255, .90);
         pauseBackground.setStroke(white);
         firstLine.setStroke(white);
         secondLine.setStroke(white);
         playButton.setStroke(white);
      });
      // Boolean property toggling the playing and pausing
      // the media player
      playAndPauseToggle.addListener(
              (observable, oldValue, newValue) -> {

         if (newValue) {
            // Play
            if (mediaPlayer != null) {
               updatePlayAndPauseButtons(false, root);
               mediaPlayer.play();
            }
         } else {
            // Pause
            if (mediaPlayer!=null) {
               updatePlayAndPauseButtons(true, root);
               if (mediaPlayer.getStatus() == Status.PLAYING) {
                  mediaPlayer.pause();
               }
            }
         }
      });

      // Press toggle button
      playPauseToggleButton.setOnMouseClicked( mouseEvent ->{
         if (mouseEvent.getClickCount() == 1) {
            playAndPauseToggle.set(!playAndPauseToggle.get());
         }
      });

      buttonPanel.getChildren()
                 .addAll(stopButton,
                         playPauseToggleButton);
      buttonPanel.setPrefWidth(50);

      // Intercept mouse events. This prevents the
      // root node from receiving mouse events drag events while
      // user is pressing on the button panel.
      buttonPanel.addEventHandler(MouseEvent.ANY, mouseEventConsumer);

      return buttonPanel;
   }

   /**
    * The close button to exit application
    *
    * @return Node representing a close button.
    */
   private Node createCloseButton() {

      StackPane closeButton = new StackPane();
      closeButton.setId("close-button");

      Node closeBackground = new Circle(0, 0, 7);
      closeBackground.setId("close-circle");
      SVGPath closeXmark = new SVGPath();
      closeXmark.setId("close-x-mark");
      closeXmark.setContent("M 0 0 L 6 6 M 6 0 L 0 6");

      closeButton.getChildren()
                 .addAll(closeBackground,
                         closeXmark);
      // exit app
      closeButton.setOnMouseClicked(mouseEvent -> {
         if (mediaPlayer != null){
            mediaPlayer.stop();
         }
         Platform.exit();
      });
      // Filter mouse events from propagating to the parent.
      closeButton.addEventHandler(MouseEvent.ANY, mouseEventConsumer);
      return closeButton;
   }

   /**
    * After a file is dragged onto the application a new MediaPlayer 
    * instance is created with a media file.
    *
    * @param url - The URL pointing to an audio file.
    * @param root - The scene graph's root pane.
    */
   private void playMedia(String url, Pane root) {

      if (mediaPlayer != null) {
         mediaPlayer.pause();
         mediaPlayer.setOnPaused(null);
         mediaPlayer.setOnPlaying(null);
         mediaPlayer.setOnReady(null);
         mediaPlayer.currentTimeProperty()
                    .removeListener(progressListener);
         mediaPlayer.setAudioSpectrumListener(null);
      }

      // create a new media player
      Media validMedia = null;
      try {
         validMedia = new Media(url);
      } catch (Exception e) {
         new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait()
                 .filter(response -> response == ButtonType.OK)
                 .ifPresent(response -> e.printStackTrace());
         return;
      }
      final Media media = validMedia;

      mediaPlayer = new MediaPlayer(media);

      // as the media is playing move the slider for progress
      mediaPlayer.currentTimeProperty()
                 .addListener(progressListener);

      mediaPlayer.setOnReady(() -> {
         // display media's metadata 
         media.getMetadata().forEach( (name, val) -> {
            System.out.println(name + ": " + val);
         });
         updatePlayAndPauseButtons(false, root);
         Slider progressSlider = 
               (Slider) root.lookup("#seek-position-slider");
         progressSlider.setValue(0);
         progressSlider.setMax(mediaPlayer.getMedia()
                                          .getDuration()
                                          .toSeconds());
         mediaPlayer.play();
      });
      
      // Rewind back to the beginning
      mediaPlayer.setOnEndOfMedia( ()-> {
         updatePlayAndPauseButtons(true, root);
         // change buttons to the play button
         mediaPlayer.stop();
         playAndPauseToggle.set(false);
      });

      // Obtain chart path area
      Path chartArea = (Path) root.lookup("#chart-area");

      int chartPadding = 5;

      // The frequency domain is the X Axis.
      // The freqAxisY is the Y coordinate of the freq axis.
      double freqAxisY = root.getHeight() - 45;

      // The chart's height
      double chartHeight = freqAxisY - chartPadding;

      // In the CSS file the padding is set with the following:
      //   -fx-border-insets: 6 6 6 6; (top, right, bottom, left)
      //   -fx-border-width: 1.5;
      // Below the padding will equal 7.5 which is the union of
      // the inset 6 (left) and border width of 1.5)
      double padding = root.getBorder().getInsets().getLeft();
      double chartWidth = root.getWidth() - ((2 * padding) + (2 * chartPadding));

      // Audio sound is in decibels and the magnitude is from 0 to -60
      // Squaring the magnitudes stretches the plot. Also dividing into
      // the chart height will normalize or keep the y coordinate within the
      // chart bounds.
      double scaleY = chartHeight / (60 * 60);
      double space = 5; // between each data point. Helps stretch the chart width-wise.

      mediaPlayer.setAudioSpectrumListener(
         (double timestamp,
          double duration,
          float[] magnitudes,
          float[] phases) -> {
            if (mediaPlayer.getStatus() == Status.PAUSED || mediaPlayer.getStatus() == Status.STOPPED) {
               return;
            }
            // The freqBarX is the x coordinate to plot
            double freqBarX = padding + chartPadding;

            // The scaleX is one unit. This keeps the plotting within the chart width area.
            double scaleX = chartWidth / (magnitudes.length * space);

            // Checks if the data array is created.
            // If not create the number of path components to be
            // added to the chartArea path. The check of the size minus 3 is excluding
            // the first MoveTo element, and the last two elements LineTo, and ClosePath
            // respectively.
            if ((chartArea.getElements().size() - 3) != magnitudes.length) {

               // Move to bottom left of chart.
               chartArea.getElements().clear();
               chartArea.getElements().add(new MoveTo(freqBarX, freqAxisY));

               // Update all LineTo elements to draw the line chart
               for(float magnitude:magnitudes) {
                  double dB = magnitude * magnitude;
                  dB = chartHeight - dB * scaleY;
                  chartArea.getElements().add(new LineTo(freqBarX, freqAxisY - dB));
                  freqBarX+=(scaleX * space);
               }

               // Close the path by adding LineTo to the bottom right
               // of the chart and close path to form an shape.
               chartArea.getElements().add(new LineTo(freqBarX, freqAxisY));
               chartArea.getElements().add(new ClosePath());
            } else {
               // If elements already created
               // go through and update path elements
               int idx = 0;
               for(float magnitude:magnitudes) {
                  double dB = magnitude * magnitude;
                  dB = chartHeight - dB * scaleY;

                  // skip first MoveTo element in path.
                  idx++;

                  // update elements with a x and y
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
   * @param root - The root node (AnchorPane)
   */
   private void updatePlayAndPauseButtons(boolean playVisible, Parent root) {
      Node playButton = root.lookup("#play-button");
      Node pauseButton = root.lookup("#pause-button");

      if (playVisible) {
         // show play button
         playButton.toFront();
         playButton.setVisible(true);
         pauseButton.setVisible(false);
         pauseButton.toBack();

      } else {
         // show pause button
         pauseButton.toFront();
         pauseButton.setVisible(true);
         playButton.setVisible(false);
         playButton.toBack();
      }

   }
   /**
    * A position slider to seek backward and forward 
    * that is bound to a media player control.
    *
    * @return Slider control bound to media player.
    */
   private Slider createSlider() {
      Slider slider = new Slider(0, 100, 1);
      slider.setId("seek-position-slider");
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
}
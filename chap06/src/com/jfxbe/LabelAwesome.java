package com.jfxbe;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.*;

/**
 * LabelAwesome is an example using Jens Deters' FontAwesomeFX library.
 * @author carldea
 */
public class LabelAwesome extends Application {

    // lookup icon packs
    private static Map<String, List<GlyphIcons>> ICON_PACKS_MAP = new HashMap<>();

    @Override
    public void init() throws Exception {
        // load all icons
        Font.loadFont(GlyphsDude.class.getResource(FontAwesomeIconView.TTF_PATH).openStream(), 10.0);
        Font.loadFont(GlyphsDude.class.getResource(MaterialDesignIconView.TTF_PATH).openStream(), 10.0);
        Font.loadFont(GlyphsDude.class.getResource(MaterialIconView.TTF_PATH).openStream(), 10.0);
        Font.loadFont(GlyphsDude.class.getResource(OctIconView.TTF_PATH).openStream(), 10.0);
        Font.loadFont(GlyphsDude.class.getResource(WeatherIconView.TTF_PATH).openStream(), 10.0);

        // Prepare all icons
        ICON_PACKS_MAP.put("FontAwesomeIcon", Arrays.asList(FontAwesomeIcon.values()));
        ICON_PACKS_MAP.put("MaterialDesignIcon", Arrays.asList(MaterialDesignIcon.values()));
        ICON_PACKS_MAP.put("MaterialIcon", Arrays.asList(MaterialIcon.values()));
        ICON_PACKS_MAP.put("OctIcon", Arrays.asList(OctIcon.values()));
        ICON_PACKS_MAP.put("WeatherIcon", Arrays.asList(WeatherIcon.values()));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("LabelAwesome ");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 450);

        // Create Title
        Text labelText = new Text("Label ");
        labelText.setFont(Font.font("Helvetica", FontWeight.EXTRA_LIGHT, 60));
        Text awesomeText = new Text("Awesome");
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(3.0f);
        innerShadow.setOffsetY(3.0f);
        awesomeText.setEffect(innerShadow);
        awesomeText.setFill(Color.WHITE);
        awesomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 60));
        TextFlow title = new TextFlow(labelText, awesomeText);
        HBox banner = new HBox(title);
        banner.setPadding(new Insets(10, 0, 10, 10));
        banner.setPrefHeight(70);

        root.setTop(banner);


        // Display Icon area
        VBox labelDisplayPanel = new VBox(5);
        labelDisplayPanel.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane(labelDisplayPanel);
        root.setCenter(scrollPane);
        scrollPane.setPadding(new Insets(10, 10, 10, 10));

        // Select Icons packs (ComboBox)
        VBox controlsPanel = new VBox(10);
        controlsPanel.setPadding(new Insets(10, 10, 10, 10));
        List<String> iconPackList = new ArrayList<>();
        iconPackList.add("FontAwesomeIcon");
        iconPackList.add("MaterialDesignIcon");
        iconPackList.add("MaterialIcon");
        iconPackList.add("OctIcon");
        iconPackList.add("WeatherIcon");

        ObservableList<String> obsIconPackList = FXCollections.observableList(iconPackList);
        ComboBox<String> iconPacks = new ComboBox<>(obsIconPackList);
        iconPacks.setValue(iconPackList.get(0));
        controlsPanel.getChildren().add(iconPacks);

        // Input Field (TextField)
        TextField inputField = new TextField();
        inputField.setPrefWidth(200);
        inputField.setPromptText("Search Icon Name");
        controlsPanel.getChildren().add(inputField);

        // Selecting the Icon Position (RadioBox)
        VBox imagePositionPanel = new VBox(5);
        ToggleGroup position = new ToggleGroup();
        RadioButton topPosition = new RadioButton("Top");
        topPosition.setSelected(true);
        topPosition.setUserData(ContentDisplay.TOP);
        topPosition.requestFocus();
        topPosition.setToggleGroup(position);

        RadioButton bottomPosition = new RadioButton("Bottom");
        bottomPosition.setUserData(ContentDisplay.BOTTOM);
        bottomPosition.setToggleGroup(position);

        RadioButton leftPosition = new RadioButton("Left");
        leftPosition.setUserData(ContentDisplay.LEFT);
        leftPosition.setToggleGroup(position);

        RadioButton rightPosition = new RadioButton("Right");
        rightPosition.setUserData(ContentDisplay.RIGHT);
        rightPosition.setToggleGroup(position);

        imagePositionPanel.getChildren()
                          .addAll(topPosition,
                            bottomPosition,
                            leftPosition,
                            rightPosition);
        controlsPanel.getChildren()
                     .add(imagePositionPanel);

        root.setLeft(controlsPanel);

        // As the user types the text is searched.
        inputField.textProperty().addListener((o, oldVal, newVal) ->
            showIconList(newVal,labelDisplayPanel,
                    iconPacks.getValue(),
                    position.getSelectedToggle()
                            .getUserData())
        );

        // When the radio button select Position to place the Icon
        position.selectedToggleProperty().addListener((o, oldVal, newVal) ->
            showIconList(inputField.getText(),
                    labelDisplayPanel,
                    iconPacks.getValue(),
                    position.getSelectedToggle()
                            .getUserData()));

        // When Combo box chooses an Icon pack.
        iconPacks.setOnAction(actionEvent ->
            showIconList(inputField.getText(),
                    labelDisplayPanel,
                    iconPacks.getValue(),
                    position.getSelectedToggle()
                            .getUserData()));

        stage.setScene(scene);
        stage.show();

        // Initial display of the current list of Icons
        showIconList(inputField.getText(),
                labelDisplayPanel,
                iconPacks.getValue(),
                position.getSelectedToggle()
                        .getUserData());

    }

    private void showIconList(String textInput,
                              VBox labelDisplayPanel,
                              String iconPack,
                              Object position) {

        // Clear the right display
        labelDisplayPanel.getChildren().clear();

        // Obtain the icon pack's list of names.
        List<GlyphIcons> iconPackIcons = ICON_PACKS_MAP.get(iconPack);

        iconPackIcons.stream()
                .filter(iconEnum -> iconEnum.toString().toUpperCase()
                        .indexOf(textInput.toUpperCase()) > -1)
                .forEach(iconEnum -> {
                    // create a text node using the vector font.
                    Text iconShape = new Text(iconEnum.characterToString());
                    iconShape.getStyleClass().add("glyph-icon");
                    iconShape.setStyle(
                            String.format("-fx-font-family: %s; -fx-font-size: %s;",
                            iconEnum.getFontFamily(), 20));
                    Label label = new Label(iconEnum.toString(), iconShape);
                    label.setContentDisplay((ContentDisplay)position);
                    labelDisplayPanel.getChildren().add(label);
                });
    }
}

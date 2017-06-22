
package com.jfxbe;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static com.jfxbe.Person.MOOD_TYPES;
import static com.jfxbe.Person.MOOD_TYPES.*;

/**
 * A JavaFX example application to demonstrate
 * observable lists used in ListViews, and TableViews.
 * Also this demos the use of domain objects with
 * properties as attributes.
 *
 * Bosses and Employees
 * @author cdea
 */
public class BossesAndEmployees extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bosses and Employees: Working with Tables");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 630, 250, Color.WHITE);

        // create a grid pane
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(20));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        root.setCenter(gridpane);

        // candidates label
        Label candidatesLbl = new Label("Boss");
        GridPane.setHalignment(candidatesLbl, HPos.CENTER);
        gridpane.add(candidatesLbl, 0, 0);

        // List of bosses
        ObservableList<Person> bosses = getPeople();
        final ListView<Person> leaderListView = new ListView<>(bosses);
        leaderListView.setPrefWidth(150);
        leaderListView.setMinWidth(200);
        leaderListView.setMaxWidth(200);
        leaderListView.setPrefHeight(Integer.MAX_VALUE);

        // display first and last name with tooltip using alias
        leaderListView.setCellFactory(listView -> {
            Tooltip tooltip = new Tooltip();
            ListCell<Person> cell = new ListCell<Person>() {
                @Override
                public void updateItem(Person item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getFirstName() + " " + item.getLastName());
                        tooltip.setText(item.getAliasName());
                        setTooltip(tooltip);
                    }
                }
            }; // ListCell
            return cell;
        }); // setCellFactory

        gridpane.add(leaderListView, 0, 1);

        Label emplLbl = new Label("Employees");
        gridpane.add(emplLbl, 2, 0);
        GridPane.setHalignment(emplLbl, HPos.CENTER);

        TableView<Person> employeeTableView = new TableView<>();
        employeeTableView.setEditable(true);
        employeeTableView.setPrefWidth(Integer.MAX_VALUE);

        ObservableList<Person> teamMembers = FXCollections.observableArrayList();
        employeeTableView.setItems(teamMembers);

        TableColumn<Person, String> aliasNameCol = new TableColumn<>("Alias");
        aliasNameCol.setCellValueFactory(new PropertyValueFactory<>("aliasName"));
        aliasNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());


        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Person, MOOD_TYPES> moodCol = new TableColumn<>("Mood");
        moodCol.setCellValueFactory(new PropertyValueFactory<>("mood"));
        ObservableList<MOOD_TYPES> moods = FXCollections.observableArrayList(MOOD_TYPES.values());
        moodCol.setCellFactory(ComboBoxTableCell.forTableColumn(moods));
        moodCol.setPrefWidth(100);
        employeeTableView.getColumns().add(aliasNameCol);
        employeeTableView.getColumns().add(firstNameCol);
        employeeTableView.getColumns().add(lastNameCol);
        employeeTableView.getColumns().add(moodCol);
        gridpane.add(employeeTableView, 2, 1);

        // selection listening
        leaderListView.getSelectionModel()
                      .selectedItemProperty()
                      .addListener((observable, oldValue, newValue) -> {
            if (observable != null && observable.getValue() != null) {
                teamMembers.clear();
                teamMembers.addAll(observable.getValue().employeesProperty());
            }
        });



        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<Person> getPeople() {
        ObservableList<Person> people = FXCollections.<Person>observableArrayList();

        Person docX = new Person("Professor X", "Charles", "Xavier", Positive);
        docX.employeesProperty().add(new Person("Wolverine", "James", "Howlett", Angry));
        docX.employeesProperty().add(new Person("Cyclops", "Scott", "Summers", Happy));
        docX.employeesProperty().add(new Person("Storm", "Ororo", "Munroe", Positive));

        Person magneto = new Person("Magneto", "Max", "Eisenhardt", Sad);
        magneto.employeesProperty().add(new Person("Juggernaut", "Cain", "Marko", Angry));
        magneto.employeesProperty().add(new Person("Mystique", "Raven", "Darkhölme", Sad));
        magneto.employeesProperty().add(new Person("Sabretooth", "Victor", "Creed", Angry));

        Person biker = new Person("Mountain Biker", "Jonathan", "Gennick", Positive);
        biker.employeesProperty().add(new Person("MkHeck", "Mark", "Heckler", Happy));
        biker.employeesProperty().add(new Person("Hansolo", "Gerrit", "Grunwald", Positive));
        biker.employeesProperty().add(new Person("Doc", "José", "Pereda", Happy));
        biker.employeesProperty().add(new Person("Cosmonaut", "Sean", "Phillips", Positive));
        biker.employeesProperty().add(new Person("CarlFX", "Carl", "Dea", Happy));

        people.add(docX);
        people.add(magneto);
        people.add(biker);

        return people;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

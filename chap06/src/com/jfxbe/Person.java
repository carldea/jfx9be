package com.jfxbe;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cdea
 */
public class Person {
    public enum MOOD_TYPES {
        Happy,
        Sad,
        Angry,
        Positive
    }
    private StringProperty aliasName;
    private StringProperty firstName;
    private StringProperty lastName;
    private ObjectProperty<MOOD_TYPES> mood;

    private ObservableList<Person> employees = FXCollections.observableArrayList();

    public Person(String alias, String firstName, String lastName, MOOD_TYPES mood) {
        setAliasName(alias);
        setFirstName(firstName);
        setLastName(lastName);
        setMood(mood);
    }

    public final void setAliasName(String value) {
        aliasNameProperty().set(value);
    }

    public final String getAliasName() {
        return aliasNameProperty().get();
    }

    public StringProperty aliasNameProperty() {
        if (aliasName == null) {
            aliasName = new SimpleStringProperty();
        }
        return aliasName;
    }

    public final void setFirstName(String value) {
        firstNameProperty().set(value);
    }

    public final String getFirstName() {
        return firstNameProperty().get();
    }

    public StringProperty firstNameProperty() {
        if (firstName == null) {
            firstName = new SimpleStringProperty();
        }
        return firstName;
    }

    public final void setLastName(String value) {
        lastNameProperty().set(value);
    }

    public final String getLastName() {
        return lastNameProperty().get();
    }

    public StringProperty lastNameProperty() {
        if (lastName == null) {
            lastName = new SimpleStringProperty();
        }
        return lastName;
    }

    public final void setMood(MOOD_TYPES value) {
        moodProperty().set(value);
    }

    public final MOOD_TYPES getMood() {
        return moodProperty().get();
    }

    public ObjectProperty<MOOD_TYPES> moodProperty() {
        if (mood == null) {
            mood = new SimpleObjectProperty<>(MOOD_TYPES.Happy);
        }
        return mood;
    }

    public ObservableList<Person> employeesProperty() {
        return employees;
    }
}
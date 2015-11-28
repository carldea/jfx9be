package com.jfxbe;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final static String USERNAME_PROP_NAME = "userName";
    private final ReadOnlyStringWrapper userName;
    
    private final static String PASSWORD_PROP_NAME = "password";
    private StringProperty password;
    public User() {
        userName = new ReadOnlyStringWrapper(this, USERNAME_PROP_NAME, System.getProperty("user.name"));
        password = new SimpleStringProperty(this, PASSWORD_PROP_NAME, "");
    }
    
    public final String getUserName() {
        return userName.get();
    }
    public ReadOnlyStringProperty userNameProperty() {
        return userName.getReadOnlyProperty();
    }
    
    public final String getPassword() {
        return password.get();
    }
    public final void setPassword(String password) {
        this.password.set(password);
    }
    public StringProperty passwordProperty() {
        return password;
    }
}

package com.noticeditorteam.noticeditor.view;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;

public class PasswordDialog extends Dialog<String> {

    private final PasswordField passwordField;

    public PasswordDialog() {
        this("");
    }

    public PasswordDialog(String defaultValue) {
        setHeaderText("Enter password");
        passwordField = new PasswordField();
        passwordField.setText(defaultValue);
        getDialogPane().setContent(passwordField);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Platform.runLater(() -> passwordField.requestFocus());

        setResultConverter(param -> param == ButtonType.OK ? passwordField.getText() : null);
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
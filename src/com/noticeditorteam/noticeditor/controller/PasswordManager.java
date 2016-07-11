package com.noticeditorteam.noticeditor.controller;

import java.util.Optional;

public final class PasswordManager {

    private static Optional<String> lastPath = Optional.empty();
    private static Optional<String> lastPassword = Optional.empty();

    public static void resetPassword() {
        lastPassword = Optional.empty();
        lastPath = Optional.empty();
    }

    public static Optional<String> askPassword(String forFilePath) {
        if (lastPath.isPresent() && lastPath.get().equals(forFilePath)) {
            return lastPassword;
        }
        lastPassword = NoticeController.getController()
                .newPasswordDialog(lastPassword.orElse(""))
                .showAndWait();
        if (lastPassword.isPresent()) {
            lastPath = Optional.ofNullable(forFilePath);
        }
        return lastPassword;
    }
}

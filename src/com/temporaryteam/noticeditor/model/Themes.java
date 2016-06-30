package com.temporaryteam.noticeditor.model;

public enum Themes {
    DEFAULT("Default"),
    DARK("Dark", "dark.css");

    public static final String PATH = "/resources/styles/themes/";

    private final String name;
    private final String cssPath;

    Themes(String name) {
        this.name = name;
        this.cssPath = null;
    }

    Themes(String name, String cssPath) {
        this.name = name;
        this.cssPath = PATH + cssPath;
    }

    public String getName() {
        return name;
    }

    public String getCssPath() {
        return cssPath;
    }
}

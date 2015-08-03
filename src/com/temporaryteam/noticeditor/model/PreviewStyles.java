package com.temporaryteam.noticeditor.model;

public enum PreviewStyles {
	DEFAULT("Default"),
	GITHUB("GitHub", "github.css"),
	MARKDOWN("Markdown", "markdown.css");
	
	public static final String PATH = "/resources/styles/markdown/";

	private final String name;
	private final String cssPath;
	
	PreviewStyles(String name) {
		this.name = name;
		this.cssPath = null;
	}

	PreviewStyles(String name, String cssPath) {
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

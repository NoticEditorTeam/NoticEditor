package com.noticeditorteam.noticeditor.model;

public enum PreviewStyles {
	DEFAULT("Default"),
	GITHUB("GitHub", "github.css"),
	MARKDOWN("Markdown", "markdown.css"),
	DARK("Dark", "dark.css", "darkula.css"),
	SOLARIZED_DARK("Solarized", "solarizeddark.css", "solarized_dark.css");
	
	public static final String PATH = "/resources/styles/markdown/";

	private final String name;
	private final String cssPath;
	private final String codeCssName;

	PreviewStyles(String name) {
        this.name = name;
        this.cssPath = null;
		this.codeCssName = null;
	}

	PreviewStyles(String name, String cssPath) {
		this(name, cssPath, null);
	}

	PreviewStyles(String name, String cssPath, String codeCssName) {
		this.name = name;
		this.cssPath = PATH + cssPath;
		this.codeCssName = codeCssName;
	}

	public String getName() {
		return name;
	}

	public String getCssPath() {
		return cssPath;
	}

	public String getCodeCssName() {
		return codeCssName;
	}
}

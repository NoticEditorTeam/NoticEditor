package com.temporaryteam.noticeditor.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model class for a Notice.
 */
public class Notice {

	private final StringProperty notice;

	/**
	 * Default constructor.
	 */
	public Notice() {
		this(null);
	}

	/*
	 * Constructor with notice.
	 */
	public Notice(String notice) {
		this.notice = new SimpleStringProperty(notice);
	}

	public String getNotice() {
		return notice.get();
	}

	public void setNotice(String newNotice) {
		notice.set(newNotice);
	}

	public StringProperty getNoticeProperty() {
		return notice;
	}

}

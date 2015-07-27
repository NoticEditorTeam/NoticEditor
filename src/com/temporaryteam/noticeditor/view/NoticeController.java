package com.temporaryteam.noticeditor.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.model.Notice;

public class NoticeController {

	@FXML
	private TextArea noticeArea;

	private Main main;
	
	/**
	 * The constructor. Must be called before initialization method
	 */
	public NoticeController() {
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Sets reference to Main class
	 */
	public void setMain(Main main) {
		this.main = main;
		try {
			noticeArea.setText(main.getCurrentNotice().getNotice());
		} catch(IndexOutOfBoundsException e) {
		}
	}
	
}

package com.temporaryteam.noticeditor.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.MenuItem;
import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.model.Notice;

public class NoticeController {

	@FXML
	private TextArea noticeArea;

	@FXML
	private MenuItem exitItem;

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
		noticeArea.setText("Enter your notice here");
	}
	
	/**
	 * Handler
	 */
	@FXML
	private void handleMenu(ActionEvent event) {
		MenuItem source = (MenuItem)event.getSource();
		if(source.equals(exitItem)) Platform.exit();
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

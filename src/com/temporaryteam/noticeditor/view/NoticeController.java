package com.temporaryteam.noticeditor.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.TextArea;
import javafx.scene.control.MenuItem;
import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.model.Notice;

public class NoticeController {

	@FXML
	private TextArea noticeArea;

	@FXML
	private MenuItem newItem;

	@FXML
	private MenuItem openItem;

	@FXML
	private MenuItem saveItem;

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
		if(source.equals(newItem)) {
			noticeArea.setText(main.toString());
		}
		else if((source.equals(openItem))||(source.equals(saveItem))) {
			try {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Select notice to open");
				chooser.getExtensionFilters().addAll(
					new ExtensionFilter("Text files", "*.txt"),
					new ExtensionFilter("PDF files", "*.pdf"),
					new ExtensionFilter("HTML files", "*.html"),
					new ExtensionFilter("All files", "*.*"));
				File selected = chooser.showOpenDialog(main.getPrimaryStage());
				if(selected!=null) {
					if(source.equals(openItem)) {
						String notice = "";
						Scanner in = new Scanner(selected);
						while(in.hasNext()) {
							notice+=in.nextLine()+"\n";
						}
						noticeArea.setText(notice);
						in.close();
					}
					else if(source.equals(saveItem)) {
						String notice = noticeArea.getText();
						if(!selected.exists()) selected.createNewFile();
						FileWriter writeFile = new FileWriter(selected);
						writeFile.write(notice);
						writeFile.close();
					}
				}
			} catch (IOException ioe) {
			}
		}
		else if(source.equals(exitItem)) Platform.exit();
	}

	/**
	 * Sets reference to Main class
	 */
	public void setMain(Main main) {
		this.main = main;
/*		try {
			noticeArea.setText(main.getCurrentNotice().getNotice());
		} catch(IndexOutOfBoundsException e) {
		}*/
	}
	
}

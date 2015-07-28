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
	private MenuItem saveAsItem;

	@FXML
	private MenuItem exitItem;

	private Main main;
	private File openedFile;
	private FileChooser chooser;
	
	/**
	 * The constructor. Must be called before initialization method
	 */
	public NoticeController() {
		openedFile = null;
		chooser = new FileChooser();
		chooser.setTitle("Select notice to open");
		chooser.getExtensionFilters().addAll(
			new ExtensionFilter("Text files", "*.txt"),
			new ExtensionFilter("PDF files", "*.pdf"),
			new ExtensionFilter("HTML files", "*.html"),
			new ExtensionFilter("All files", "*"));
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
			openedFile = null;
		}
		else if(source.equals(saveItem)) {
			try {
				File toSave = null;
				File selected = null;
				if(openedFile==null) {
					selected = chooser.showSaveDialog(main.getPrimaryStage());
					if(selected!=null) toSave = selected;
				}
				else toSave = openedFile;
				if(toSave!=null) {
					String notice = noticeArea.getText();
					if(!toSave.exists()) toSave.createNewFile();
					FileWriter writeFile = new FileWriter(toSave);
					writeFile.write(notice);
					writeFile.close();
				}
			} catch(IOException ioe) {
			}
		}
		else if((source.equals(openItem))||(source.equals(saveAsItem))) {
			try {
				File selected = null;
				if(source.equals(openItem)) {
					selected = chooser.showOpenDialog(main.getPrimaryStage());
				}
				else if(source.equals(saveAsItem)) {
					selected = chooser.showSaveDialog(main.getPrimaryStage());
				}
				if(selected!=null) {
					if(source.equals(openItem)) {
						String notice = "";
						Scanner in = new Scanner(selected);
						while(in.hasNext()) {
							notice+=in.nextLine()+"\n";
						}
						noticeArea.setText(notice);
						openedFile = selected;
						in.close();
					}
					else if(source.equals(saveAsItem)) {
						String notice = noticeArea.getText();
						if(!selected.exists()) selected.createNewFile();
						FileWriter writeFile = new FileWriter(selected);
						writeFile.write(notice);
						writeFile.close();
						openedFile = selected;
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

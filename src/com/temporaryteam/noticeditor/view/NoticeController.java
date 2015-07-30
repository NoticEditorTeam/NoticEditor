package com.temporaryteam.noticeditor.view;

import org.json.JSONObject;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.model.Notice;
import com.temporaryteam.noticeditor.model.NoticeCategory;

public class NoticeController {

	@FXML
	private TextArea noticeArea;

	@FXML
	private WebView viewer;

	@FXML
	private MenuItem newItem;

	@FXML
	private MenuItem openItem;

	@FXML
	private MenuItem saveItem;

	@FXML
	private MenuItem saveAsItem;

	@FXML
	private MenuItem exportHTMLItem;

	@FXML
	private MenuItem exitItem;

	@FXML
	private MenuItem aboutItem;

	@FXML
	private TreeView<String> noticeTree;

	private Main main;
	private File openedFile;
	private FileChooser chooser;
	private WebEngine engine;
	private String input;
	private PegDownProcessor processor;
	private NoticeCategory currentNotice;
	
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
		processor = new PegDownProcessor();
	}

	/**
	 * Method for operate with markdown
	 */
	private String operate(String source) {
		return processor.markdownToHtml(source);
	}
	
	/**
	 * Generate node
	 */
	private NoticeTreeItem<String> createNode(NoticeCategory notice) {
		return new NoticeTreeItem<String>(notice);
	}
	
	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		noticeArea.setText("Enter your notice here");
		engine = viewer.getEngine();
		currentNotice = new NoticeCategory("", new Notice("Enter your notice here"));
		noticeTree.setRoot(createNode(currentNotice));
		engine.loadContent(noticeArea.getText());
		noticeArea.textProperty().addListener((observable, oldValue, newValue) -> engine.loadContent(operate(newValue)));
	}
	
	/**
	 * Handler
	 */
	@FXML
	private void handleMenu(ActionEvent event) {
		MenuItem source = (MenuItem)event.getSource();
		if(source.equals(newItem)) {
			noticeArea.setText("");
			currentNotice = new NoticeCategory("", new Notice(""));
			noticeTree.setRoot(createNode(currentNotice));
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
					if(!toSave.exists()) toSave.createNewFile();
					FileWriter writeFile = new FileWriter(toSave);
					JSONObject obj = currentNotice.toJson();
					obj.write(writeFile);
					writeFile.close();
				}
			} catch(IOException ioe) {
			} catch(JSONException e) {
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
						JSONObject obj = new JSONObject(notice);
						currentNotice.fromJson(obj);
						noticeArea.setText("");
						openedFile = selected;
						in.close();
						noticeTree.setRoot(createNode(currentNotice));
					}
					else if(source.equals(saveAsItem)) {
						if(!selected.exists()) selected.createNewFile();
						FileWriter writeFile = new FileWriter(selected);
						JSONObject obj = currentNotice.toJson();
						obj.write(writeFile);
						writeFile.close();
						openedFile = selected;
					}
				}
			} catch (IOException ioe) {
			} catch (JSONException e) {
			}
		}
		else if(source.equals(exportHTMLItem)) {
			try {
				String notice = noticeArea.getText();
				notice = processor.markdownToHtml(notice);
				File selected = chooser.showSaveDialog(main.getPrimaryStage());
				if(!selected.exists()) selected.createNewFile();
				FileWriter writeFile = new FileWriter(selected);
				writeFile.write(notice);
				writeFile.close();
			} catch(IOException ioe) {
			}
		}
		else if(source.equals(exitItem)) Platform.exit();
	}

	/**
	 * Sets reference to Main class
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
}

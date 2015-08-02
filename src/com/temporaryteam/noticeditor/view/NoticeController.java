package com.temporaryteam.noticeditor.view;

import org.json.JSONObject;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import javafx.util.Callback;
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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
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
	private NoticeTreeItem currentTreeItem;

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

	public NoticeTreeItem getCurrentTreeItem() {
		return currentTreeItem;
	}

	public void setCurrentTreeItem(NoticeTreeItem newCurrentTreeItem) {
		currentTreeItem = newCurrentTreeItem;
	}
	
	/**
	 * Write node
	 */
	public void writeNode(NoticeCategory node, String name) throws IOException {
		File write;
		if(openedFile!=null) write = new File(openedFile.getParent() + "/" + name);
		else write = chooser.showSaveDialog(main.getPrimaryStage());
		if(!write.exists()) write.createNewFile();
		FileWriter writeHTML = new FileWriter(write);
		writeHTML.write(node.toHTML(processor));
		writeHTML.close();
		if(node.getSubCategories()!=null) {
			for(NoticeCategory subcategory : node.getSubCategories()) {
				writeNode(subcategory, (subcategory.getName() + ".html"));
			}
		}
	}

	/**
	 * Rebuild tree
	 */
	public void rebuild(String str) {
		ArrayList<NoticeCategory> list = new ArrayList<NoticeCategory>();
		list.add(new NoticeCategory("Default notice", str));
		currentNotice = new NoticeCategory("Default branch", list);
		noticeTree.setRoot(createNode(currentNotice));
	}

	/**
	 * Open notice in TextArea
	 */
	public void open(String notice) {
		noticeArea.setText(notice);
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
		rebuild("Enter your notice here");
		NoticeController controller = this;
		noticeTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				EditNoticeTreeCell cell = new EditNoticeTreeCell();
				cell.setController(controller);
				return cell;
			}
		});
		engine.loadContent(noticeArea.getText());
		noticeArea.textProperty().addListener((observable, oldValue, newValue) -> {
			engine.loadContent(operate(newValue));
			if(currentTreeItem!=null) currentTreeItem.getNotice().setContent(newValue);
		});
	}
	
	/**
	 * Handler
	 */
	@FXML
	private void handleMenu(ActionEvent event) {
		MenuItem source = (MenuItem)event.getSource();
		if(source.equals(newItem)) {
			noticeArea.setText("");
			rebuild("");
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
				if(openedFile!=null) chooser.setInitialDirectory(new File(openedFile.getParent()));
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
				e.printStackTrace();
			}
		}
		else if(source.equals(exportHTMLItem)) {
			try {
				writeNode(((NoticeTreeItem)noticeTree.getRoot()).getNotice(), "index.html");
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

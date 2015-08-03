package com.temporaryteam.noticeditor.view;

import org.json.JSONObject;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.net.URI;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.util.Callback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.model.NoticeCategory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class NoticeController {

	@FXML
	private SplitPane mainPanel;

	@FXML
	private SplitPane editorPanel;

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
	private MenuItem zipItem;

	@FXML
	private MenuItem exportHTMLItem;

	@FXML
	private MenuItem exitItem;

	@FXML
	private MenuItem aboutItem;

	@FXML
	private MenuItem rotateItem;

	@FXML
	private CheckMenuItem wordWrapItem;

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
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
	}

	public NoticeTreeItem getCurrentTreeItem() {
		return currentTreeItem;
	}

	public void setCurrentTreeItem(NoticeTreeItem newCurrentTreeItem) {
		currentTreeItem = newCurrentTreeItem;
	}
	
	/** 
	 * Pack directory
	 */
	private void pack(File directory, String toSave)  throws IOException {
		URI root = directory.toURI();
		Deque<File> queue = new LinkedList<>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(new File(toSave));
		Closeable res = out;
		try {
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while(!queue.isEmpty()) {
				directory = queue.pop();
				for(File child : directory.listFiles()) {
					String name = root.relativize(child.toURI()).getPath();
					if(child.isDirectory()) {
						queue.push(child);
						name = name.endsWith("/") ? name : (name + "/");
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						InputStream in = new FileInputStream(child);
						try {
							byte[] buffer = new byte[1024];
							while(true) {
								int readCount = in.read(buffer);
								if(readCount<0) break;
								zout.write(buffer, 0, readCount);
							}
						} finally {
							in.close();
						}
						zout.closeEntry();
					}
				}
			}
		} finally {
			res.close();
		}
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
	 * Write node in filesystem
	 */
	private void writeFSNode(NoticeCategory node, String name, File dir) throws IOException {
		System.out.println("In " + node.getName() + " with name " + name);
		if(node.getSubCategories()!=null) {
			for(NoticeCategory cat : node.getSubCategories()) {
				File newDir = new File(dir.getPath() + "/" + name);
				if(newDir.exists()) newDir.delete();
				newDir.mkdir();
				writeFSNode(cat, cat.getName(), newDir);
			}
		}
		else {
			File toWrite = new File(dir.getPath() + "/" + name + ".md");
			if(!toWrite.exists()) toWrite.createNewFile();
			FileWriter writer = new FileWriter(toWrite);
			writer.write(node.getContent());
			writer.close();
		}
		System.out.println("Exit");
	}

	/**
	 * Rebuild tree
	 */
	public void rebuild(String str) {
		ArrayList<NoticeCategory> list = new ArrayList<>();
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
		return new NoticeTreeItem<>(notice);
	}
	
	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		noticeArea.setText("help");
		engine = viewer.getEngine();
		rebuild("help");
		final NoticeController controller = this;
		noticeTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				EditNoticeTreeCell cell = new EditNoticeTreeCell();
				cell.setController(controller);
				return cell;
			}
		});
		engine.loadContent(noticeArea.getText());
		noticeArea.textProperty().addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        engine.loadContent(operate(newValue));
			if(currentTreeItem!=null) currentTreeItem.getNotice().setContent(newValue);
                    }
                });
		noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());
	}
	
	/**
	 * Handler
	 */
	@FXML
	private void handleMenu(ActionEvent event) {
		MenuItem source = (MenuItem)event.getSource();
		if(source.equals(newItem)) {
			noticeArea.setText("help");
			rebuild("help");
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
		else if(source.equals(zipItem)) {
			try {
				File toWrite;
				if(openedFile!=null) toWrite = new File(openedFile.getParent() + "/." + ((NoticeTreeItem)noticeTree.getRoot()).getNotice().getName());
				else toWrite = chooser.showSaveDialog(main.getPrimaryStage());
				if(toWrite.exists()) toWrite.delete();
				toWrite.mkdir();
				writeFSNode(((NoticeTreeItem)noticeTree.getRoot()).getNotice(), ((NoticeTreeItem)noticeTree.getRoot()).getNotice().getName(), toWrite);
				pack(toWrite, (toWrite.getParent() + "/" + ((NoticeTreeItem)noticeTree.getRoot()).getNotice().getName() + ".zip"));
				toWrite.delete();
			} catch(IOException ioe) {
			}
		}
		else if(source.equals(rotateItem)) {
			Orientation or = editorPanel.getOrientation();
			if (or == Orientation.HORIZONTAL) or = Orientation.VERTICAL;
			else or = Orientation.HORIZONTAL;
			editorPanel.setOrientation(or);
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

package com.temporaryteam.noticeditor.view;

import org.json.JSONObject;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javafx.util.Callback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.io.IOUtil;
import com.temporaryteam.noticeditor.model.NoticeCategory;
import com.temporaryteam.noticeditor.model.PreviewStyles;

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
	private MenuItem addBranchItem;

	@FXML
	private MenuItem addNoticeItem;

	@FXML
	private MenuItem deleteItem;

	@FXML
	private CheckMenuItem wordWrapItem;
	
	@FXML
    	private Menu previewStyleMenu;

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
	private EditNoticeTreeCell cell;

	/**
	 * The constructor. Must be called before initialization method
	 */
	public NoticeController() {
		openedFile = null;
		chooser = new FileChooser();
		chooser.setTitle("Select notice to open");
		chooser.getExtensionFilters().addAll(
			new ExtensionFilter("Text files", "*.txt"),
			new ExtensionFilter("All files", "*"));
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
	}

	public MenuItem getAddBranchItem() {
		return addBranchItem;
	}

	public MenuItem getAddNoticeItem() {
		return addNoticeItem;
	}

	public MenuItem getDeleteItem() {
		return deleteItem;
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
		File file;
		if (openedFile != null) file = new File(openedFile.getParent() + "/" + name);
		else file = chooser.showSaveDialog(main.getPrimaryStage());
		IOUtil.writeContent(file, node.toHTML(processor));
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
			IOUtil.writeContent(toWrite, node.getContent());
		}
		System.out.println("Exit");
	}

	/**
	 * Rebuild tree
	 */
	public void rebuild(String str) {
		ArrayList<NoticeCategory> list = new ArrayList<>();
		list.add(new NoticeCategory("Default notice", str));
		currentNotice = new NoticeCategory("Root", list);
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
		noticeTree.setShowRoot(false);
		engine = viewer.getEngine();
		
		// Set preview styles menu items
		ToggleGroup previewStyleGroup = new ToggleGroup();
		for (PreviewStyles style : PreviewStyles.values()) {
			final String cssPath = style.getCssPath();
			RadioMenuItem item = new RadioMenuItem(style.getName());
			item.setToggleGroup(previewStyleGroup);
			if (cssPath == null) item.setSelected(true);
			item.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e)
				{
				String path = cssPath;
				if (path != null) path = getClass().getResource(path).toExternalForm();
				engine.setUserStyleSheetLocation(path);
				}
			});
			previewStyleMenu.getItems().add(item);
		}
		
		rebuild("help");
		final NoticeController controller = this;
		noticeTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				cell = new EditNoticeTreeCell();
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
				if (openedFile == null) {
					File selected = chooser.showSaveDialog(main.getPrimaryStage());
					if  (selected != null) toSave = selected;
				}
				else toSave = openedFile;
				if (toSave != null) {
					IOUtil.writeJson(toSave, currentNotice.toJson());
				}
			} catch(IOException | JSONException ioe) {
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
						JSONObject obj = new JSONObject(IOUtil.readContent(selected));
						currentNotice.fromJson(obj);
						noticeArea.setText("");
						openedFile = selected;
						noticeTree.setRoot(createNode(currentNotice));
					}
					else if(source.equals(saveAsItem)) {
						IOUtil.writeJson(selected, currentNotice.toJson());
						openedFile = selected;
					}
				}
			} catch (IOException | JSONException e) {
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
				File destFile; // zipFile to save zip
				destFile = chooser.showSaveDialog(main.getPrimaryStage());
				String rootName = ((NoticeTreeItem)noticeTree.getRoot()).getNotice().getName();
				File temporary = Files.createTempDirectory("noticeditor").toFile();
				writeFSNode(((NoticeTreeItem)noticeTree.getRoot()).getNotice(), rootName, temporary);
				IOUtil.pack(temporary, destFile.getPath());
				IOUtil.removeDirectory(temporary);
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
		else cell.handleContextMenu(event);
	}
	/**
	 * Sets reference to Main class
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
}

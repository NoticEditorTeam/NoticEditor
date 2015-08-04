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
	private MenuItem addBranchItem, addNoticeItem, deleteItem;

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
	private void writeFSNode(NoticeCategory node, File dir) throws IOException {
		String name = node.getName();
		System.out.println("In " + node.getName() + " with name " + name);
		if (node.getSubCategories() != null) {
			for(NoticeCategory cat : node.getSubCategories()) {
				File newDir = new File(dir.getPath() + "/" + name);
				if(newDir.exists()) newDir.delete();
				newDir.mkdir();
				writeFSNode(cat, newDir);
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
	private void handleContextMenu(ActionEvent event) {
		cell.handleContextMenu(event);
	}
	
	@FXML
	private void handleNew(ActionEvent event) {
		noticeArea.setText("help");
		rebuild("help");
		openedFile = null;
	}
	
	@FXML
	private void handleOpen(ActionEvent event) {
		try {
			if (openedFile != null) chooser.setInitialDirectory(new File(openedFile.getParent()));
			File selected = chooser.showOpenDialog(main.getPrimaryStage());
			if (selected == null) return;
			
			JSONObject obj = new JSONObject(IOUtil.readContent(selected));
			currentNotice.fromJson(obj);
			noticeArea.setText("");
			openedFile = selected;
			noticeTree.setRoot(createNode(currentNotice));
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleSave(ActionEvent event) {
		try {
			File toSave = null;
			if (openedFile == null) {
				File selected = chooser.showSaveDialog(main.getPrimaryStage());
				if (selected != null) toSave = selected;
			}
			else toSave = openedFile;
			if (toSave != null) {
				IOUtil.writeJson(toSave, currentNotice.toJson());
			}
		} catch(IOException | JSONException ioe) {
		}
	}
	
	@FXML
	private void handleSaveAs(ActionEvent event) {
		try {
			if(openedFile != null) chooser.setInitialDirectory(new File(openedFile.getParent()));
			File selected = chooser.showSaveDialog(main.getPrimaryStage());
			if (selected == null) return;
			
			IOUtil.writeJson(selected, currentNotice.toJson());
			openedFile = selected;
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleSaveToZip(ActionEvent event) {
		try {
			File destFile = chooser.showSaveDialog(main.getPrimaryStage());
			if (destFile == null) return;
			
			File temporaryDir = Files.createTempDirectory("noticeditor").toFile();
			writeFSNode(((NoticeTreeItem)noticeTree.getRoot()).getNotice(), temporaryDir);
			IOUtil.pack(temporaryDir, destFile.getPath());
			IOUtil.removeDirectory(temporaryDir);
		} catch(IOException ioe) {
		}
	}
	
	@FXML
	private void handleExportHtml(ActionEvent event) {
		try {
			writeNode(((NoticeTreeItem)noticeTree.getRoot()).getNotice(), "index.html");
		} catch(IOException ioe) {
		}
	}
	
	@FXML
	private void handleExit(ActionEvent event) {
		Platform.exit();
	}
	
	@FXML
	private void handleSwitchOrientation(ActionEvent event) {
		Orientation or = editorPanel.getOrientation();
		if (or == Orientation.HORIZONTAL) or = Orientation.VERTICAL;
		else or = Orientation.HORIZONTAL;
		editorPanel.setOrientation(or);
	}
	
	@FXML
	private void handleAbout(ActionEvent event) {
		
	}
	
	/**
	 * Sets reference to Main class
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
}

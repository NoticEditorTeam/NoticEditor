package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
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
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.io.IOUtil;
import com.temporaryteam.noticeditor.model.NoticeItem;
import com.temporaryteam.noticeditor.model.PreviewStyles;
import com.temporaryteam.noticeditor.view.Chooser;
import com.temporaryteam.noticeditor.view.EditNoticeTreeCell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import jfx.messagebox.MessageBox;

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
	private WebEngine engine;
	private final PegDownProcessor processor;
	private NoticeItem currentNotice;
	private NoticeTreeItem currentTreeItem;
	private EditNoticeTreeCell cell;
	private File fileSaved;

	public NoticeController() {
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
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
			if (cssPath == null) {
				item.setSelected(true);
			}
			item.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					String path = cssPath;
					if (path != null) {
						path = getClass().getResource(path).toExternalForm();
					}
					engine.setUserStyleSheetLocation(path);
				}
			});
			previewStyleMenu.getItems().add(item);
		}

		rebuild("help");
		final NoticeController controller = this;
		noticeTree.setShowRoot(false);
		noticeTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		noticeTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
				if (newValue == null) {
					return;
				}
				currentTreeItem = (NoticeTreeItem) newValue;
				noticeArea.setEditable(currentTreeItem.isLeaf());
				if (currentTreeItem.isLeaf()) {
					open(currentTreeItem.getNotice());
				}
			}
		});
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
				if (currentTreeItem != null) {
					currentTreeItem.getNotice().setContent(newValue);
				}
			}
		});
		noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());
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
	 * Save item as HTML pages. Root item was saved to index.html
	 *
	 * @param item node to recursively save
	 * @param file file to save
	 */
	public void exportToHtmlPages(NoticeItem item, File file) throws IOException {
		IOUtil.writeContent(file, item.toHTML(processor));
		if (item.isBranch()) {
			for (NoticeItem child : item.childrens()) {
				exportToHtmlPages(child, new File(file.getParent(), child.getId() + ".html"));
			}
		}
	}

	/**
	 * Write node in filesystem
	 */
	private void writeFSNode(NoticeItem node, File dir) throws IOException {
		String name = node.getName();
		System.out.println("In " + node.getName() + " with name " + name);
		if (node.isBranch()) {
			for (NoticeItem child : node.childrens()) {
				File newDir = new File(dir.getPath() + "/" + name);
				if (newDir.exists()) {
					newDir.delete();
				}
				newDir.mkdir();
				writeFSNode(child, newDir);
			}
		} else {
			File toWrite = new File(dir.getPath() + "/" + name + ".md");
			IOUtil.writeContent(toWrite, node.getContent());
		}
		System.out.println("Exit");
	}

	/**
	 * Rebuild tree
	 */
	public void rebuild(String str) {
		ArrayList<NoticeItem> list = new ArrayList<>();
		list.add(new NoticeItem("Default notice", str));
		currentNotice = new NoticeItem("Root", list);
		noticeTree.setRoot(createNode(currentNotice));
	}

	/**
	 * Open notice in TextArea
	 */
	public void open(NoticeItem notice) {
		if (notice == null) {
			return;
		}
		noticeArea.setText(notice.getContent());
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
	private NoticeTreeItem<String> createNode(NoticeItem notice) {
		return new NoticeTreeItem<>(notice);
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
		fileSaved = null;
	}

	@FXML
	private void handleOpen(ActionEvent event) {
		try {
			fileSaved = Chooser.file().open()
					.filter(Chooser.SUPPORTED, Chooser.ALL)
					.title("Open notice")
					.show(main.getPrimaryStage());
			if (fileSaved == null) return;

			JSONObject json = new JSONObject(IOUtil.readContent(fileSaved));
			currentNotice = new NoticeItem(json);
			noticeArea.setText("");
			noticeTree.setRoot(createNode(currentNotice));
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSave(ActionEvent event) {
		if (fileSaved == null) {
			fileSaved = Chooser.file().save()
					.filter(Chooser.JSON, Chooser.SUPPORTED, Chooser.ALL)
					.title("Save notice")
					.show(main.getPrimaryStage());
			if (fileSaved == null) return;
		}
		try {
			IOUtil.writeJson(fileSaved, currentNotice.toJson());
		} catch (IOException | JSONException ioe) {
		}
	}

	@FXML
	private void handleSaveAs(ActionEvent event) {
		fileSaved = Chooser.file().save()
					.filter(Chooser.JSON, Chooser.SUPPORTED, Chooser.ALL)
					.title("Save notice")
					.show(main.getPrimaryStage());
		if (fileSaved == null) return;

		try {
			IOUtil.writeJson(fileSaved, currentNotice.toJson());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSaveToZip(ActionEvent event) {
		File destFile = Chooser.file().save()
					.filter(Chooser.ZIP)
					.title("Save notice as zip archive")
					.show(main.getPrimaryStage());
		if (destFile == null) return;
		try {
			File temporaryDir = Files.createTempDirectory("noticeditor").toFile();
			writeFSNode(((NoticeTreeItem) noticeTree.getRoot()).getNotice(), temporaryDir);
			IOUtil.pack(temporaryDir, destFile.getPath());
			IOUtil.removeDirectory(temporaryDir);
		} catch (IOException ioe) {
		}
	}

	@FXML
	private void handleExportHtml(ActionEvent event) {
		File destDir = Chooser.directory()
					.title("Select directory to save HTML files")
					.show(main.getPrimaryStage());
		if (destDir == null) return;

		File indexFile = new File(destDir, "index.html");
		try {
			exportToHtmlPages(((NoticeTreeItem) noticeTree.getRoot()).getNotice(), indexFile);
			MessageBox.show(main.getPrimaryStage(), "Export success!", "", MessageBox.OK);
		} catch (IOException ioe) {
		}
	}

	@FXML
	private void handleExit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void handleSwitchOrientation(ActionEvent event) {
		editorPanel.setOrientation(editorPanel.getOrientation() == Orientation.HORIZONTAL
				? Orientation.VERTICAL : Orientation.HORIZONTAL);
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

package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import org.json.JSONObject;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
import com.temporaryteam.noticeditor.model.PreviewStyles;
import com.temporaryteam.noticeditor.view.Chooser;
import com.temporaryteam.noticeditor.view.EditNoticeTreeCell;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import jfx.messagebox.MessageBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NoticeController {

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
	private NoticeTreeItem currentTreeItem;
	private EditNoticeTreeCell cell;
	private File fileSaved;
	private Logger logger = Logger.getLogger(NoticeController.class.getName());

	public NoticeController(Main main) {
		this.main = main;
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
					open(currentTreeItem);
				}
			}
		});
		noticeTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new EditNoticeTreeCell();
			}
		});

		engine.loadContent(noticeArea.getText());
		noticeArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				engine.loadContent(operate(newValue));
				currentTreeItem.changeContent(newValue);
			}
		});
		noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());
	}

	/**
	 * Save item as HTML pages. Root item was saved to index.html
	 *
	 * @param item node to recursively save
	 * @param file file to save
	 */
	public void exportToHtmlPages(NoticeTreeItem<String> item, File file) throws IOException {
		Document doc = Jsoup.parse(getClass().getResourceAsStream("/resources/export_template.html"), null, "");
		item.toHTML(processor, doc);
		IOUtil.writeContent(file, doc.outerHtml());
		if (item.isBranch()) {
			for (Object obj : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) obj;
				exportToHtmlPages(child, new File(file.getParent(), child.getId() + ".html"));
			}
		}
	}

	/**
	 * Write node in filesystem
	 */
	private void writeFSNode(NoticeTreeItem item, File dir) throws IOException {
		String title = item.getTitle();
		System.out.println("In " + item.getTitle() + " with title " + title);
		if (item.isBranch()) {
			for (Object child : item.getChildren()) {
				File newDir = new File(dir.getPath() + "/" + title);
				if (newDir.exists()) {
					newDir.delete();
				}
				newDir.mkdir();
				writeFSNode((NoticeTreeItem) child, newDir);
			}
		} else {
			File toWrite = new File(dir.getPath() + "/" + title + ".md");
			IOUtil.writeContent(toWrite, item.getContent());
		}
		System.out.println("Exit");
	}

	/**
	 * Rebuild tree
	 */
	public void rebuild(String defaultNoticeContent) {
		NoticeTreeItem rootItem = new NoticeTreeItem("Root");
		currentTreeItem = new NoticeTreeItem("Default notice", defaultNoticeContent);
		rootItem.getChildren().add(currentTreeItem);
		noticeTree.setRoot(rootItem);
	}

	/**
	 * Open notice in TextArea
	 */
	public void open(NoticeTreeItem notice) {
		noticeArea.setText(notice.getContent());
	}

	/**
	 * Method for operate with markdown
	 */
	private String operate(String source) {
		return processor.markdownToHtml(source);
	}

	/**
	 * Handler
	 */
	@FXML
	private void handleContextMenu(ActionEvent event) {
		Object source = event.getSource();
		ObservableList<NoticeTreeItem> childTreeItems;
		if (currentTreeItem != null) {
			if (currentTreeItem.isLeaf() || source == deleteItem) {
				childTreeItems = currentTreeItem.getParent().getChildren();
			} else {
				childTreeItems = currentTreeItem.getChildren();
			}
		} else {
			childTreeItems = ((NoticeTreeItem) (noticeTree.getRoot())).getChildren();
		}
		if (source == addBranchItem) {
			childTreeItems.add(new NoticeTreeItem("New branch"));
		} else if (source == addNoticeItem) {
			childTreeItems.add(new NoticeTreeItem("New notice", ""));
		} else if (source == deleteItem) {
			childTreeItems.remove(currentTreeItem);
		}
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
			currentTreeItem = new NoticeTreeItem(json);
			noticeArea.setText("");
			noticeTree.setRoot(currentTreeItem);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSave(ActionEvent event) {
		if (fileSaved == null) {
			fileSaved = Chooser.file().save()
					.filter(Chooser.SUPPORTED, Chooser.JSON, Chooser.ALL)
					.title("Save notice")
					.show(main.getPrimaryStage());
			if (fileSaved == null) return;
		}
		try {
			IOUtil.writeJson(fileSaved, ((NoticeTreeItem) noticeTree.getRoot()).toJson());
		} catch (IOException | JSONException ioe) {
		}
	}

	@FXML
	private void handleSaveAs(ActionEvent event) {
		fileSaved = Chooser.file().save()
					.filter(Chooser.SUPPORTED, Chooser.JSON, Chooser.ALL)
					.title("Save notice")
					.show(main.getPrimaryStage());
		if (fileSaved == null) return;

		try {
			IOUtil.writeJson(fileSaved, ((NoticeTreeItem) noticeTree.getRoot()).toJson());
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
			writeFSNode((NoticeTreeItem) noticeTree.getRoot(), temporaryDir);
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
			exportToHtmlPages((NoticeTreeItem) noticeTree.getRoot(), indexFile);
			MessageBox.show(main.getPrimaryStage(), "Export success!", "", MessageBox.OK);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
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
	
	public NoticeTreeItem<String> getCurrentNotice() {
		return currentTreeItem;
	}

}

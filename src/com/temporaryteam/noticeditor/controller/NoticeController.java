package com.temporaryteam.noticeditor.controller;

import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.io.File;
import java.io.IOException;

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
import com.temporaryteam.noticeditor.io.DocumentFormat;
import com.temporaryteam.noticeditor.io.ExportException;
import com.temporaryteam.noticeditor.io.ExportStrategy;
import com.temporaryteam.noticeditor.io.ExportStrategyHolder;
import com.temporaryteam.noticeditor.model.*;
import com.temporaryteam.noticeditor.view.Chooser;
import com.temporaryteam.noticeditor.view.EditNoticeTreeCell;
import com.temporaryteam.noticeditor.view.Notification;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NoticeController {

	private static final Logger logger = Logger.getLogger(NoticeController.class.getName());

	@FXML
	private SplitPane noticeView;

	@FXML
	private NoticeViewController noticeViewController;

	@FXML
	private MenuItem addBranchItem, addNoticeItem, deleteItem;

	@FXML
	private CheckMenuItem wordWrapItem;

	@FXML
	private Menu previewStyleMenu;
	
	@FXML
	private TextField searchField;

	@FXML
	private TreeView<NoticeItem> noticeTreeView;

	@FXML
	private NoticeSettingsController noticeSettingsController;
	
	@FXML
	private VBox notificationBox;
	
	@FXML
	private Label notificationLabel;
	
	@FXML
	private ResourceBundle resources;

	private static NoticeController instance;
	private Main main;
	private NoticeTree noticeTree;
	private NoticeTreeItem currentTreeItem;
	private File fileSaved;

	public NoticeController() {
		instance = this;
	}

	public void setApplication(Main main) {
		this.main = main;
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		Notification.init(notificationBox, notificationLabel);

		// Set preview styles menu items
		ToggleGroup previewStyleGroup = new ToggleGroup();
		for (PreviewStyles style : PreviewStyles.values()) {
			final String cssPath = style.getCssPath();
			RadioMenuItem item = new RadioMenuItem(style.getName());
			item.setUserData(cssPath);
			item.setToggleGroup(previewStyleGroup);
			if (cssPath == null) {
				item.setSelected(true);
			}
			item.setOnAction(noticeViewController.onPreviewStyleChange);
			previewStyleMenu.getItems().add(item);
		}

		noticeTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		noticeTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<NoticeItem>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<NoticeItem>> observable, TreeItem<NoticeItem> oldValue, TreeItem<NoticeItem> newValue) {
				currentTreeItem = (NoticeTreeItem) newValue;
				open();
			}
		});
		noticeTreeView.setCellFactory(new Callback<TreeView<NoticeItem>, TreeCell<NoticeItem>>() {
			@Override
			public TreeCell<NoticeItem> call(TreeView<NoticeItem> p) {
				return new EditNoticeTreeCell();
			}
		});
		
		noticeSettingsController.setNoticeController(this);

		noticeViewController.getEditor().wrapTextProperty().bind(wordWrapItem.selectedProperty());
		rebuildTree(resources.getString("help"));
	}

	/**
	 * Rebuild tree
	 */
	public void rebuildTree(String defaultNoticeContent) {
		final NoticeTreeItem root = new NoticeTreeItem("Root");
		noticeTree = new NoticeTree(root);
		currentTreeItem = new NoticeTreeItem("Default notice", defaultNoticeContent, NoticeItem.STATUS_NORMAL);
		noticeTree.addItem(currentTreeItem, root);
		noticeTreeView.setRoot(root);
		createSearchBinding(root);
		open();
	}

	private void createSearchBinding(final NoticeTreeItem root) {
		searchField.clear();
		root.predicateProperty().bind(
				Bindings.createObjectBinding(this::searchTreeItemPredicate, searchField.textProperty()));
	}
	
	private NoticeTreeItem.Predicate<NoticeItem> searchTreeItemPredicate() {
		if ( (searchField.getText() == null) || (searchField.getText().isEmpty()) ) {
			return null;
		}
		return this::noticeSearch;
	}
	
	/**
	 * Search by title and content
	 * @return 
	 */
	private boolean noticeSearch(TreeItem<NoticeItem> parent, NoticeItem note) {
		final String searchString = searchField.getText().toLowerCase();

		final String title = note.getTitle().toLowerCase();
		if (title.contains(searchString)) return true;

		final String content = note.getContent();
		if (content == null || content.isEmpty()) return false;

		return content.toLowerCase().contains(searchString);
	}

	/**
	 * Open current item in UI. If current item == null or isBranch, interface will be cleared from last data.
	 */
	public void open() {
		if (currentTreeItem == null || currentTreeItem.isBranch()) {
			noticeViewController.getEditor().setDisable(true);
			noticeViewController.getEditor().setText("");
		} else {
			noticeViewController.getEditor().setDisable(false);
			noticeViewController.getEditor().setText(currentTreeItem.getContent());
		}
		noticeSettingsController.open(currentTreeItem);
	}

	/**
	 * Handler
	 */
	@FXML
	private void handleContextMenu(ActionEvent event) {
		Object source = event.getSource();
		if (source == addBranchItem) {
			noticeTree.addItem(new NoticeTreeItem("New branch"), currentTreeItem);
		} else if (source == addNoticeItem) {
			noticeTree.addItem(new NoticeTreeItem("New notice", "", NoticeItem.STATUS_NORMAL), currentTreeItem);
		} else if (source == deleteItem) {
			noticeTree.removeItem(currentTreeItem);
			if (currentTreeItem != null && currentTreeItem.getParent() == null) {
				currentTreeItem = null;
				noticeSettingsController.open(null);
			}
		}
	}

	@FXML
	private void handleNew(ActionEvent event) {
		rebuildTree(resources.getString("help"));
		fileSaved = null;
		NoticeStatusList.restore();
	}

	@FXML
	private void handleOpen(ActionEvent event) {
		try {
			fileSaved = Chooser.file().open()
					.filter(Chooser.SUPPORTED, Chooser.ALL)
					.title("Open notice")
					.show(main.getPrimaryStage());
			if (fileSaved == null) {
				return;
			}

			noticeTree = DocumentFormat.open(fileSaved);
			noticeTreeView.setRoot(noticeTree.getRoot());
			createSearchBinding(noticeTree.getRoot());
			currentTreeItem = null;
			open();
			noticeSettingsController.updateStatuses();
		} catch (IOException | JSONException e) {
			logger.log(Level.SEVERE, null, e);
			Notification.error("Unable to open " + fileSaved.getName());
		}
	}

	@FXML
	private void handleSave(ActionEvent event) {
		if (fileSaved == null) {
			handleSaveAs(event);
		} else {
			saveDocument(fileSaved);
		}
	}

	@FXML
	private void handleSaveAs(ActionEvent event) {
		fileSaved = Chooser.file().save()
				.filter(Chooser.ZIP, Chooser.JSON)
				.title("Save notice")
				.show(main.getPrimaryStage());
		if (fileSaved == null) {
			return;
		}

		saveDocument(fileSaved);
	}

	private void saveDocument(File file) {
		ExportStrategy strategy;
		if (Chooser.JSON.equals(Chooser.getLastSelectedExtensionFilter())
				|| file.getName().toLowerCase().endsWith(".json")) {
			strategy = ExportStrategyHolder.JSON;
		} else {
			strategy = ExportStrategyHolder.ZIP;
		}
		try {
			DocumentFormat.save(file, noticeTree, strategy);
			Notification.success("Successfully saved!");
		} catch (ExportException e) {
			logger.log(Level.SEVERE, null, e);
			Notification.error("Successfully failed!");
		}
		
	}

	@FXML
	private void handleExportHtml(ActionEvent event) {
		File destDir = Chooser.directory()
				.title("Select directory to save HTML files")
				.show(main.getPrimaryStage());
		if (destDir == null) {
			return;
		}

		try {
			ExportStrategyHolder.HTML.setProcessor(noticeViewController.processor);
			ExportStrategyHolder.HTML.export(destDir, noticeTree);
			Notification.success("Export success!");
		} catch (ExportException e) {
			logger.log(Level.SEVERE, null, e);
			Notification.error("Export failed!");
		}
	}

	@FXML
	private void handleExit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void handleSwitchOrientation(ActionEvent event) {
		noticeView.setOrientation(noticeView.getOrientation() == Orientation.HORIZONTAL
				? Orientation.VERTICAL : Orientation.HORIZONTAL);
	}

	@FXML
	private void handleAbout(ActionEvent event) {
		Notification.show("NoticEditor\n==========\n\nhttps://github.com/TemporaryTeam/NoticEditor");
	}
	
	@FXML
	private void handleImportUrl(ActionEvent event) {
		try {
			final ResourceBundle resource = ResourceBundle.getBundle("resources.i18n.WebImport", Locale.getDefault());
			
			Stage stage = new Stage();
			stage.setTitle(resource.getString("import"));
			stage.initOwner(main.getPrimaryStage());
			stage.initModality(Modality.WINDOW_MODAL);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WebImport.fxml"), resource);
			Scene scene = new Scene(loader.load());
			stage.setScene(scene);
			WebImportController controller = (WebImportController) loader.getController();
			controller.setImportCallback((html, ex) -> {
				if (ex != null) {
					Notification.error(ex.toString());
				} else if (html != null) {
					noticeViewController.getEditor().setText(html);
				}
				stage.close();
			});
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static NoticeController getController() {
		return instance;
	}

	public NoticeTreeItem getCurrentNotice() {
		return currentTreeItem;
	}

}

package com.temporaryteam.noticeditor.controller;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.io.DocumentFormat;
import com.temporaryteam.noticeditor.io.ExportException;
import com.temporaryteam.noticeditor.io.ExportStrategy;
import com.temporaryteam.noticeditor.io.ExportStrategyHolder;
import com.temporaryteam.noticeditor.io.importers.FileImporter;
import com.temporaryteam.noticeditor.model.*;
import com.temporaryteam.noticeditor.view.Chooser;
import com.temporaryteam.noticeditor.view.Notification;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class NoticeController {

	private static final Logger logger = Logger.getLogger(NoticeController.class.getName());

	@FXML
	private VBox noticeTreeView;

	@FXML
	private NoticeTreeViewController noticeTreeViewController;


	@FXML
	private CheckMenuItem wordWrapItem;

	@FXML
	private Menu recentFilesMenu, previewStyleMenu;
	
	@FXML
	private SplitPane noticeView;

	@FXML
	private NoticeViewController noticeViewController;

	@FXML
	private VBox notificationBox;
	
	@FXML
	private Label notificationLabel;
	
	@FXML
	private ResourceBundle resources;

	private static NoticeController instance;
	private Main main;
	private File fileSaved;

	public NoticeController() {
		instance = this;
	}

	public void setApplication(Main main) {
		this.main = main;
	}
	
	public static NoticeController getController() {
		return instance;
	}

	public static NoticeViewController getNoticeViewController() {
		return instance.noticeViewController;
	}

	public static NoticeTreeViewController getNoticeTreeViewController() {
		return instance.noticeTreeViewController;
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		Notification.init(notificationBox, notificationLabel);
		// Restore initial directory
		File initialDirectory = new File(Prefs.getLastDirectory());
		if (initialDirectory.isDirectory() && initialDirectory.exists()) {
			Chooser.setInitialDirectory(initialDirectory);
		}
		rebuildRecentFilesMenu();
				
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

		noticeViewController.getEditor().wrapTextProperty().bind(wordWrapItem.selectedProperty());
		noticeTreeViewController.rebuildTree(resources.getString("help"));
	}
	
	private void rebuildRecentFilesMenu() {
		recentFilesMenu.getItems().clear();
		Prefs.getRecentFiles().stream()
				.distinct()
				.map(File::new)
				.filter(File::exists)
				.filter(File::isFile)
				.forEach(file -> {
					MenuItem item = new MenuItem(file.getAbsolutePath());
					item.setOnAction(e -> {
						fileSaved = file;
						openDocument(file);
					});
					recentFilesMenu.getItems().add(item);
				});
		recentFilesMenu.setDisable(recentFilesMenu.getItems().isEmpty());
	}

	@FXML
	private void handleNew(ActionEvent event) {
		noticeTreeViewController.rebuildTree(resources.getString("help"));
		fileSaved = null;
		NoticeStatusList.restore();
	}

	@FXML
	private void handleOpen(ActionEvent event) {
		fileSaved = Chooser.file().open()
				.filter(Chooser.SUPPORTED, Chooser.ALL)
				.title("Open notice")
				.show(main.getPrimaryStage());
		if (fileSaved != null) {
			openDocument(fileSaved);
			Prefs.addToRecentFiles(fileSaved.getAbsolutePath());
			rebuildRecentFilesMenu();
		}
	}
	
	private void openDocument(File file) {
		try {
			noticeTreeViewController.rebuildTree(DocumentFormat.open(file));
		} catch (IOException e) {
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
			DocumentFormat.save(file, noticeTreeViewController.getNoticeTree(), strategy);
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
			ExportStrategyHolder.HTML.export(destDir, noticeTreeViewController.getNoticeTree());
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
			logger.log(Level.SEVERE, null, e);
		}
	}

	@FXML
	private void handleImportFile(ActionEvent event) {
		File file = Chooser.file().open()
				.filter(Chooser.SUPPORTED, Chooser.ALL)
				.title("Import file")
				.show(main.getPrimaryStage());
		if (file == null) return;

		FileImporter.content().importFrom(file, null, (text, ex) -> {
			if (ex != null) {
				Notification.error(ex.toString());
			} else if (text != null) {
				noticeViewController.getEditor().setText(text);
			}
		});
	}

	public void onExit(WindowEvent we) {
		Prefs.setLastDirectory(Chooser.getLastDirectory().getAbsolutePath());
	}
}

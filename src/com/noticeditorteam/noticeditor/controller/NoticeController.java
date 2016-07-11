package com.noticeditorteam.noticeditor.controller;

import com.noticeditorteam.noticeditor.Main;
import com.noticeditorteam.noticeditor.io.DocumentFormat;
import com.noticeditorteam.noticeditor.io.ExportException;
import com.noticeditorteam.noticeditor.io.ExportStrategy;
import com.noticeditorteam.noticeditor.io.ExportStrategyHolder;
import com.noticeditorteam.noticeditor.io.importers.FileImporter;
import com.noticeditorteam.noticeditor.model.NoticeStatusList;
import com.noticeditorteam.noticeditor.model.Prefs;
import com.noticeditorteam.noticeditor.model.PreviewStyles;
import com.noticeditorteam.noticeditor.model.Themes;
import com.noticeditorteam.noticeditor.view.Chooser;
import com.noticeditorteam.noticeditor.view.Notification;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class NoticeController {

    private static final Logger logger = Logger.getLogger(NoticeController.class.getName());

    @FXML
    private VBox noticeTreeView;

    @FXML
    private NoticeTreeViewController noticeTreeViewController;

    @FXML
    private CheckMenuItem wordWrapItem;

    @FXML
    private Menu recentFilesMenu, previewStyleMenu, themesMenu, localesMenu;

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

    public static Logger getLogger() {
        return logger;
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
            item.setUserData(style.ordinal());
            item.setToggleGroup(previewStyleGroup);
            if (cssPath == null) {
                item.setSelected(true);
            }
            item.setOnAction(noticeViewController.onPreviewStyleChange);
            previewStyleMenu.getItems().add(item);
        }

        // Set themes menu items
        ToggleGroup themesGroup = new ToggleGroup();
        for (Themes theme : Themes.values()) {
            final String cssPath = theme.getCssPath();
            RadioMenuItem item = new RadioMenuItem(theme.getName());
            item.setUserData(theme.ordinal());
            item.setToggleGroup(themesGroup);
            if (cssPath == null) {
                item.setSelected(true);
            }
            item.setOnAction(noticeViewController.onThemeChange);
            themesMenu.getItems().add(item);
        }

        // Set locales menu items
        final Locale currentLocale = Locale.getDefault();
        ToggleGroup localeGroup = new ToggleGroup();
        for (String lang : new String[] {"en", "ru", "uk"}) {
            final Locale locale = new Locale(lang);
            RadioMenuItem item = new RadioMenuItem(beautify(locale.getDisplayLanguage(locale)));
            item.setToggleGroup(localeGroup);
            if (lang.equals(currentLocale.getLanguage())) {
                item.setSelected(true);
            }
            item.setOnAction(a -> {
                Prefs.setLocale(locale);
                Notification.show(resources.getString("messages.restart_is_needed"), Duration.seconds(5));
            });
            localesMenu.getItems().add(item);
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
                .title(resources.getString("opennotice"))
                .show(main.getPrimaryStage());
        if (fileSaved == null) return;

        final boolean isOpened = openDocument(fileSaved);
        if (isOpened) {
            Prefs.addToRecentFiles(fileSaved.getAbsolutePath());
            rebuildRecentFilesMenu();
        }
    }

    @FXML
    private void handleKey(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case O:
                    handleOpen(null);
                case S:
                    if (event.isShiftDown())
                        handleSaveAs(null);
                    else
                        handleSave(null);
                case N:
                    if (event.isShiftDown())
                        handleNew(null);
            }
        }
    }

    private boolean openDocument(File file) {
        try {
            noticeTreeViewController.rebuildTree(DocumentFormat.open(file));
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            Notification.error(resources.getString("errors.cantopen") + " " + fileSaved.getName());
        }
        return false;
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
                .title(resources.getString("savenotice"))
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
            if (DocumentFormat.save(file, noticeTreeViewController.getNoticeTree(), strategy)) {
                Notification.success(resources.getString("save.success"));
            }
        } catch (ExportException e) {
            logger.log(Level.SEVERE, null, e);
            Notification.error(resources.getString("save.error"));
        }

    }

    @FXML
    private void handleExportHtml(ActionEvent event) {
        File destDir = Chooser.directory()
                .title(resources.getString("exporthtml"))
                .show(main.getPrimaryStage());
        if (destDir == null) {
            return;
        }

        try {
            ExportStrategyHolder.HTML.setProcessor(noticeViewController.processor);
            ExportStrategyHolder.HTML.export(destDir, noticeTreeViewController.getNoticeTree());
            Notification.success(resources.getString("exporthtml.success"));
        } catch (ExportException e) {
            logger.log(Level.SEVERE, null, e);
            Notification.error(resources.getString("exporthtml.fail"));
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void handleImportFile(ActionEvent event) {
        File file = Chooser.file().open()
                .filter(Chooser.SUPPORTED, Chooser.ALL)
                .title(resources.getString("importfile"))
                .show(main.getPrimaryStage());
        if (file == null)
            return;

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

    private String beautify(String source) {
        StringBuilder builder = new StringBuilder();
        char[] word;
        word = source.toCharArray();
        word[0] = Character.toUpperCase(word[0]);
        builder.append(word);
        return builder.toString();
    }

    public PasswordDialog newPasswordDialog(String defaultValue) {
        final PasswordDialog dialog = new PasswordDialog(defaultValue);
        dialog.setTitle(resources.getString("dialogs.passworddialog.title"));
        dialog.setHeaderText(resources.getString("dialogs.passworddialog.headertext"));
        dialog.initOwner(main.getPrimaryStage());
        dialog.initModality(Modality.WINDOW_MODAL);
        return dialog;
    }

}

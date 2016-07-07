package com.noticeditorteam.noticeditor.controller;

import com.noticeditorteam.noticeditor.io.IOUtil;
import com.noticeditorteam.noticeditor.model.Attachments;
import com.noticeditorteam.noticeditor.plugin.attachments.AttachmentImporter;
import com.noticeditorteam.noticeditor.plugin.attachments.FromURLImporter;
import com.noticeditorteam.noticeditor.view.Notification;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

/**
 * @author aNNiMON
 */
public class AttachmentImportController implements Initializable {

    @FXML
    private ComboBox<AttachmentImporter> importersBox;

    @FXML
    private Button importButton;

    @FXML
    private TextArea importDataArea;

    @FXML
    private ProgressBar progressBar;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        final List<AttachmentImporter> importers = new ArrayList<AttachmentImporter>();
        for (String pluginClass : IOUtil.linesFromResource(AttachmentImporter.PLUGINS_LIST_RESOURCE)) {
            if (pluginClass == null || pluginClass.isEmpty()) continue;
            try {
                final Class<AttachmentImporter> clazz = (Class<AttachmentImporter>) Class.forName(pluginClass);
                final Constructor<AttachmentImporter> constructor = clazz.getConstructor(ResourceBundle.class);
                final AttachmentImporter plugin = constructor.newInstance(resources);
                plugin.setImportDataArea(importDataArea);
                plugin.setProgressBar(progressBar);
                importers.add(plugin);
            } catch (Exception ex) {
                NoticeController.getLogger().log(Level.SEVERE, "Unable to load plugin: {0}", pluginClass);
            }
        }
        if (importers.isEmpty()) {
            importers.add(new FromURLImporter(resources));
        }

        importersBox.setCellFactory(param -> new ListCell<AttachmentImporter>() {
            @Override
            protected void updateItem(AttachmentImporter item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setText(item.name());
                }
            }
        });
        importersBox.setConverter(new StringConverter<AttachmentImporter>() {
            @Override
            public String toString(AttachmentImporter object) {
                return object.name();
            }

            @Override
            public AttachmentImporter fromString(String string) {
                return null;
            }
        });
        importersBox.getItems().addAll(importers);
        importersBox.getSelectionModel().selectFirst();

        importButton.disableProperty().bind(Bindings.or(
                Bindings.isEmpty(importDataArea.textProperty()),
                Bindings.isNull(importersBox.valueProperty())));
    }

    @FXML
    private void handleImport(ActionEvent event) {
        final AttachmentImporter importer = importersBox.getValue();
        if (importer == null) return;

        if (importer.isRunning()) {
            importer.cancel();
            return;
        }

        progressBar.visibleProperty().bind(importer.runningProperty());
        progressBar.progressProperty().bind(importer.progressProperty());
        importer.runningProperty().addListener(e -> {
            final String key = importer.isRunning() ? "cancel" : "import";
            importButton.setText(resources.getString(key));
        });
        importer.setOnFailed(e -> Notification.error(resources.getString("notification.failed")));
        importer.setOnSucceeded(e -> {
            Notification.success(resources.getString("notification.completed"));

            final Attachments attachments = importer.getValue();
            if (attachments == null) return;

            NoticeController.getNoticeTreeViewController().getCurrentNotice().addAttachments(attachments);
            NoticeController.getNoticeViewController().rebuildAttachsView();
        });
        new Thread(importer).start();
    }
}

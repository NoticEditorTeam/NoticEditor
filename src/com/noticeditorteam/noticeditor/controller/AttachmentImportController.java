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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
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
    private BorderPane container;

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
                plugin.setContainer(container);
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
        importersBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.onDeactivated();
            }
            if (newValue != null) {
                newValue.onActivated();

                // Setup bindings
                final BooleanBinding noSelectedImporters = Bindings.isNull(importersBox.valueProperty());
                final Optional<BooleanBinding> binding = newValue.importButtonDisabled();
                BooleanBinding importButtonDisable = noSelectedImporters;
                if (binding.isPresent()) {
                    importButtonDisable = Bindings.or(importButtonDisable, binding.get());
                }
                importButton.disableProperty().bind(importButtonDisable);
            }
        });
        importersBox.getItems().addAll(importers);
        importersBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleImport(ActionEvent event) {
        final Service<Attachments> service = importersBox.getValue();
        if (service.isRunning()) {
            service.cancel();
            return;
        }
        service.reset();

        importersBox.disableProperty().bind(service.runningProperty());
        progressBar.visibleProperty().bind(service.runningProperty());
        progressBar.progressProperty().bind(service.progressProperty());
        service.runningProperty().addListener(e -> {
            final String key = service.isRunning() ? "cancel" : "import";
            importButton.setText(resources.getString(key));
        });
        service.setOnFailed(e -> Notification.error(resources.getString("notification.failed")));
        service.setOnSucceeded(e -> {
            Notification.success(resources.getString("notification.completed"));

            final Attachments attachments = service.getValue();
            if (attachments == null) return;

            NoticeController.getNoticeTreeViewController().getCurrentNotice().addAttachments(attachments);
            NoticeController.getNoticeViewController().rebuildAttachsView();
        });
        service.start();
    }
}

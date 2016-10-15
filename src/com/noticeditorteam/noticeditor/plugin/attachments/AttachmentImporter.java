package com.noticeditorteam.noticeditor.plugin.attachments;

import com.noticeditorteam.noticeditor.model.Attachments;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Service;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 * Attachment importer plugin.
 *
 * @author aNNiMON
 */
public abstract class AttachmentImporter extends Service<Attachments> {

    public static final String PLUGINS_LIST_RESOURCE = "/resources/plugins/attachment_importers.txt";

    protected final ResourceBundle resources;
    protected BorderPane container;
    protected ProgressBar progressBar;

    public AttachmentImporter(ResourceBundle resources) {
        this.resources = resources;
    }

    public final void setContainer(BorderPane container) {
        this.container = container;
    }

    public final void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public abstract String name();

    public void onActivated() {
    }

    public void onDeactivated() {
    }

    public Optional<BooleanBinding> importButtonDisabled() {
        return Optional.empty();
    }
}

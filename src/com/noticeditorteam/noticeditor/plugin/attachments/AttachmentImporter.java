package com.noticeditorteam.noticeditor.plugin.attachments;

import com.noticeditorteam.noticeditor.model.Attachments;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

/**
 * Attachment importer plugin.
 *
 * @author aNNiMON
 */
public abstract class AttachmentImporter extends Service<Attachments> {

    public static final String PLUGINS_LIST_RESOURCE = "/resources/plugins/attachment_importers.txt";

    protected final ResourceBundle resources;
    protected TextArea importDataArea;
    protected ProgressBar progressBar;

    public AttachmentImporter(ResourceBundle resources) {
        this.resources = resources;
    }

    public final void setImportDataArea(TextArea importDataArea) {
        this.importDataArea = importDataArea;
    }

    public final void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public final String getTextData() {
        return importDataArea.getText();
    }

    public abstract String name();
}

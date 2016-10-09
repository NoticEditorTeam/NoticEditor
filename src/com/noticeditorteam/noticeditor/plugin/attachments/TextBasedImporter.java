package com.noticeditorteam.noticeditor.plugin.attachments;

import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.TextArea;

public abstract class TextBasedImporter extends AttachmentImporter {

    public final TextArea importTextArea;

    public TextBasedImporter(ResourceBundle resources) {
        super(resources);
        importTextArea = new TextArea();
        importTextArea.setPromptText(resources.getString("prompt"));
    }

    public final String getTextData() {
        return importTextArea.getText();
    }

    @Override
    public void onActivated() {
        super.onActivated();
        container.setCenter(importTextArea);
    }

    @Override
    public void onDeactivated() {
        super.onDeactivated();
        container.setCenter(null);
    }

    @Override
    public Optional<BooleanBinding> importButtonDisabled() {
        return Optional.of(Bindings.isEmpty(importTextArea.textProperty()));
    }
}

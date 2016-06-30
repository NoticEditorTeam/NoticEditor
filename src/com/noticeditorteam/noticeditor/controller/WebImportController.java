package com.noticeditorteam.noticeditor.controller;

import com.noticeditorteam.noticeditor.io.importers.HtmlImportMode;
import com.noticeditorteam.noticeditor.io.importers.WebImporter;
import com.noticeditorteam.noticeditor.io.importers.ImportCallback;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * @author aNNiMON
 */
public class WebImportController implements Initializable {

    @FXML
    private VBox modesBox;

    @FXML
    private WebView pagePreview;

    @FXML
    private TextField urlField;

    private WebImporter importer;
    private HtmlImportMode importMode;
    private ImportCallback<String, Exception> importCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importer = new WebImporter();
        importMode = HtmlImportMode.ORIGINAL;

        ObservableList<Node> nodes = modesBox.getChildren();
        nodes.clear();
        final ToggleGroup modesGroup = new ToggleGroup();
        for (HtmlImportMode value : HtmlImportMode.values()) {
            RadioButton radio = new RadioButton(resources.getString(value.getName()));
            if (value == importMode) radio.setSelected(true);
            radio.setOnAction(e -> onModeChanged(value));
            radio.setToggleGroup(modesGroup);
            nodes.add(radio);
        }

        pagePreview.getEngine().loadContent(resources.getString("preview"), "text/html");
    }

    public void setImportCallback(ImportCallback<String, Exception> importCallback) {
        this.importCallback = importCallback;
    }

    private void onModeChanged(HtmlImportMode mode) {
        importMode = mode;
        handlePreview(null);
    }

    @FXML
    private void handlePreview(ActionEvent event) {
        if (!isUrlValid()) return;

        importer.importFrom(urlField.getText(), importMode, (html, ex) -> {
            if (html != null) {
                pagePreview.getEngine().loadContent(html, "text/html");
            }
        });
    }

    @FXML
    private void handleImport(ActionEvent event) {
        if (!isUrlValid()) return;

        importer.importFrom(urlField.getText(), importMode, importCallback);
    }

    private boolean isUrlValid() {
        final String url = urlField.getText();
        return !url.isEmpty();
    }
}

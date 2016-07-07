package com.noticeditorteam.noticeditor.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

/**
 * @author aNNiMON
 */
public class AttachmentImportController implements Initializable {

    @FXML
    private ComboBox<?> importersBox;

    @FXML
    private Button importButton;

    @FXML
    private TextArea importDataArea;

    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleImport(ActionEvent event) {
    }
}

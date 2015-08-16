package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.model.NoticeStatus;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class.
 * Responsible for attaching files and note importance.
 *
 * @author Naik
 */
public class NoticeSettingsController implements Initializable {
	@FXML
	private GridPane settingsPane;
	@FXML
	private ListView<String> listAttached;
	@FXML
	private Button btnRemoveFile;
	@FXML
	private Button btnSelectFile;
	@FXML
	private ChoiceBox<String> choiceBoxNoticeStatus;
	
	private NoticeController noticeController;

	public void setNoticeController(NoticeController noticeController) {
		this.noticeController = noticeController;
	}
	
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		NoticeStatus.add(rb.getString("normal"));
		NoticeStatus.add(rb.getString("important"));
		
		choiceBoxNoticeStatus.setItems(NoticeStatus.asObservable());
		choiceBoxNoticeStatus.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				NoticeTreeItem currentNotice = noticeController.getCurrentNotice();
				if (null != currentNotice && newValue != null && currentNotice.isLeaf()) {
					currentNotice.setStatus(NoticeStatus.getStatusCode(newValue));
				}
			}
		});
		open(null);
	}
	
	public void open(NoticeTreeItem item) {
		if (item == null || item.isBranch()) {
			choiceBoxNoticeStatus.getSelectionModel().clearSelection();
			settingsPane.setDisable(true);
		} else {
			choiceBoxNoticeStatus.setItems(NoticeStatus.asObservable());
			choiceBoxNoticeStatus.getSelectionModel().select(NoticeStatus.getStatusName(item.getStatus()));
			settingsPane.setDisable(false);
		}
	}

	@FXML
	private void handleRemoveAttach(ActionEvent event) {
		
	}

	@FXML
	private void handleSelectAttach(ActionEvent event) {
	}
	
}

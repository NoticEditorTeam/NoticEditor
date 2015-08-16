package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.model.NoticeStatus;
import com.temporaryteam.noticeditor.model.NoticeStatusList;
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
	private ChoiceBox<NoticeStatus> choiceBoxNoticeStatus;
	
	private NoticeController noticeController;

	public void setNoticeController(NoticeController noticeController) {
		this.noticeController = noticeController;
	}
	
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		NoticeStatusList.add(rb.getString("normal"));
		NoticeStatusList.add(rb.getString("important"));
		NoticeStatusList.save();
		updateStatuses();
		
		choiceBoxNoticeStatus.getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends NoticeStatus> observable, NoticeStatus oldValue, NoticeStatus newValue) -> {
				NoticeTreeItem currentNotice = noticeController.getCurrentNotice();
				if (currentNotice != null && newValue != null && currentNotice.isLeaf()) {
					currentNotice.setStatus(newValue.getCode());
				}
			}
		);
		
		open(null);
	}
	
	/**
	 * Updates items (statuses) in choice box
	 */
	public void updateStatuses() {
		choiceBoxNoticeStatus.setItems(NoticeStatusList.asObservable());
	}
	
	public void open(NoticeTreeItem item) {
		if (item == null || item.isBranch()) {
			choiceBoxNoticeStatus.getSelectionModel().clearSelection();
			settingsPane.setDisable(true);
		} else {
			choiceBoxNoticeStatus.getSelectionModel().select(NoticeStatusList.getStatus(item.getStatus()));
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

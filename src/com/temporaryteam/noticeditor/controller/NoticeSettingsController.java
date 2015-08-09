package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

/**
 * FXML Controller class.
 * Responsible for attaching files and note importance.
 *
 * @author Naik
 */
public class NoticeSettingsController implements Initializable {
	@FXML
	private ListView<?> listAttached;
	@FXML
	private Button btnRemoveFile;
	@FXML
	private Button btnSelectFile;
	@FXML
	private ChoiceBox<String> choiceBoxNoticeStatus;
	
	private final NoticeController noticeController;

	public NoticeSettingsController(NoticeController noticeController) {
		this.noticeController = noticeController;
	}
	
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		choiceBoxNoticeStatus.setItems(FXCollections.observableArrayList(
				"Normal", "Important"
		));
		choiceBoxNoticeStatus.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				NoticeTreeItem noticeTreeItem = noticeController.getCurrentNotice();
				if (noticeTreeItem != null && noticeTreeItem.isLeaf()) {
					switch(newValue.intValue()) {
						case 0: noticeTreeItem.setStatus(NoticeTreeItem.STATUS_NORMAL); break;
						case 1: noticeTreeItem.setStatus(NoticeTreeItem.STATUS_IMPORTANT); break;
					}
				}
			}
		});
	}	

	@FXML
	private void handleRemoveAttach(ActionEvent event) {
	}

	@FXML
	private void handleSelectAttach(ActionEvent event) {
	}
	
}

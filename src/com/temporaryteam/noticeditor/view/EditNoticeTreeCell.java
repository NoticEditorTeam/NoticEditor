package com.temporaryteam.noticeditor.view;

import com.temporaryteam.noticeditor.model.NoticeItem;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class EditNoticeTreeCell extends TreeCell<NoticeItem> {

	private final Circle CIRCLE_AQUAMARINE = new Circle(5, Color.AQUAMARINE);
	private final Circle CIRCLE_YELLOW = new Circle(5, Color.YELLOW);
	
	private TextField noticeNameField;

	@Override
	public void startEdit() {
		super.startEdit();
		if (noticeNameField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(noticeNameField);
		noticeNameField.requestFocus();
		noticeNameField.selectAll();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem().getTitle());
		setGraphic(getIcon());
	}

	@Override
	public void commitEdit(NoticeItem item) {
		super.commitEdit(item);
		getNoticeTreeItem().setTitle(item.getTitle());
	}

	@Override
	public void updateItem(NoticeItem item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (noticeNameField != null) {
					noticeNameField.setText(getTitle());
				}
				setText(null);
				setGraphic(noticeNameField);
			} else {
				setText(getTitle());
				setGraphic(getIcon());
			}
		}
	}

	private NoticeTreeItem getNoticeTreeItem() {
		return (NoticeTreeItem) getTreeItem();
	}

	private void createTextField() {
		noticeNameField = new TextField(getTitle());
		noticeNameField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					getItem().setTitle(noticeNameField.getText());
					commitEdit(getItem());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	private Node getIcon() {
		if (getNoticeTreeItem().isBranch()) {
			return null;
		}
		switch (getNoticeTreeItem().getStatus()) {
			case NoticeItem.STATUS_IMPORTANT:
				return CIRCLE_YELLOW;
			default:
				return CIRCLE_AQUAMARINE;
		}
	}
	
	/**
	 *
	 * @return selected item's title or empty string
	 */
	private String getTitle() {
		return ((getItem() == null) ? "" : getItem().getTitle());
	}

}

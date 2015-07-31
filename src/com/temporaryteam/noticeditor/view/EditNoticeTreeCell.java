package com.temporaryteam.noticeditor.view;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EditNoticeTreeCell extends TreeCell<String> {

	private TextField noticeName;

	public EditNoticeTreeCell() {
	}

	@Override
	public void startEdit() {
		super.startEdit();
		if(noticeName==null) {
			createTextField();
		}
		setText(null);
		setGraphic(noticeName);
		noticeName.selectAll();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText((String)getItem());
		setGraphic(getTreeItem().getGraphic());
	}

	@Override
	public void commitEdit(String str) {
		super.commitEdit(str);
		getNoticeTreeItem().getNotice().setName(str);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if(empty) {
			setText(null);
			setGraphic(null);
		} else {
			if(isEditing()) {
				if(noticeName != null) {
					noticeName.setText(getString());
				}
				setText(null);
				setGraphic(noticeName);
			} else {
				setText(getString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}
	
	private NoticeTreeItem getNoticeTreeItem() {
		return (NoticeTreeItem)getTreeItem();
	}

	private void createTextField() {
		noticeName = new TextField(getString());
		noticeName.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if(t.getCode() == KeyCode.ENTER) {
					commitEdit(noticeName.getText());
				} else if(t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	private String getString() {
		return ((getItem() == null) ? "" : getItem().toString());
	}
}

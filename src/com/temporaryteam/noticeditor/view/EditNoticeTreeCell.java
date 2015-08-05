package com.temporaryteam.noticeditor.view;

import com.temporaryteam.noticeditor.controller.NoticeController;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EditNoticeTreeCell extends TreeCell<String> {

	private TextField noticeName;
	private NoticeController controller;

	// TODO: decompose method to handlers?
	public void handleContextMenu(ActionEvent e) {
		MenuItem source = (MenuItem) e.getSource();
		NoticeTreeItem selected = controller.getCurrentTreeItem();
		ObservableList<NoticeTreeItem> childTreeItems;
		if (selected != null) {
			if (selected.isLeaf() || source == controller.getDeleteItem()) {
				childTreeItems = selected.getParent().getChildren();
			} else {
				childTreeItems = selected.getChildren();
			}
		} else {
			childTreeItems = ((NoticeTreeItem) (getTreeView().getRoot())).getChildren();
		}
		if (source == controller.getAddBranchItem()) {
			childTreeItems.add(new NoticeTreeItem("New branch"));
		} else if (source == controller.getAddNoticeItem()) {
			childTreeItems.add(new NoticeTreeItem("New notice", ""));
		} else if (source == controller.getDeleteItem()) {
			childTreeItems.remove(selected);
		}
	}

	@Override
	public void startEdit() {
		super.startEdit();
		if (noticeName == null) {
			createTextField();
		}
		setText(null);
		setGraphic(noticeName);
		noticeName.selectAll();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem());
		setGraphic(getTreeItem().getGraphic());
	}

	@Override
	public void commitEdit(String str) {
		super.commitEdit(str);
		getNoticeTreeItem().setTitle(str);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (noticeName != null) {
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
		return (NoticeTreeItem<String>) getTreeItem();
	}

	private void createTextField() {
		noticeName = new TextField(getString());
		noticeName.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(noticeName.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	/**
	 *
	 * @return selected item or empty string
	 */
	private String getString() {
		return ((getItem() == null) ? "" : getItem());
	}

	public NoticeController getController() {
		return controller;
	}

	public void setController(NoticeController controller) {
		this.controller = controller;
	}

}

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
<<<<<<< HEAD
		else {
			children = ((NoticeTreeItem)(getTreeView().getRoot())).getChildren();
			subcategories = ((NoticeTreeItem)(getTreeView().getRoot())).getNotice().getSubCategories();
		}
		if(source.equals(controller.getAddBranchItem())) {
			ArrayList<NoticeCategory> list = new ArrayList<>();
			NoticeCategory toAdd = new NoticeCategory("New branch", list);
			NoticeTreeItem newBranch = new NoticeTreeItem(toAdd);
			children.add(newBranch);
			subcategories.add(toAdd);
		}
		else if(source.equals(controller.getAddNoticeItem())) {
                        NoticeCategory toAdd = new NoticeCategory("New notice", "");
			NoticeTreeItem newNotice = new NoticeTreeItem(toAdd);
			children.add(newNotice);
			subcategories.add(toAdd);
		}
		else if(source.equals(controller.getDeleteItem())) {
			NoticeCategory toDel = getNoticeTreeItem().getNotice();
			children = getNoticeTreeItem().getParent().getChildren();
			children.remove(getNoticeTreeItem());
			deleteNode(toDel);
=======
		if (source == controller.getAddBranchItem()) {
			childTreeItems.add(new NoticeTreeItem("New branch"));
		} else if (source == controller.getAddNoticeItem()) {
			childTreeItems.add(new NoticeTreeItem("New notice", ""));
		} else if (source == controller.getDeleteItem()) {
			childTreeItems.remove(selected);
<<<<<<< HEAD
			childItems.remove(toDel);
>>>>>>> 412f0999eb1ebd77267bb7d60dabbb044df7bbcb
=======
>>>>>>> 4d03a274ed3fb8f22b5d82146ed6b4bb05bbe517
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

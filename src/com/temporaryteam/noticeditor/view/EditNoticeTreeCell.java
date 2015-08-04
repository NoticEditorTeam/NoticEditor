package com.temporaryteam.noticeditor.view;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.temporaryteam.noticeditor.model.NoticeItem;

public class EditNoticeTreeCell extends TreeCell<String> {

	private TextField noticeName;
	private NoticeController controller;

	public void handleContextMenu(ActionEvent e) {
		MenuItem source = (MenuItem)e.getSource();
		NoticeTreeItem selected = controller.getCurrentTreeItem();
		
		ArrayList<NoticeItem> subcategories;
		ObservableList<NoticeTreeItem> children;
		if(selected != null) {
			if(selected.getNotice().getContent() !=null || source == controller.getDeleteItem()) {
				children = selected.getParent().getChildren();
				subcategories = ((NoticeTreeItem)(selected.getParent())).getNotice().childrens();
			}
			else {
				children = selected.getChildren();
				subcategories = selected.getNotice().childrens();
			}
		}
		else {
			children = ((NoticeTreeItem)(getTreeView().getRoot())).getChildren();
			subcategories = ((NoticeTreeItem)(getTreeView().getRoot())).getNotice().childrens();
		}
		if(source.equals(controller.getAddBranchItem())) {
			ArrayList<NoticeItem> list = new ArrayList<>();
			NoticeItem toAdd = new NoticeItem("New branch", list);
			NoticeTreeItem newBranch = new NoticeTreeItem(toAdd);
			children.add(newBranch);
			subcategories.add(toAdd);
		}
		else if(source.equals(controller.getAddNoticeItem())) {
                        NoticeItem toAdd = new NoticeItem("New notice", "");
			NoticeTreeItem newNotice = new NoticeTreeItem(toAdd);
			children.add(newNotice);
			subcategories.add(toAdd);
		}
		else if(source.equals(controller.getDeleteItem())) {
			NoticeItem toDel = selected.getNotice();
			children = selected.getParent().getChildren();
			children.remove(selected);
			deleteNode(toDel);
		}
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
		return (NoticeTreeItem<String>)getTreeItem();
	}

	private void deleteNode(NoticeItem node) {
		if(node.isBranch()) {
			for(NoticeItem category : node.childrens()) {
				deleteNode(category);
			}
			node.childrens().clear();
		} else {
			node.setContent(null);
		}
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
		return ((getItem() == null) ? "" : getItem());
	}

	public NoticeController getController() {
		return controller;
	}
	
	public void setController(NoticeController controller) {
		this.controller = controller;
	}

}

package com.temporaryteam.noticeditor.view;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.temporaryteam.noticeditor.model.NoticeCategory;

public class EditNoticeTreeCell extends TreeCell<String> {

	private TextField noticeName;
	private NoticeController controller;

	public EditNoticeTreeCell() {
		setOnMouseReleased(new EventHandler<MouseEvent>() {
                        @Override
			public void handle(MouseEvent t) {
				if(getNoticeTreeItem()!=null) {
					if(getNoticeTreeItem().getNotice().getContent()!=null) {
						controller.setCurrentTreeItem(getNoticeTreeItem());
						controller.open(getNoticeTreeItem().getNotice().getContent());
					}
				}
			}
		});
	}

	public void handleContextMenu(ActionEvent e) {
		MenuItem source = (MenuItem)e.getSource();
		ArrayList<NoticeCategory> subcategories;
		ObservableList<NoticeTreeItem> children;
		if(getTreeItem()!=null) {
			if((getNoticeTreeItem().getNotice().getContent()!=null)||(source.equals(controller.getDeleteItem()))) {
				children = getNoticeTreeItem().getParent().getChildren();
				subcategories = ((NoticeTreeItem)(getNoticeTreeItem().getParent())).getNotice().getSubCategories();
			}
			else {
				children = getNoticeTreeItem().getChildren();
				subcategories = getNoticeTreeItem().getNotice().getSubCategories();
			}
		}
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

	private void deleteNode(NoticeCategory node) {
		if(node.getSubCategories()==null) {
			node.setContent(null);
		}
		else {
			for(NoticeCategory category : node.getSubCategories()) {
				deleteNode(category);
			}
			node.setSubCategories(null);
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

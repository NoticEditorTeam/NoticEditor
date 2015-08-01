package com.temporaryteam.noticeditor.view;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.temporaryteam.noticeditor.model.NoticeCategory;

public class EditNoticeTreeCell extends TreeCell<String> {

	private TextField noticeName;
	private ContextMenu branchMenu;
	private ContextMenu noticeMenu;
	private NoticeController controller;

	public EditNoticeTreeCell() {
		branchMenu = new ContextMenu();
		noticeMenu = new ContextMenu();
		MenuItem addBranchItem = new MenuItem("Add branch");
		MenuItem addNoticeItem = new MenuItem("Add notice");
		MenuItem deleteItem = new MenuItem("Delete");
		MenuItem deleteItem2 = new MenuItem("Delete");
		branchMenu.getItems().addAll(addBranchItem, addNoticeItem, deleteItem);
		noticeMenu.getItems().add(deleteItem2);
		addBranchItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				ArrayList<NoticeCategory> list = new ArrayList<NoticeCategory>();
				NoticeCategory branch = new NoticeCategory("New branch", list);
				NoticeTreeItem newBranch = new NoticeTreeItem<String>(branch);
				getTreeItem().getChildren().add(newBranch);
				getNoticeTreeItem().getNotice().getSubCategories().add(branch);
			}
		});
		addNoticeItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				NoticeCategory notice = new NoticeCategory("New notice", "Your notice");
				NoticeTreeItem newNotice = new NoticeTreeItem<String>(notice);
				getTreeItem().getChildren().add(newNotice);
				getNoticeTreeItem().getNotice().getSubCategories().add(notice);
			}
		});
		EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				NoticeCategory notice = getNoticeTreeItem().getNotice();
				NoticeTreeItem deletingNotice = getNoticeTreeItem();
				if(!(deletingNotice.getParent()==null)) deletingNotice.getParent().getChildren().remove(deletingNotice);
				deleteNode(notice);
				notice = null;
				deletingNotice = null;
			}
		};
		deleteItem.setOnAction(handler);
		deleteItem2.setOnAction(handler);
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent t) {
				if(getNoticeTreeItem().getNotice().getContent()!=null) {
					controller.setCurrentTreeItem(getNoticeTreeItem());
					controller.open(getNoticeTreeItem().getNotice().getContent());
				}
			}
		});
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
				if(!getTreeItem().isLeaf()) setContextMenu(branchMenu);
				else setContextMenu(noticeMenu);
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
		return ((getItem() == null) ? "" : getItem().toString());
	}

	public NoticeController getController() {
		return controller;
	}
	
	public void setController(NoticeController controller) {
		this.controller = controller;
	}

}

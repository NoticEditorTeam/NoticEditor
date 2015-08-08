package com.temporaryteam.noticeditor.model;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public NoticeTree extends TreeView<String>() {

	public NoticeTree() {
		super();
	}

	public NoticeTree(NoticeTreeItem root) {
		super(root);
	}

	public NoticeTree(JSONObject jsobj) throws JSONException {
		NoticeTreeItem root = new NoticeTreeItem(jsobj);
		super(root);
	}

	public void addNote(NoticeTreeItem level, String name, String content) {
		NoticeTreeItem item = new NoticeTreeItem(name, content);
		if(level.isLeaf()) {
			level.getParent().getChildren().add(item);
		}
		else {
			level.getChildren().add(item);
		}
	}

	public void addBranch(NoticeTreeItem level, String name) {
		NoticeTreeItem branch = new NoticeTreeItem(name);
		if(level.isLeaf()) {
			branch.getParent().getChildren().add(branch);
		}
		else {
			level.getChildren().add(branch);
		}
	}

	public void deleteNode(NoticeTreeItem toDel) {
		if(!isLeaf()) {
			for(NoticeTreeItem son : toDel.getChildren()) deleteNode(son);
		}
		else {
			if(toDel.getParent()!=null) {
				toDel.getParent().getChildren().remove(toDel);
			}
		}
	}

	public void toHTML(PegDownProcessor, Document doc) {
		getRoot().toHTML();
	}

	public JSONObject toJson() throws JSONException {
		return getRoot().toJson();
	}

}

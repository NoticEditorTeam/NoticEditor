package com.temporaryteam.noticeditor.model;

import org.pegdown.PegDownProcessor;
import org.jsoup.nodes.Document;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class NoticeTree extends TreeView<String> {

	public NoticeTree() {
		super();
	}

	public NoticeTree(NoticeTreeItem root) {
		super(root);
	}

	public NoticeTree(JSONObject jsobj) throws JSONException {
		super(new NoticeTreeItem(jsobj));
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
		if(toDel.isLeaf()) {
			for(Object son : toDel.getChildren()) deleteNode((NoticeTreeItem)son);
		}
		else {
			if(toDel.getParent()!=null) {
				toDel.getParent().getChildren().remove(toDel);
			}
		}
	}

	public void toHTML(PegDownProcessor processor, Document doc) {
		((NoticeTreeItem)getRoot()).toHTML(processor, doc, "index");
	}

	public JSONObject toJson() throws JSONException {
		return ((NoticeTreeItem)getRoot()).toJson();
	}

}

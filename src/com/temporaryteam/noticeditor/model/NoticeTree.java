package com.temporaryteam.noticeditor.model;

import java.util.ArrayDeque;

import org.pegdown.PegDownProcessor;
import org.jsoup.nodes.Document;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class NoticeTree {

	private NoticeTreeItem root;

	public NoticeTree() {
		root = null;
	}

	public NoticeTree(NoticeTreeItem root) {
		this.root = root;
	}

	public NoticeTree(JSONObject jsobj) throws JSONException {
		root = new NoticeTreeItem(jsobj);
	}

	public NoticeTreeItem getRoot() {
		return root;
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
		ArrayDeque<NoticeTreeItem> items = new ArrayDeque<NoticeTreeItem>();
		items.push(toDel);
		while(!items.isEmpty()) {
			NoticeTreeItem currentItem = items.pop();
			if(currentItem.isLeaf()) {
				currentItem.getParent().getChildren().remove(currentItem);
				currentItem = null;
			}
			else {
				for(Object son : currentItem.getChildren()) {
					items.push((NoticeTreeItem)son);
				}
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

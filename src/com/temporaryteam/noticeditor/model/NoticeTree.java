package com.temporaryteam.noticeditor.model;

import java.util.ArrayDeque;

import org.json.JSONException;
import org.json.JSONObject;
import javafx.scene.control.TreeItem;

public class NoticeTree {

	private final NoticeTreeItem root;

	public NoticeTree() {
		root = null;
	}

	/**
	 * Create NoticeTree with set root
	 * @param root set root
	 */
	public NoticeTree(NoticeTreeItem root) {
		this.root = root;
	}
	
	/**
	 * Import NoticeTree from JSON
	 * @param jsobj object to import from
	 */
	public NoticeTree(JSONObject jsobj) throws JSONException {
		this(new NoticeTreeItem(""));
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
				for(TreeItem<String> son : currentItem.getChildren()) {
					items.push((NoticeTreeItem)son);
				}
			}
		}
	}

	public JSONObject toJson() throws JSONException {
		return root.toJson();
	}

}

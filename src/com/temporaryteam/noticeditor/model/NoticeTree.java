package com.temporaryteam.noticeditor.model;

import static com.temporaryteam.noticeditor.model.NoticeTreeItem.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.collections.ObservableList;
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
		root = new NoticeTreeItem(jsobj);
	}

	public NoticeTreeItem getRoot() {
		return root;
	}

	/**
	 * @param item to add
	 * @param parent if null, item will be added to root item.
	 */
	public void addItem(NoticeTreeItem item, NoticeTreeItem parent) {
		if (parent == null) {
			parent = root;
		} else if (parent.isLeaf()) {
			parent = (NoticeTreeItem) parent.getParent();
		}
		parent.getChildren().add(item);
		parent.setExpanded(true);
	}

	public void removeItem(NoticeTreeItem item) {
		if (item == null) return;
		item.getParent().getChildren().remove(item);
	}

	public JSONObject toJson() throws JSONException {
		return root.toJson();
	}

}

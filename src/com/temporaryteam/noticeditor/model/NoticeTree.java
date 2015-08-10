package com.temporaryteam.noticeditor.model;

import org.json.JSONException;
import org.json.JSONObject;

public class NoticeTree {

	private final NoticeTreeItem root;

	public NoticeTree(NoticeTreeItem root) {
		this.root = root;
	}

	public NoticeTree(JSONObject jsobj) throws JSONException {
		root = new NoticeTreeItem(jsobj);
	}

	public NoticeTreeItem getRoot() {
		return root;
	}

	/**
	 * @param item
	 * @param parent if null, item will be added to root item.
	 */
	public void addItem(NoticeTreeItem item, NoticeTreeItem parent) {
		if (parent == null) {
			parent = root;
		} else if (parent.isLeaf()) {
			parent = (NoticeTreeItem) parent.getParent();
		}
		parent.getChildren().add(item);
	}

	public void removeItem(NoticeTreeItem item) {
		if (item == null) return;
		item.getParent().getChildren().remove(item);
	}

	public JSONObject toJson() throws JSONException {
		return root.toJson();
	}

}

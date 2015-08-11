package com.temporaryteam.noticeditor.model;

import java.util.ArrayDeque;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

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
		root = new NoticeTreeItem(jsobj.getString(NoticeTreeItem.KEY_TITLE), jsobj.optString(NoticeTreeItem.KEY_CONTENT, null));
		ArrayDeque<Pair<JSONObject,ObservableList<TreeItem<String>>>> stack = new ArrayDeque<>();
		stack.push(new Pair(jsobj, null));
		while(!stack.isEmpty()) {
			final Pair<JSONObject,ObservableList<TreeItem<String>>> currentPair = stack.pop();
			final JSONObject currentObject = currentPair.getKey();
			NoticeTreeItem currentItem = new NoticeTreeItem(currentObject.getString(NoticeTreeItem.KEY_TITLE),
									currentObject.optString(NoticeTreeItem.KEY_CONTENT, null));
			JSONArray arr = currentObject.getJSONArray(NoticeTreeItem.KEY_CHILDREN);
			for(int i = arr.length() - 1; i>-1; i++) {
				stack.push(new Pair(arr.getJSONObject(i), currentItem.getChildren()));
			}
			if(currentPair.getValue()!=null) currentPair.getValue().add(currentItem);
		}
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
		JSONObject jsobj = new JSONObject();
		jsobj.put(NoticeTreeItem.KEY_TITLE, root.getTitle());
		jsobj.put(NoticeTreeItem.KEY_CONTENT, root.getContent());
		ArrayDeque<Pair<NoticeTreeItem,JSONArray>> stack = new ArrayDeque<>();
		stack.push(new Pair(root, null));
		while(!stack.isEmpty()) {
			final Pair<NoticeTreeItem,JSONArray> currentPair = stack.pop();
			final NoticeTreeItem currentItem = currentPair.getKey();
			JSONObject currentObject = new JSONObject();
			currentObject.put(NoticeTreeItem.KEY_TITLE, currentItem.getTitle());
			currentObject.putOpt(NoticeTreeItem.KEY_CONTENT, root.getContent());
			ObservableList<TreeItem<String>> arr = currentItem.getChildren();
			for(TreeItem<String> item : arr) {
				stack.push(new Pair(item, new JSONArray())); 
			}
			if(currentPair.getValue()!=null) currentPair.getValue().put(currentObject);
		}
		return jsobj;
	}

}

package com.temporaryteam.noticeditor.model;

import static com.temporaryteam.noticeditor.model.NoticeItem.*;
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
		root = new NoticeTreeItem(
				jsobj.getString(KEY_TITLE),
				jsobj.optString(KEY_CONTENT, null),
				jsobj.optInt(KEY_STATUS, STATUS_NORMAL));
		
		ArrayDeque<Pair<JSONObject, ObservableList<TreeItem<NoticeItem>>>> stack = new ArrayDeque<>();
		// Add root items to stack
		final JSONArray rootChildrenArray = jsobj.getJSONArray(KEY_CHILDREN);
		for (int i = 0; i < rootChildrenArray.length(); i++) {
			stack.addLast(new Pair<>(rootChildrenArray.getJSONObject(i), root.getInternalChildren()));
		}
		while(!stack.isEmpty()) {
			final Pair<JSONObject, ObservableList<TreeItem<NoticeItem>>> currentPair = stack.pop();
			final JSONObject currentObject = currentPair.getKey();
			
			NoticeTreeItem currentItem = new NoticeTreeItem(
					currentObject.getString(KEY_TITLE),
					currentObject.optString(KEY_CONTENT, null),
					currentObject.optInt(KEY_STATUS, STATUS_NORMAL));
			if (currentObject.has(KEY_CHILDREN)) {
				JSONArray childrenArray = currentObject.getJSONArray(KEY_CHILDREN);
				for (int i = 0; i < childrenArray.length(); i++) {
					stack.addLast(new Pair(childrenArray.getJSONObject(i), currentItem.getInternalChildren()));
				}
			}
			if (currentPair.getValue() != null) {
				currentPair.getValue().add(currentItem);
			}
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
		parent.getInternalChildren().add(item);
		parent.setExpanded(true);
	}

	public void removeItem(NoticeTreeItem item) {
		if (item == null) return;
		NoticeTreeItem parent = (NoticeTreeItem) item.getParent();
		if (parent == null) return;
		parent.getInternalChildren().remove(item);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject jsobj = new JSONObject();
		jsobj.put(KEY_TITLE, root.getTitle());
		jsobj.putOpt(KEY_STATUS, root.getStatus());
		jsobj.putOpt(KEY_CONTENT, root.getContent());
		jsobj.put(KEY_CHILDREN, new JSONArray());
		
		ArrayDeque<Pair<NoticeTreeItem, JSONObject>> stack = new ArrayDeque<>();
		// Add root items to stack
		for (TreeItem<NoticeItem> item : root.getInternalChildren()) {
			stack.addLast(new Pair(item, jsobj)); 
		}
		while(!stack.isEmpty()) {
			final Pair<NoticeTreeItem, JSONObject> currentPair = stack.pop();
			final NoticeTreeItem currentItem = currentPair.getKey();
			final JSONObject parentObject = currentPair.getValue();
			
			JSONObject newObject = new JSONObject();
			newObject.put(KEY_TITLE, currentItem.getTitle());
			newObject.putOpt(KEY_STATUS, currentItem.getStatus());
			newObject.putOpt(KEY_CONTENT, currentItem.getContent());
			
			if (currentItem.isBranch()) {
				// Mark newObject as parent for next children
				newObject.put(KEY_CHILDREN, new JSONArray());
				for (TreeItem<NoticeItem> item : currentItem.getInternalChildren()) {
					stack.addLast(new Pair(item, newObject)); 
				}
			}
			
			// Add created object to parent children array
			parentObject.getJSONArray(KEY_CHILDREN).put(newObject);
		}
		return jsobj;
	}

}

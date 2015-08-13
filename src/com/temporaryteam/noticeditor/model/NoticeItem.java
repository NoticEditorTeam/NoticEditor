package com.temporaryteam.noticeditor.model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @author naik, setser, annimon, kalter
 */
public class NoticeItem {

	public static final String KEY_TITLE = "title";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_CHILDREN = "children";
	public static final String KEY_STATUS = "status";

	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_IMPORTANT = 1;

	private String title;
	private ObservableList<NoticeItem> children;
	private String content;
	private int status;

	/**
	 * Create branch node on tree.
	 *
	 * @param title
	 */
	public NoticeItem(String title) {
		this(title, null, 0);
	}
	
	/**
	 * Create leaf node on tree.
	 * @param title
	 * @param content 
	 */
	public NoticeItem(String title, String content) {
		this(title, content, STATUS_NORMAL);
	}

	/**
	 * Create leaf node on tree.
	 *
	 * @param title
	 * @param content
	 * @param status
	 */
	public NoticeItem(String title, String content, int status) {
		this.title = title;
		this.content = content;
		this.status = status;
		children = FXCollections.observableArrayList();
	}

	public NoticeItem(JSONObject json) throws JSONException {
		this(json.getString(KEY_TITLE), json.optString(KEY_CONTENT, null), json.optInt(KEY_STATUS, STATUS_NORMAL));
		JSONArray arr = json.getJSONArray(KEY_CHILDREN);
		for (int i = 0; i < arr.length(); i++) {
			children.add(new NoticeItem(arr.getJSONObject(i)));
		}
	}

	public void addChild(NoticeItem item) {
		children.add(item);
	}

	public boolean isLeaf() {
		return content != null;
	}

	/**
	 * @return true if content == null
	 */
	public boolean isBranch() {
		return content == null;
	}

	/**
	 * @return notice content or null if its a branch
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Content will be changed only when is a leaf node.
	 *
	 * @param content
	 */
	public void changeContent(String content) {
		if (isLeaf()) {
			this.content = content;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_TITLE, title);
		if (isLeaf()) {
			json.put(KEY_STATUS, status);
			json.put(KEY_CONTENT, content);
		}
		ArrayList list = new ArrayList();
		for (NoticeItem child : children) {
			list.add(child.toJson());
		}
		json.put(KEY_CHILDREN, new JSONArray(list));
		return json;
	}

	@Override
	public String toString() {
		return title + "\n\n" + content;
	}
}

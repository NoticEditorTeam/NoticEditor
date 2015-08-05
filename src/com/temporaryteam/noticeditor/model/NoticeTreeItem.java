package com.temporaryteam.noticeditor.model;

import java.util.ArrayList;
import java.util.Random;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pegdown.PegDownProcessor;

/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @param <T> tree item name
 * @author naik, setser, annimon, kalter
 */
public class NoticeTreeItem<T extends String> extends TreeItem {

	public static final String KEY_TITLE = "title";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_CHILDS = "childs";

	private static final Random RND = new Random();

	private String title;
	private ObservableList<NoticeTreeItem> childs;
	private String content;
	private long id;

	/**
	 * Create branch node on tree.
	 *
	 * @param title
	 */
	public NoticeTreeItem(String title) {
		super(title);
		this.title = title;
		childs = getChildren();
	}

	/**
	 * Create leaf node on tree.
	 *
	 * @param title
	 * @param content
	 */
	public NoticeTreeItem(String title, String content) {
		this(title);
		this.content = content;
	}

	public NoticeTreeItem(JSONObject json) throws JSONException {
		title = json.getString(KEY_TITLE);
		if (json.has(KEY_CONTENT)) {
			content = json.getString(KEY_CONTENT);
		}
		JSONArray arr = json.getJSONArray(KEY_CHILDS);
		childs = getChildren();
		for (int i = 0; i < arr.length(); i++) {
			childs.add(new NoticeTreeItem(arr.getJSONObject(i)));
		}
		genId();
		setValue(title);
	}

	private void genId() {
		id = System.nanoTime() + RND.nextInt(100);
	}

	/**
	 * id used for diff nodes with equals name (for example when exporting to HTML).
	 *
	 * @return generated unique id
	 */
	public long getId() {
		return id;
	}

	@Override
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

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		setValue(title);
		this.title = title;
	}

	public String toHTML(PegDownProcessor processor) {
		StringBuilder html = new StringBuilder();
		if (isBranch()) {
			for (NoticeTreeItem child : childs) {
				html.append("<a href=\"").append(child.getId()).append(".html\">");
				html.append(child.getTitle()).append(".html</a><br/>\n");
			}
			html.append("<br/><br/>\n");
		} else {
			html.append(processor.markdownToHtml(content));
		}
		return html.toString();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_TITLE, title);
		if (content != null) {
			json.put(KEY_CONTENT, content);
		}
		ArrayList list = new ArrayList();
		for (NoticeTreeItem child : childs) {
			list.add(child.toJson());
		}
		json.put(KEY_CHILDS, new JSONArray(list));
		return json;
	}

}

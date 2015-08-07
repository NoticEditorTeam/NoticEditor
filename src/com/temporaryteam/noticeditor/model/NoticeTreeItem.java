package com.temporaryteam.noticeditor.model;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_IMPORTANT = 2;

	private static int id_counter = 1;

	private String title;
	private ObservableList<NoticeTreeItem> childs;
	private String content;
	private final long id = id_counter++;
	private int status = STATUS_NORMAL;

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
		setValue(title);
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
		setValue(title);
		this.title = title;
	}

	public void setStatus(int status) {
		this.status = status;
		Event.fireEvent(this, new TreeModificationEvent(childrenModificationEvent(), this));
	}

	public int getStatus() {
		return status;
	}

	public void toHTML(PegDownProcessor processor, Document doc) {
		doc.title(title);
		doc.select("#notice_title").first().text(title);
		Element data = doc.select("#content").first();
		if (isBranch()) {
			Element list = doc.createElement("div").addClass("list-group");
			for (NoticeTreeItem child : childs) {
				Element item = doc.createElement("div").addClass("list-group-item");
				if (child.isBranch()) {
					item.appendElement("span").addClass("glyphicon glyphicon-folder-open");
				} else {
					switch (child.getStatus()) {
						case STATUS_IMPORTANT:
							item.appendElement("span").addClass("glyphicon glyphicon-paperclip important");
							break;
						default:
							item.appendElement("span").addClass("glyphicon glyphicon-paperclip normal");
					}
				}
				item.appendElement("a").attr("href", child.getId() + ".html")
						.text(child.getTitle())
						.appendElement("br");
				list.appendChild(item);
			}
			data.appendChild(list);
		} else {
			data.html(processor.markdownToHtml(content));
		}
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

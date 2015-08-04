package com.temporaryteam.noticeditor.model;

import java.util.ArrayList;
import java.util.Random;

import org.pegdown.PegDownProcessor;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// TODO: remove this class (use NoticeTreeItem)
/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @author naik, setser, annimon, kalter
 */
public class NoticeItem {

	public static final String KEY_NAME = "name";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_CHILDS = "childs";

	private static final Random RND = new Random();
	
	private String name;
	private ArrayList<NoticeItem> childs = new ArrayList<>();
	private String content;
	private long id;

	public NoticeItem(String name, String content) {
		this.name = name;
		this.content = content;
		genId();
	}

	public NoticeItem(String name, ArrayList<NoticeItem> childs) {
		this.name = name;
		this.childs = childs;
		genId();
	}

	public NoticeItem(JSONObject json) throws JSONException {
		name = json.getString(KEY_NAME);
		if (json.has(KEY_CONTENT)) {
			content = json.getString(KEY_CONTENT);
		}
		JSONArray arr = json.getJSONArray(KEY_CHILDS);
		childs = new ArrayList();
		if (arr.length() != 0) {
			for (int i = 0; i < arr.length(); i++) {
				childs.add(new NoticeItem(arr.getJSONObject(i)));
			}
		}
		genId();
	}
	
	private void genId() {
		id = System.nanoTime() + RND.nextInt(100);
	}

	public long getId() {
		return id;
	}

	/**
	 *
	 * @return notice content or null if its a branch
	 */
	public String getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public ArrayList<NoticeItem> childrens() {
		return childs;
	}

	public boolean isBranch() {
		return content == null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubCategories(ArrayList<NoticeItem> childs) {
		this.childs = childs;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toHTML(PegDownProcessor processor) {
		StringBuilder html = new StringBuilder();
		if (isBranch()) {
			for (NoticeItem child : childs) {
				html.append("<a href=\"").append(child.getId()).append(".html\">");
				html.append(child.getName()).append(".html</a><br/>\n");
			}
			html.append("<br/><br/>\n");
		} else {
			html.append(processor.markdownToHtml(content));
		}
		return html.toString();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(KEY_NAME, name);
		if (content != null) {
			obj.put(KEY_CONTENT, content);
		}
		ArrayList list = new ArrayList();
		if (childs != null) {
			for (NoticeItem subcategory : childs) {
				list.add(subcategory.toJson());
			}
		}
		obj.put(KEY_CHILDS, new JSONArray(list));
		return obj;
	}

}

package com.temporaryteam.noticeditor.model;

import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class NoticeCategory {

	private String name;
	private NoticeCategory[] subcategories;
	private Notice content;

	public NoticeCategory() {
		name = null;
		content = null;
		subcategories = null;
	}

	public NoticeCategory(String name, Notice content) {
		this.name = name;
		this.content = content;
		subcategories = null;
	}

	public NoticeCategory(String name, NoticeCategory[] subcategories) {
		this.name = name;
		content = null;
		this.subcategories = subcategories;
	}

	public NoticeCategory(String name, NoticeCategory[] subcategories, Notice content) {
		this.name = name;
		this.content = content;
		this.subcategories = subcategories;
	}

	public String getName() {
		return name;
	}

	public NoticeCategory[] getSubCategories() {
		return subcategories;
	}

	public Notice getContent() {
		return content;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubCategories(NoticeCategory[] subcategories) {
		this.subcategories = subcategories;
	}

	public void setContent(Notice content) {
		this.content = content;
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("content", content.getNotice());
		Vector<JSONObject> vect = new Vector<JSONObject>();
		if(subcategories!=null) for(NoticeCategory subcategory : subcategories) vect.addElement(subcategory.toJson());
		obj.put("subcategories", new JSONArray(vect));
		return obj;
	}

	public void fromJson(JSONObject jsobj) throws JSONException {
		name = jsobj.getString("name");
		content = new Notice();
		content.fromJson(jsobj);
		JSONArray arr = jsobj.getJSONArray("subcategories");
		subcategories = new NoticeCategory[arr.length()];
		NoticeCategory category = new NoticeCategory();
		for(int i = 0; i<arr.length(); i++) {
			subcategories[i].fromJson(arr.getJSONObject(i));
		}
	}

}

package com.temporaryteam.noticeditor.model;

import org.json.JSONObject;
import org.json.JSONException;

public class Notice {

	private String notice;

	public Notice() {
		notice = null;
	}

	public Notice(String notice) {
		this.notice = notice;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("content", notice);
		return obj;
	}

	public void fromJson(JSONObject jsobj) throws JSONException {
		notice = jsobj.getString("content");
	}

}

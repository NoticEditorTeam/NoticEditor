package com.temporaryteam.noticeditor.io;

import static com.temporaryteam.noticeditor.io.JsonFields.*;
import com.temporaryteam.noticeditor.model.NoticeItem;
import com.temporaryteam.noticeditor.model.NoticeStatus;
import com.temporaryteam.noticeditor.model.NoticeTree;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Naik
 */
public class JsonFormat {

	public static JsonFormat with(File file) {
		return new JsonFormat(file);
	}

	private final File file;

	private JsonFormat(File file) {
		this.file = file;
	}

	public NoticeTree importDocument() throws IOException, JSONException {
		JSONObject json = new JSONObject(IOUtil.readContent(file));

		if (json.has(KEY_STATUSINFO)) {
			JSONObject status = json.getJSONObject(KEY_STATUSINFO);
			Iterator it = status.keys();
			if (it.hasNext()) {
				NoticeStatus.clear();
			}
			while (it.hasNext()) {
				String key = it.next().toString();
				NoticeStatus.add(key, status.getInt(key));
			}
		}
		
		return new NoticeTree(jsonToTree(json));
	}
	
	private NoticeTreeItem jsonToTree(JSONObject json) throws JSONException {
		NoticeTreeItem item = new NoticeTreeItem(json.getString(KEY_TITLE), json.optString(KEY_CONTENT, null),
				json.optInt(KEY_STATUS, NoticeItem.STATUS_NORMAL));
		JSONArray arr = json.getJSONArray(KEY_CHILDREN);
		for (int i = 0; i < arr.length(); i++) {
			item.addChild(jsonToTree(arr.getJSONObject(i)));
		}
		return item;
	}

	public void export(NoticeTree tree) throws JSONException, IOException {
		if (file.exists()) {
			file.delete();
		}
		IOUtil.writeJson(file, export(tree.getRoot()));
	}
	
	public JSONObject export(NoticeTreeItem root) throws JSONException {
		JSONObject json = new JSONObject();
		treeToJson(root, json);
		json.put("statusInfo", NoticeStatus.asObservableMap());
		return json;
	}

	private void treeToJson(NoticeTreeItem item, JSONObject json) throws JSONException {
		json.put(KEY_TITLE, item.getTitle());
		JSONArray childs = new JSONArray();
		if (item.isBranch()) {
			for (TreeItem<NoticeItem> object : item.getInternalChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) object;
				JSONObject entry = new JSONObject();
				treeToJson(child, entry);
				childs.put(entry);
			}
		} else {
			json.put(KEY_STATUS, item.getStatus());
			json.put(KEY_CONTENT, item.getContent());
		}
		json.put(KEY_CHILDREN, childs);
	}

}

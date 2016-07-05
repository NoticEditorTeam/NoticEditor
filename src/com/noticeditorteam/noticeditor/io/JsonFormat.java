package com.noticeditorteam.noticeditor.io;

import static com.noticeditorteam.noticeditor.io.JsonFields.*;
import com.noticeditorteam.noticeditor.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javafx.scene.control.TreeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
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
            JSONArray statusList = json.getJSONArray(KEY_STATUSINFO);
            if (statusList.length() > 0) {
                NoticeStatusList.clear();
            }
            // TODO refactor length const
            for (int i = 0; i < statusList.length(); i++) {
                JSONObject obj = (JSONObject) statusList.get(i);
                String name = obj.getString("name");
                int code = obj.getInt("code");
                NoticeStatusList.add(name, code);
            }
        } else {
            NoticeStatusList.restore();
        }
        return new NoticeTree(jsonToTree(json));
    }

    private NoticeTreeItem jsonToTree(JSONObject json) throws JSONException {
        NoticeTreeItem item = new NoticeTreeItem(json.getString(KEY_TITLE), json.optString(KEY_CONTENT, null),
                json.optInt(KEY_STATUS, NoticeItem.STATUS_NORMAL));
        if (json.has(KEY_ATTACHMENTS)) {
            Attachments attachments = readAttachments(json.getJSONArray(KEY_ATTACHMENTS));
            item.setAttachments(attachments);
        }
        JSONArray arr = json.getJSONArray(KEY_CHILDREN);
        for (int i = 0; i < arr.length(); i++) {
            item.addChild(jsonToTree(arr.getJSONObject(i)));
        }
        return item;
    }

    private Attachments readAttachments(JSONArray jsonAttachments) throws JSONException {
        Attachments attachments = new Attachments();
        final int length = jsonAttachments.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonAttachment = jsonAttachments.getJSONObject(i);
            Attachment attachment = new Attachment(
                    jsonAttachment.getString(KEY_ATTACHMENT_NAME),
                    Base64.getDecoder().decode(jsonAttachment.getString(KEY_ATTACHMENT_DATA)));
            attachments.add(attachment);
        }
        return attachments;
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

		json.put(KEY_STATUSINFO, NoticeStatusList.asObservable());

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
            JSONArray jsonAttachments = writeAttachments(item.getAttachments());
            json.put(KEY_ATTACHMENTS, jsonAttachments);
        }
        json.put(KEY_CHILDREN, childs);
    }

    private JSONArray writeAttachments(Attachments attachments) throws JSONException {
        final JSONArray jsonAttachments = new JSONArray();
        for (Attachment attachment : attachments) {
            final JSONObject jsonAttachment = new JSONObject();
            jsonAttachment.put(KEY_ATTACHMENT_NAME, attachment.getName());
            jsonAttachment.put(KEY_ATTACHMENT_DATA, attachment.getDataAsBase64());
            jsonAttachments.put(jsonAttachment);
        }
        return jsonAttachments;
    }

}

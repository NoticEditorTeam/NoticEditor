package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

/**
 * Export notices to json.
 * @author aNNiMON
 */
public class JsonExportStrategy implements ExportStrategy {

	@Override
	public void export(File file, NoticeTreeItem notice) {
		try {
			IOUtil.writeJson(file, notice.toJson());
		} catch (IOException | JSONException e) {
			throw new ExportException(e);
		}
	}

}

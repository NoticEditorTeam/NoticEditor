package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

/**
 * Export notices to json.
 * @author aNNiMON
 */
public class JsonExportStrategy implements ExportStrategy {

	@Override
	public void export(File file, NoticeTree notice) {
		try {
			IOUtil.writeJson(file, notice.toJson());
		} catch (IOException | JSONException e) {
			throw new ExportException(e);
		}
	}

}

package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides common operations with document.
 * @author aNNiMON
 */
public final class DocumentFormat {

	public static NoticeTreeItem open(File file) throws JSONException, IOException {
		try {
			return ZipWithIndexFormat.with(file).importDocument();
		} catch (ZipException | IOException | JSONException e) {
			JSONObject json = new JSONObject(IOUtil.readContent(file));
			return new NoticeTreeItem(json);
		}
	}
	
	public static void save(File file, NoticeTreeItem root, ExportStrategy strategy) {
		strategy.export(file, root);
	}
}

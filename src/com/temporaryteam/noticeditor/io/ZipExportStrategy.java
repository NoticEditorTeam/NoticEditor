package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

/**
 * Export document to zip archive with index.json.
 * @author aNNiMON
 */
public class ZipExportStrategy implements ExportStrategy {
	
	@Override
	public void export(File file, NoticeTreeItem notice) {
		try {
			ZipWithIndexFormat.with(file).export(notice);
		} catch (ZipException | IOException | JSONException e) {
			throw new ExportException(e);
		}
	}
}

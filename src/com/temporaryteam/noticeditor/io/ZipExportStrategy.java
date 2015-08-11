package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
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
	public void export(File file, NoticeTree notice) {
		try {
			if (file.exists()) file.delete();
			ZipWithIndexFormat.with(file).export(notice);
		} catch (ZipException | IOException | JSONException e) {
			throw new ExportException(e);
		}
	}
}

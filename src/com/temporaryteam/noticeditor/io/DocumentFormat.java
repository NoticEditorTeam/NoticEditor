package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

/**
 * Provides common operations with document.
 * @author aNNiMON
 */
public final class DocumentFormat {

	public static NoticeTree open(File file) throws JSONException, IOException {
		try {
			return ZipWithIndexFormat.with(file).importDocument();
		} catch (ZipException | IOException | JSONException e) {
			return JsonFormat.with(file).importDocument();
		}
	}
	
	public static void save(File file, NoticeTree tree, ExportStrategy strategy) {
		strategy.export(file, tree);
	}
}

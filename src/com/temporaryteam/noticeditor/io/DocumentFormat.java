package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.io.importers.FileImporter;
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

	public static NoticeTree open(File file) throws IOException {
		try {
			final String filename = file.getName().toLowerCase();
			if (filename.endsWith(".zip")) {
				return ZipWithIndexFormat.with(file).importDocument();
			}
			return JsonFormat.with(file).importDocument();
		} catch (ZipException | IOException | JSONException e) {
			return FileImporter.Tree.importFrom(file);
		}
	}
	
	public static void save(File file, NoticeTree tree, ExportStrategy strategy) {
		strategy.export(file, tree);
	}
}

package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Export document to zip archive with index.json.
 * @author aNNiMON
 */
public class ZipExportStrategy implements ExportStrategy {
	
	private static final String KEY_TITLE = "title";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_CHILDS = "childs";
	
	private static final String BRANCH_PREFIX = "branch_";
	private static final String NOTE_PREFIX = "note_";
	
	@Override
	public void export(File file, NoticeTreeItem notice) {
		try {
			File temporaryDir = Files.createTempDirectory("noticeditor").toFile();
			JSONObject index = new JSONObject();
			writeNoticesAndFillIndex(temporaryDir, notice, index);
			IOUtil.writeJson(new File(temporaryDir, "index.json"), index);
			IOUtil.pack(temporaryDir, file.getPath());
			IOUtil.removeDirectory(temporaryDir);
		} catch (IOException | JSONException e) {
			throw new ExportException(e);
		}
	}

	private void writeNoticesAndFillIndex(File dir, NoticeTreeItem item, JSONObject index) throws IOException, JSONException {
		final String title = item.getTitle();
		String filename = (item.isBranch() ? BRANCH_PREFIX : NOTE_PREFIX) + IOUtil.sanitizeFilename(title);
		
		File newDir = new File(dir, filename);
		if (newDir.exists()) {
			// solve collision
			int counter = 1;
			String newFileName = filename;
			while (newDir.exists()) {
				newFileName = String.format("%s_(%d)", filename, counter++);
				newDir = new File(dir, newFileName);
			}
			filename = newFileName;
		}
		newDir.mkdir();
		
		index.put(KEY_TITLE, title);
		index.put(KEY_FILENAME, filename);
		
		if (item.isBranch()) {
			// ../branch_filename
			ArrayList list = new ArrayList();
			for (Object object : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) object;
				
				JSONObject indexEntry = new JSONObject();
				writeNoticesAndFillIndex(newDir, child, indexEntry);
				list.add(indexEntry);
			}
			index.put(KEY_CHILDS, new JSONArray(list));
		} else {
			// ../note_filename/filename.md
			File mdFile = new File(newDir, filename + ".md");
			IOUtil.writeContent(mdFile, item.getContent());
		}
	}
	
}

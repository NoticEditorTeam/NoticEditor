package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
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
	
	private Set<String> paths;
	private ZipFile zip;
	private ZipParameters parameters;
	
	@Override
	public void export(File file, NoticeTreeItem notice) {
		paths = new HashSet<>();
		try {
			zip = new ZipFile(file);
			parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setSourceExternalStream(true);
			
			JSONObject index = new JSONObject();
			writeNoticesAndFillIndex("", notice, index);
			storeFile("index.json", index.toString());
		} catch (ZipException | IOException | JSONException e) {
			throw new ExportException(e);
		}
	}
	
	private void storeFile(String path, String content) throws IOException, ZipException {
		parameters.setFileNameInZip(path);
		try (InputStream stream = IOUtil.toStream(content)) {
			zip.addStream(stream, parameters);
		}
	}

	private void writeNoticesAndFillIndex(String dir, NoticeTreeItem item, JSONObject index) throws IOException, JSONException, ZipException {
		final String title = item.getTitle();
		String filename = (item.isBranch() ? BRANCH_PREFIX : NOTE_PREFIX) + IOUtil.sanitizeFilename(title);
		
		String newDir = dir + filename;
		if (paths.contains(newDir)) {
			// solve collision
			int counter = 1;
			String newFileName = filename;
			while (paths.contains(newDir)) {
				newFileName = String.format("%s_(%d)", filename, counter++);
				newDir = dir + newFileName;
			}
			filename = newFileName;
		}
		paths.add(newDir);
		
		index.put(KEY_TITLE, title);
		index.put(KEY_FILENAME, filename);
		
		if (item.isBranch()) {
			// ../branch_filename
			ArrayList list = new ArrayList();
			for (Object object : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) object;
				
				JSONObject indexEntry = new JSONObject();
				writeNoticesAndFillIndex(newDir + "/", child, indexEntry);
				list.add(indexEntry);
			}
			index.put(KEY_CHILDS, new JSONArray(list));
		} else {
			// ../note_filename/filename.md
			storeFile(newDir + filename + ".md", item.getContent());
		}
	}
	
}

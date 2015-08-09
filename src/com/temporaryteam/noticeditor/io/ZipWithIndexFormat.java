package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.TreeItem;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Document format that stores to zip archive with index.json.
 * @author aNNiMON
 */
public class ZipWithIndexFormat {
	
	private static final String INDEX_JSON = "index.json";
	
	private static final String KEY_TITLE = "title";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_CHILDS = "childs";
	
	private static final String BRANCH_PREFIX = "branch_";
	private static final String NOTE_PREFIX = "note_";
	
	public static ZipWithIndexFormat with(File file) throws ZipException {
		return new ZipWithIndexFormat(file);
	}
	
	private final Set<String> paths;
	private final ZipFile zip;
	private final ZipParameters parameters;
	
	private ZipWithIndexFormat(File file) throws ZipException {
		paths = new HashSet<>();
		zip = new ZipFile(file);
		parameters = new ZipParameters();
	}
	
	public NoticeTree importDocument() throws IOException, JSONException, ZipException {
		String indexContent = readFile(INDEX_JSON);
		if (indexContent == null || indexContent.isEmpty()) {
			throw new IOException("Invalid file format");
		}
		
		JSONObject index = new JSONObject(indexContent);
		return new NoticeTree(readNotices("", index));
	}
	
	private String readFile(String path) throws IOException, ZipException {
		FileHeader header = zip.getFileHeader(path);
		if (header == null) return "";
		return IOUtil.stringFromStream(zip.getInputStream(header));
	}
	
	private NoticeTreeItem readNotices(String dir, JSONObject index) throws IOException, JSONException, ZipException {
		final String title = index.getString(KEY_TITLE);
		final String filename = index.getString(KEY_FILENAME);
		final String dirPrefix = index.has(KEY_CHILDS) ? BRANCH_PREFIX : NOTE_PREFIX;
		
		final String newDir = dir + dirPrefix + filename + "/";
		if (index.has(KEY_CHILDS)) {
			JSONArray childs = index.getJSONArray(KEY_CHILDS);
			NoticeTreeItem branch = new NoticeTreeItem(title);
			for (int i = 0; i < childs.length(); i++) {
				branch.addChild( readNotices(newDir, childs.getJSONObject(i)) );
			}
			return branch;
		} else {
			// ../note_filename/filename.md
			final String mdPath = newDir + filename + ".md";
			return new NoticeTreeItem(title, readFile(mdPath));
		}
	}

	public void export(NoticeTreeItem notice) throws IOException, JSONException, ZipException {
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setSourceExternalStream(true);

		JSONObject index = new JSONObject();
		writeNoticesAndFillIndex("", notice, index);
		storeFile(INDEX_JSON, index.toString());
	}

	public void export(NoticeTree tree) throws IOException, JSONException, ZipException {
		export((NoticeTreeItem)tree.getRoot());
	}
	
	private void storeFile(String path, String content) throws IOException, ZipException {
		parameters.setFileNameInZip(path);
		try (InputStream stream = IOUtil.toStream(content)) {
			zip.addStream(stream, parameters);
		}
	}

	private void writeNoticesAndFillIndex(String dir, NoticeTreeItem item, JSONObject index) throws IOException, JSONException, ZipException {
		final String title = item.getTitle();
		final String dirPrefix = item.isBranch() ? BRANCH_PREFIX : NOTE_PREFIX;
		String filename = IOUtil.sanitizeFilename(title);
		
		String newDir = dir + dirPrefix + filename;
		if (paths.contains(newDir)) {
			// solve collision
			int counter = 1;
			String newFileName = filename;
			while (paths.contains(newDir)) {
				newFileName = String.format("%s_(%d)", filename, counter++);
				newDir = dir + dirPrefix + newFileName;
			}
			filename = newFileName;
		}
		paths.add(newDir);
		
		index.put(KEY_TITLE, title);
		index.put(KEY_FILENAME, filename);
		
		if (item.isBranch()) {
			// ../branch_filename
			ArrayList list = new ArrayList();
			for (TreeItem<String> object : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) object;
				
				JSONObject indexEntry = new JSONObject();
				writeNoticesAndFillIndex(newDir + "/", child, indexEntry);
				list.add(indexEntry);
			}
			index.put(KEY_CHILDS, new JSONArray(list));
		} else {
			// ../note_filename/filename.md
			storeFile(newDir + "/" + filename + ".md", item.getContent());
		}
	}
}

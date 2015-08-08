package com.temporaryteam.noticeditor.io;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

public final class IOUtil {
	
	private static final int FILENAME_LIMIT = 60;
	private static final String NEW_LINE = System.lineSeparator();

	public static String readContent(File file) throws IOException {
		final StringBuilder result = new StringBuilder();
		try (InputStream is = new FileInputStream(file);
				Reader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			result.append(reader.readLine()).append(NEW_LINE);
		}
		return result.toString();
	}
	
	public static void writeContent(File file, String content) throws IOException {
		try (OutputStream os = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(os, "UTF-8")) {
			writer.write(content);
		}
	}
	
	public static void writeJson(File file, JSONObject json) throws IOException, JSONException {
		try (OutputStream os = new FileOutputStream(file);
				Writer writer = new OutputStreamWriter(os, "UTF-8")) {
			json.write(writer);
		}
	}
	
	public static void removeDirectory(File directory) {
		if (directory.isFile() || !directory.exists()) return;
		removeDirectoryHelper(directory);
	}
	
	private static void removeDirectoryHelper(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				removeDirectoryHelper(f);
			}
		}
		file.delete();
	}
	
	public static String sanitizeFilename(String name) {
		if (name == null || name.isEmpty()) return "empty";
		
		// Convert non-ascii chars to char code
		String newName = name;
		try {
			newName = URLEncoder.encode(newName, "UTF-8");
		} catch (UnsupportedEncodingException ex) { }
		// Allow only english chars, numbers and some specific symbols
		newName = newName.toLowerCase().replaceAll("[^a-z0-9._\\(\\)]", "_");
		// Limit filename length
		if (newName.length() > FILENAME_LIMIT) {
			newName = newName.substring(0, FILENAME_LIMIT);
		}
		return newName;
	}
	
	public static InputStream toStream(String content) throws IOException {
		return toStream(content, "UTF-8");
	}
	
	public static InputStream toStream(String content, String charset) throws IOException {
		return new ByteArrayInputStream(content.getBytes(charset));
	}
	
	/** 
	 * Pack directory
	 */
	public static void pack(File directory, String toSave) throws IOException {
		final URI root = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		
		try (OutputStream out = new FileOutputStream(new File(toSave));
				ZipOutputStream zout = new ZipOutputStream(out)) {
			
			while(!queue.isEmpty()) {
				directory = queue.pop();
				for(File child : directory.listFiles()) {
					String name = root.relativize(child.toURI()).getPath();
					if (child.isDirectory()) {
						queue.push(child);
						name = name.endsWith("/") ? name : (name + "/");
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						try (InputStream in = new FileInputStream(child)) {
							byte[] buffer = new byte[1024];
							while (true) {
								int readCount = in.read(buffer);
								if (readCount < 0) break;
								zout.write(buffer, 0, readCount);
							}
						}
						zout.closeEntry();
					}
				}
			}
		}
	}
	
}

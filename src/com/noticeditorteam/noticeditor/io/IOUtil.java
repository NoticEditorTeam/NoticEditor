package com.noticeditorteam.noticeditor.io;

import gcardone.junidecode.Junidecode;
import java.io.*;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;

public final class IOUtil {

    private static final int FILENAME_LIMIT = 60;

    public static String readContent(File file) throws IOException {
        return stringFromStream(new FileInputStream(file));
    }

    public static void writeContent(File file, String content) throws IOException {
        try (OutputStream os = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(os, "UTF-8")) {
            writer.write(content);
        }
    }

    public static void writeContent(File file, byte[] content) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(content);
        }
    }

    public static void writeJson(File file, JSONObject json) throws IOException, JSONException {
        try (OutputStream os = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(os, "UTF-8")) {
            json.write(writer);
        }
    }

    public static void removeDirectory(File directory) {
        if (directory.isFile() || !directory.exists())
            return;
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
        if (name == null || name.isEmpty())
            return "empty";

        String newName = name;
        // Quick transliteration
        newName = Junidecode.unidecode(newName);
        // Convert non-ascii chars to char code
        try {
            newName = URLEncoder.encode(newName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
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

    public static String stringFromStream(InputStream stream) throws IOException {
        return stringFromStream(stream, "UTF-8");
    }

    public static String stringFromStream(InputStream stream, String charset) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        copy(stream, result);
        stream.close();
        return result.toString("UTF-8");
    }

    public static boolean isResourceExists(String resource) {
        try (InputStream is = IOUtil.class.getResourceAsStream(resource)) {
            return is != null;
        } catch (IOException ioe) {
            return false;
        }
    }

    public static List<String> linesFromResource(String resource) {
        return linesFromResource(resource, "UTF-8");
    }

    public static List<String> linesFromResource(String resource, String charset) {
        try (InputStream is = IOUtil.class.getResourceAsStream(resource)) {
            return linesFromStream(is, charset);
        } catch (IOException ioe) {
            return Collections.emptyList();
        }
    }

    public static List<String> linesFromStream(InputStream stream) throws IOException {
        return linesFromStream(stream, "UTF-8");
    }

    public static List<String> linesFromStream(InputStream stream, String charset) throws IOException {
        if (stream == null) return Collections.emptyList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        final int bufferSize = 8192;
        final byte[] buffer = new byte[bufferSize];
        int readed;
        while ((readed = is.read(buffer, 0, bufferSize)) != -1) {
            os.write(buffer, 0, readed);
        }
    }
}

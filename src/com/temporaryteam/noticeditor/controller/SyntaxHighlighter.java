package com.temporaryteam.noticeditor.controller;

import com.temporaryteam.noticeditor.io.IOUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * @author aNNiMON
 */
public class SyntaxHighlighter {

    private static final File DIRECTORY = new File(System.getProperty("user.home"), ".noticeditor");
    private static final Pattern PATTERN_CODE = Pattern.compile("<code class=\"(\\w+)\">");

    public void unpackHighlightJs() {
        if (DIRECTORY.exists()) return;

        DIRECTORY.mkdir();
        new Thread(unpackHighlightJs).start();
    }

    /**
     * Adds html header with css and highlight.js library.
     *
     * @param content the html content
     * @return full html page
     */
    public String highlight(String content) {
        final Set<String> languages = getUsedLanguages(content);
        if (languages == null || languages.isEmpty()) return content;

        final String path = "file://" + DIRECTORY.toURI().getPath();
        final String lang = path + "languages/";

        final StringBuilder sb = new StringBuilder();
        sb.append("<html>\n<head>\n");
        // TODO: coloring style preferences
        sb.append("<link type=\"text/css\" href=\"").append(path).append("styles/vs.css\" rel=\"stylesheet\" />\n");
        sb.append("<script src=\"").append(path).append("highlight.js\"></script>\n");
        for (String language : languages) {
            sb.append("<script src=\"").append(lang).append(language).append(".js\"></script>\n");
        }
        sb.append("<script>hljs.initHighlightingOnLoad();</script>\n");
        sb.append("</head>\n<body>\n");
        sb.append(content);
        sb.append("</body>\n</html>");
        return sb.toString();
    }

    private Set<String> getUsedLanguages(String content) {
        Matcher matcher = PATTERN_CODE.matcher(content);
        if (!matcher.find()) return null;

        Set<String> languages = new HashSet<>();
        do {
            languages.add(matcher.group(1).toLowerCase());
        } while (matcher.find());
        return languages;
    }

    private final Runnable unpackHighlightJs = new Runnable() {

        @Override
        public void run() {
            final File zipFile = new File(DIRECTORY, "highlightjs.zip");
            try {
                copyZipTo(zipFile);
                extractZip(zipFile);
            } catch (IOException | ZipException ex) {
            } finally {
                zipFile.delete();
            }
        }

        private void copyZipTo(File destFile) throws IOException {
            try (InputStream is = getClass().getResourceAsStream("/resources/highlightjs.zip");
                 OutputStream os = new FileOutputStream(destFile)) {
                IOUtil.copy(is, os);
                os.flush();
            }
        }

        private void extractZip(File zipFile) throws ZipException {
            ZipFile zip = new ZipFile(zipFile);
            zip.extractAll(DIRECTORY.getAbsolutePath());
        }
    };
}

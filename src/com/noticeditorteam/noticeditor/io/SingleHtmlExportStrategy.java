package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.exceptions.ExportException;
import com.noticeditorteam.noticeditor.model.*;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TreeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class SingleHtmlExportStrategy implements ExportStrategy {

    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("@att\\:([a-zA-Z0-9._\\(\\)]+)");

    private Parser mdParser;
    private HtmlRenderer htmlRenderer;

    public void setMarkdownParser(Parser parser) {
        this.mdParser = parser;
    }

    public void setHtmlRenderer(HtmlRenderer renderer) {
        this.htmlRenderer = renderer;
    }

    @Override
    public boolean export(File destDir, NoticeTree tree) throws ExportException {
        try {
            exportToHtmlPage(destDir, tree, "index");
            return true;
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private void exportToHtmlPage(File destDir, NoticeTree notice, String filename) throws IOException {
        Document doc = Jsoup.parse(getClass().getResourceAsStream("/resources/single_html_export_template.html"),
                null, "");
        doc.title("NoticEditor");
        doc.select("#notice_title").first().text("NoticEditor");
        generateContents(doc, notice);
        generateContent(doc, notice);
        File file = new File(destDir, filename + ".html");
        IOUtil.writeContent(file, doc.outerHtml());
        for(TreeItem<NoticeItem> child : notice.getRoot().getInternalChildren()) {
            exportAttachments(destDir, (NoticeTreeItem) child, "");
        }
    }

    private void exportAttachments(File destDir, NoticeTreeItem root, String path) throws IOException {
        String noticeTitle = IOUtil.sanitizeFilename(root.getTitle());
        if(root.isBranch()) {
            for(TreeItem<NoticeItem> notice : root.getInternalChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) notice;
                exportAttachments(destDir, child, path + "." + noticeTitle);
            }
        }
        else {
            for(Attachment attachment : root.getAttachments()) {
                String filename = path + "." + noticeTitle;
                filename = filename.replaceAll("\\.", "-");
                filename += "-";
                filename += attachment.getName();
                filename = filename.substring(1);
                File file = new File(destDir, filename);
                IOUtil.writeContent(file, attachment.getData());
            }
        }
    }

    private void generateContents(Document doc, NoticeTree tree) {
        Element data = doc.select("#content").first();
        Element HTMLRoot = doc.createElement("div").attr("id", "header");
        HTMLRoot.append("<h1>Contents: </h1>");
        Element list = doc.createElement("ul");
        for(TreeItem<NoticeItem> notice : tree.getRoot().getInternalChildren()) {
            NoticeTreeItem child = (NoticeTreeItem) notice;
            list.appendChild(generateContents(doc, child, ""));
        }
        HTMLRoot.appendChild(list);
        data.appendChild(HTMLRoot);
    }

    private Node generateContents(Document doc, NoticeTreeItem root, String path) {
        Element item = doc.createElement("li");
        String pathPart = IOUtil.sanitizeFilename(root.getTitle());
        item.appendElement("a")
                .attr("href", "#" + path + "." + pathPart)
                .text(root.getTitle());
        if(root.isBranch()) {
            Element list = doc.createElement("ul");
            for(TreeItem<NoticeItem> notice : root.getInternalChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) notice;
                list.appendChild(generateContents(doc, child, path + "." + pathPart));
            }
            item.appendChild(list);
        }
        return item;
    }

    private void generateContent(Document doc, NoticeTree notice) {
        Element data = doc.select("#content").first();
        Element HTMLRoot = doc.createElement("div").attr("id", "contents");
        for(TreeItem<NoticeItem> item : notice.getRoot().getInternalChildren()) {
            NoticeTreeItem child = (NoticeTreeItem) item;
            HTMLRoot.appendChild(generateContent(doc, child, ""));
        }
        data.appendChild(HTMLRoot);
    }

    private Node generateContent(Document doc, NoticeTreeItem root, String path) {
        String noticeName = IOUtil.sanitizeFilename(root.getTitle());
        Element data = doc.createElement("div").attr("id", path + "." + noticeName);
        data.appendElement("h2").text(root.getTitle());
        if(root.isBranch()) {
            for(TreeItem<NoticeItem> item : root.getInternalChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) item;
                data.appendChild(generateContent(doc, child, path + "." + noticeName));
            }
        } else {
            final var flexmarkDoc = mdParser.parse(root.getContent());
            final String html = htmlRenderer.render(flexmarkDoc);
            data.append(processAttachments(html, path + "." + noticeName, root.getAttachments()));
        }
        return data;
    }

    private String processAttachments(String content, String path, Attachments attachments) {
        final Matcher matcher = ATTACHMENT_PATTERN.matcher(content);
        String filename = path.replaceAll("\\.", "-");
        filename = filename.substring(1);
        String newContent = matcher.replaceAll("<img src=\"" + filename + "-$1\" />");
        StringBuilder sb = new StringBuilder(newContent);
        for(Attachment attachment : attachments) {
            if(!attachment.isImage()) {
                sb.append("<a href=\"").append(filename).append('-').append(attachment.getName()).append("\" >")
                        .append(attachment.getName()).append("</a>");
            }
        }
        return sb.toString();
    }
}

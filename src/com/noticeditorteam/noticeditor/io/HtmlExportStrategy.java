package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.model.NoticeItem;
import com.noticeditorteam.noticeditor.model.NoticeTree;
import com.noticeditorteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pegdown.PegDownProcessor;

/**
 * Export notices to html.
 *
 * @author aNNiMON
 */
public class HtmlExportStrategy implements ExportStrategy {

    private PegDownProcessor processor;
    private Map<NoticeTreeItem, String> filenames;

    public void setProcessor(PegDownProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void export(File destDir, NoticeTree notice) {
        filenames = new HashMap<>();
        try {
            exportToHtmlPages(notice, destDir, "index");
        } catch (IOException ioe) {
            throw new ExportException(ioe);
        }
    }

    /**
     * Save item as HTML pages. Root item will be saved to index.html
     *
     * @param item node to recursively save
     * @param dir directory to save
     * @param filename name of the file without extension
     */
    private void exportToHtmlPages(NoticeTreeItem item, File dir, String filename) throws IOException {
        Document doc = Jsoup.parse(getClass().getResourceAsStream("/resources/export_template.html"), null, "");
        generatePage(item, doc);
        File file = new File(dir, filename + ".html");
        IOUtil.writeContent(file, doc.outerHtml());
        if (item.isBranch()) {
            for (TreeItem<NoticeItem> obj : item.getInternalChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) obj;
                exportToHtmlPages(child, dir, generateFilename(child));
            }
        }
    }

    /**
     * Save tree to HTML pages. Root item saved to index.html
     *
     * @param tree tree to save
     * @param dir directory to save
     * @param filename name of the file without extension
     */
    private void exportToHtmlPages(NoticeTree tree, File dir, String filename) throws IOException {
        exportToHtmlPages((NoticeTreeItem) tree.getRoot(), dir, filename);
    }

    private void generatePage(NoticeTreeItem note, Document doc) {
        doc.title(note.getTitle());
        doc.select("#notice_title").first().text(note.getTitle());
        Element data = doc.select("#content").first();
        if (note.isBranch()) {
            Element list = doc.createElement("div").addClass("list-group");
            for (TreeItem<NoticeItem> treeItem : note.getInternalChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) treeItem;
                Element item = doc.createElement("div").addClass("list-group-item");
                generateIcon(child, item);
                item.appendElement("a").attr("href", generateFilename(child) + ".html")
                        .text(child.getTitle())
                        .appendElement("br");
                list.appendChild(item);
            }
            data.appendChild(list);
        } else {
            data.html(processor.markdownToHtml(note.getContent()));
        }
    }

    private void generateIcon(NoticeTreeItem child, Element item) {
        if (child.isBranch()) {
            item.appendElement("span").addClass("glyphicon glyphicon-folder-open");
        } else {
            switch (child.getStatus()) {
                case NoticeItem.STATUS_IMPORTANT:
                    item.appendElement("span").addClass("glyphicon glyphicon-pushpin important");
                    break;
                default:
                    item.appendElement("span").addClass("glyphicon glyphicon-pushpin normal");
            }
        }
    }

    private String generateFilename(NoticeTreeItem item) {
        if (filenames.containsKey(item)) {
            return filenames.get(item);
        }

        String filename = IOUtil.sanitizeFilename(item.getTitle());
        if (filenames.containsValue(filename)) {
            // solve collision
            int counter = 1;
            String newFileName = filename;
            while (filenames.containsValue(newFileName)) {
                newFileName = String.format("%s_(%d)", filename, counter++);
            }
            filename = newFileName;
        }
        filenames.put(item, filename);
        return filename;
    }
}

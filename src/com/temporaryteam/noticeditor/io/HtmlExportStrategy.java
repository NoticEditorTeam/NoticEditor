package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javafx.scene.control.TreeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pegdown.PegDownProcessor;

/**
 * Export notices to html.
 * @author aNNiMON
 */
public class HtmlExportStrategy implements ExportStrategy {
	
	private PegDownProcessor processor;
	
	public void setProcessor(PegDownProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void export(File destDir, NoticeTree notice) {
		try {
			exportToHtmlPages(notice, destDir, "index");
		} catch (IOException ioe) {
			throw new ExportException(ioe);
		}
	}

	/**
	 * Save item as HTML pages. Root item was saved to index.html
	 *
	 * @param item node to recursively save
	 * @param dir directory to save
	 * @param filename name of the file without extension
	 */
	private void exportToHtmlPages(NoticeTreeItem item, File dir, String filename) throws IOException {
		Document doc = Jsoup.parse(getClass().getResourceAsStream("/resources/export_template.html"), null, "");
		
		File file = new File(dir, filename + ".html");
		if (file.exists()) {
			// solve collision
			int counter = 1;
			String newFileName = filename;
			while (file.exists()) {
				newFileName = String.format("%s_(%d)", filename, counter++);
				file = new File(dir, newFileName + ".html");
			}
			filename = newFileName;
		}
		
		item.toHTML(processor, doc, filename + ".html");
		IOUtil.writeContent(file, doc.outerHtml());
		if (item.isBranch()) {
			for (TreeItem<String> obj : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) obj;
				exportToHtmlPages(child, dir, IOUtil.sanitizeFilename(child.getTitle()));
			}
		}
	}

	/**
	 * Save tree to HTML pages. Root item is saving to index.html
	 *
	 * @param tree tree to save
	 * @param dir directory to save
	 * @param filename name of the file
	 */
	private void exportToHtmlPages(NoticeTree tree, File dir, String filename) throws IOException {
		exportToHtmlPages((NoticeTreeItem)tree.getRoot(), dir, filename);
	}
}

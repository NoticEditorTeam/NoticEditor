package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
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
	public void export(File destDir, NoticeTreeItem notice) {
		File indexFile = new File(destDir, "index.html");
		try {
			exportToHtmlPages(notice, indexFile);
		} catch (IOException ioe) {
			throw new ExportException(ioe);
		}
	}

	/**
	 * Save item as HTML pages. Root item was saved to index.html
	 *
	 * @param item node to recursively save
	 * @param file file to save
	 */
	private void exportToHtmlPages(NoticeTreeItem<String> item, File file) throws IOException {
		IOUtil.writeContent(file, item.toHTML(processor));
		if (item.isBranch()) {
			for (Object obj : item.getChildren()) {
				NoticeTreeItem child = (NoticeTreeItem) obj;
				exportToHtmlPages(child, new File(file.getParent(), child.getId() + ".html"));
			}
		}
	}
}

package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Export notices to md files in zip archive.
 * @author aNNiMON
 */
public class ZipExportStrategy implements ExportStrategy {

	@Override
	public void export(File file, NoticeTreeItem notice) {
		try {
			File temporaryDir = Files.createTempDirectory("noticeditor").toFile();
			writeFSNode(notice, temporaryDir);
			IOUtil.pack(temporaryDir, file.getPath());
			IOUtil.removeDirectory(temporaryDir);
		} catch (IOException ioe) {
			throw new ExportException(ioe);
		}
	}

	/**
	 * Write node in filesystem
	 */
	private void writeFSNode(NoticeTreeItem item, File dir) throws IOException {
		String title = item.getTitle();
		if (item.isBranch()) {
			for (Object child : item.getChildren()) {
				File newDir = new File(dir.getPath() + "/" + title);
				if (newDir.exists()) newDir.delete();
				newDir.mkdir();
				writeFSNode((NoticeTreeItem) child, newDir);
			}
		} else {
			File toWrite = new File(dir.getPath() + "/" + title + ".md");
			IOUtil.writeContent(toWrite, item.getContent());
		}
	}
}

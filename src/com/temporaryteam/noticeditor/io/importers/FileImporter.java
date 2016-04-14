package com.temporaryteam.noticeditor.io.importers;

import com.temporaryteam.noticeditor.io.IOUtil;
import com.temporaryteam.noticeditor.model.NoticeTree;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;
import java.io.IOException;

public final class FileImporter {

	private static Tree treeImporter;
	private static Content contentImporter;

	public static Tree tree() {
		if (treeImporter == null) {
			treeImporter = new Tree();
		}
		return treeImporter;
	}

	public static Content content() {
		if (contentImporter == null) {
			contentImporter = new Content();
		}
		return contentImporter;
	}

	public static class Tree implements Importer<File, Void, NoticeTree> {

		@Override
		public void importFrom(File file, Void options, ImportCallback<NoticeTree, Exception> callback) {
			try {
				callback.call(importFrom(file), null);
			} catch (IOException ex) {
				callback.call(null, ex);
			}
		}

		public static NoticeTree importFrom(File file) throws IOException {
			final NoticeTreeItem root = new NoticeTreeItem("Root");
			final NoticeTree tree = new NoticeTree(root);
			tree.addItem(new NoticeTreeItem(file.getName(), IOUtil.readContent(file)), root);
			return tree;
		}
	}

	public static class Content implements Importer<File, Void, String> {
		
		@Override
		public void importFrom(File file, Void options, ImportCallback<String, Exception> callback) {
			try {
				callback.call(IOUtil.readContent(file), null);
			} catch (IOException ex) {
				callback.call(null, ex);
			}
		}
	}
}

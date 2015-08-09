package com.temporaryteam.noticeditor.view;

import com.temporaryteam.noticeditor.model.NoticeTree;

import javafx.scene.control.TreeView;

public class NoticeTreeView extends TreeView<String> {

	public NoticeTree dataTree;

	public NoticeTreeView() {
		super();
	}

	public NoticeTreeView(NoticeTree tree) {
		super(tree.getRoot());
		dataTree = tree;
	}

	public NoticeTree getDataTree() {
		return dataTree;
	}

	public void setDataTree(NoticeTree tree) {
		dataTree = tree;
		setRoot(dataTree.getRoot());
	}
}

package com.noticeditorteam.noticeditor.model;

public class NoticeTree {

	private final NoticeTreeItem root;

	public NoticeTree() {
		root = null;
	}

	/**
	 * Create NoticeTree with set root
	 *
	 * @param root set root
	 */
	public NoticeTree(NoticeTreeItem root) {
		this.root = root;
	}

	public NoticeTreeItem getRoot() {
		return root;
	}

	/**
	 * @param item to add
	 * @param parent if null, item will be added to root item.
	 */
	public void addItem(NoticeTreeItem item, NoticeTreeItem parent) {
		if (parent == null) {
			parent = root;
		} else if (parent.isLeaf()) {
			parent = (NoticeTreeItem) parent.getParent();
		}
		parent.getInternalChildren().add(item);
		parent.setExpanded(true);
	}

	public void removeItem(NoticeTreeItem item) {
		if (item == null)
			return;
		NoticeTreeItem parent = (NoticeTreeItem) item.getParent();
		if (parent == null)
			return;
		parent.getInternalChildren().remove(item);
	}
}

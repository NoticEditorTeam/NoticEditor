package com.temporaryteam.noticeditor.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.temporaryteam.noticeditor.model.NoticeItem;

public class NoticeTreeItem<T extends String> extends TreeItem {

	private boolean isFirstTimeChildren = true;
	private NoticeItem notice;

	public NoticeTreeItem(NoticeItem category) {
		super(category.getName());
		notice = category;
	}

	@Override
	public ObservableList<NoticeTreeItem> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;
			super.getChildren().setAll(buildChildrens(this));
		}
		return super.getChildren();
	}

	/**
	 * 
	 * @return true if content == null (its node has no childrens)
	 */
	@Override
	public boolean isLeaf() {
		return !notice.isBranch();
	}

	public NoticeItem getNotice() {
		return notice;
	}

	public void setNotice(NoticeItem notice) {
		this.notice = notice;
	}

	private ObservableList<NoticeTreeItem> buildChildrens(NoticeTreeItem noticeTreeItem) {
		NoticeItem noticeItem = noticeTreeItem.getNotice();
		if (noticeItem.isBranch()) {
			ObservableList<NoticeTreeItem> childrens = FXCollections.observableArrayList();
			for (NoticeItem child : noticeItem.childrens()) {
				childrens.add(new NoticeTreeItem(child));
			}
			return childrens;
		}
		return FXCollections.emptyObservableList();
	}
}

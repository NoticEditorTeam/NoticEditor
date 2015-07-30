package com.temporaryteam.noticeditor.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.temporaryteam.noticeditor.model.NoticeCategory;

public class NoticeTreeItem<T extends String> extends TreeItem {

	private boolean isLeaf;
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;
	private NoticeCategory notice;

	public NoticeTreeItem(NoticeCategory category) {
		super(category.getName());
		notice = category;
	}

	@Override
	public ObservableList<NoticeTreeItem> getChildren() {
		if(isFirstTimeChildren) {
			isFirstTimeChildren = false;
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if(isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			NoticeCategory category = getNotice();
			isLeaf = ((category.getSubCategories())!=null);
		}
		return isLeaf;
	}

	public NoticeCategory getNotice() {
		return notice;
	}

	public void setNotice(NoticeCategory notice) {
		this.notice = notice;
	}

	private ObservableList<NoticeTreeItem> buildChildren(NoticeTreeItem noticeItem) {
		NoticeCategory category = noticeItem.getNotice();
		if((category!=null)&&(((category.getSubCategories())!=null))) {
			NoticeCategory[] categories = category.getSubCategories();
			if(categories!=null) {
				ObservableList<NoticeTreeItem> children = FXCollections.observableArrayList();
				for(NoticeCategory cat : categories) {
					children.add(new NoticeTreeItem(cat));
				}
				return children;
			}
		}
		return FXCollections.emptyObservableList();
	}
}

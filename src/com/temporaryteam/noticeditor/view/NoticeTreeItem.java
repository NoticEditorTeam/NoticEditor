package com.temporaryteam.noticeditor.view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.temporaryteam.noticeditor.model.NoticeCategory;

public class NoticeTreeItem<T extends String> extends TreeItem {

	private boolean isFirstTimeChildren = true;
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
		return ((notice.getSubCategories())==null);
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
			ArrayList<NoticeCategory> categories = category.getSubCategories();
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

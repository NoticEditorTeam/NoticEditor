package com.noticeditorteam.noticeditor.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @author naik, setser, annimon, kalter
 */
public class NoticeItem {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_IMPORTANT = 1;

    private String title;
    private ObservableList<NoticeItem> children;
    private String content;
    private int status;
    private Attachments attachments;

    /**
     * Create branch node on tree.
     *
     * @param title notice title
     */
    public NoticeItem(String title) {
        this(title, null, 0);
    }

    /**
     * Create leaf node on tree.
     *
     * @param title notice title
     * @param content notice content
     */
    public NoticeItem(String title, String content) {
        this(title, content, STATUS_NORMAL);
    }

    /**
     * Create leaf node on tree.
     *
     * @param title   notice title
     * @param content notice content
     * @param status  is notice important
     */
    public NoticeItem(String title, String content, int status) {
        this.title = title;
        this.content = content;
        this.status = status;
        attachments = new Attachments();
        children = FXCollections.observableArrayList();
    }

    public void addChild(NoticeItem item) {
        children.add(item);
    }

    public boolean isLeaf() {
        return content != null;
    }

    /**
     * @return true if content == null
     */
    public boolean isBranch() {
        return content == null;
    }

    /**
     * @return notice content or null if its a branch
     */
    public String getContent() {
        return content;
    }

    /**
     * Content will be changed only when is a leaf node.
     *
     * @param content new notice content
     */
    public void changeContent(String content) {
        if (isLeaf()) {
            this.content = content;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return title + "\n\n" + content;
    }
}

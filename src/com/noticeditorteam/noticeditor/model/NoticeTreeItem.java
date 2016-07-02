package com.noticeditorteam.noticeditor.model;

import com.noticeditorteam.noticeditor.io.IOUtil;
import java.io.File;
import java.nio.file.Files;

/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @author naik, setser, annimon, kalter
 */
public class NoticeTreeItem extends FilterableTreeItem<NoticeItem> {

    /**
     * Create branch node on tree.
     *
     * @param title a title of notice
     */
    public NoticeTreeItem(String title) {
        this(title, null, 0);
    }

    /**
     * Create leaf node on tree.
     *
     * @param title   a title of notice
     * @param content a content of notice
     */
    public NoticeTreeItem(String title, String content) {
        this(title, content, NoticeItem.STATUS_NORMAL);
    }

    /**
     * Create leaf node on tree.
     *
     * @param title   a title of notice
     * @param content a content of notice
     * @param status  a status of notice
     */
    public NoticeTreeItem(String title, String content, int status) {
        super(new NoticeItem(title, content, status));
    }

    public void addChild(NoticeTreeItem item) {
        getInternalChildren().add(item);
        if (item != null) {
            getValue().addChild(item.getValue());
        } else {
            getValue().addChild(null);
        }
    }

    @Override
    public boolean isLeaf() {
        return getValue().isLeaf();
    }

    /**
     * @return true if content == null
     */
    public boolean isBranch() {
        return getValue().isBranch();
    }

    /**
     * @return notice content or null if its a branch
     */
    public String getContent() {
        return getValue().getContent();
    }

    /**
     * Content will be changed only when is a leaf node.
     *
     * @param content new content
     */
    public void changeContent(String content) {
        getValue().changeContent(content);
        fireChangeItem();
    }

    public String getTitle() {
        return getValue().getTitle();
    }

    public void setTitle(String title) {
        getValue().setTitle(title);
        fireChangeItem();
    }

    public int getStatus() {
        return getValue().getStatus();
    }

    public void setStatus(int status) {
        getValue().setStatus(status);
        fireChangeItem();
    }

    public void addAttachement(File file) {
        try {
            final byte[] content = Files.readAllBytes(file.toPath());
            final String name = IOUtil.sanitizeFilename(file.getName());
            final Attachment attachment = new Attachment(name, content);
            if (attachment.isImage()) {
                // TODO place attach code at the cursor place
                // and replace newline chars with space
                String newcontent = getContent() + "\n@att:" + name + "\n";
                changeContent(newcontent);
            }
            getValue().getAttachments().add(attachment);
        } catch (Exception e) {
            // TODO logger
            e.printStackTrace();
        }
    }

    public Attachments getAttachments() {
        return getValue().getAttachments();
    }

    public void setAttachments(Attachments attachments) {
        getValue().setAttachments(attachments);
        fireChangeItem();
    }
}

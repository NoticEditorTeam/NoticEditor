package com.noticeditorteam.noticeditor.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class NoticeTreeTest {

    @Test(expected = NullPointerException.class)
    public void testEmpty() {
        NoticeTree tree = new NoticeTree();
        assertNull(tree.getRoot());
        tree.addItem(null, tree.getRoot());
    }

    @Test
    public void testTree() {
        NoticeTreeItem root = new NoticeTreeItem("branch");
        NoticeTree tree = new NoticeTree(root);

        assertEquals(root, tree.getRoot());

        NoticeTreeItem leaf = new NoticeTreeItem("leaf", "content");
        tree.addItem(leaf, root);
        tree.addItem(new NoticeTreeItem("branch"), null);
        assertEquals(2, tree.getRoot().getInternalChildren().size());

        tree.removeItem(leaf);
        assertEquals(1, tree.getRoot().getInternalChildren().size());
    }

    @Test
    public void testRemoveRoot() {
        NoticeTreeItem root = new NoticeTreeItem("branch");
        NoticeTree tree = new NoticeTree(root);
        tree.removeItem(root);
    }
}

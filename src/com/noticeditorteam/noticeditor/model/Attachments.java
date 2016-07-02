package com.noticeditorteam.noticeditor.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Attachments implements Iterable<Attachment> {

    private final List<Attachment> attachments;

    public Attachments() {
        attachments = new ArrayList<>();
    }

    public int size() {
        return attachments.size();
    }

    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    public boolean add(Attachment e) {
        return attachments.add(e);
    }

    public boolean remove(Attachment o) {
        return attachments.remove(o);
    }

    public void clear() {
        attachments.clear();
    }

    public Attachment get(int index) {
        return attachments.get(index);
    }

    public Attachment remove(int index) {
        return attachments.remove(index);
    }

    @Override
    public Iterator<Attachment> iterator() {
        return attachments.iterator();
    }
}

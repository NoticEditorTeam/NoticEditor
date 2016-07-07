package com.noticeditorteam.noticeditor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Attachments implements Iterable<Attachment> {

    private final Map<String, Attachment> attachments;

    public Attachments() {
        attachments = new HashMap<>();
    }

    public Attachments(Map<String, Attachment> map) {
        attachments = new HashMap<>(map);
    }

    public Attachments(List<Attachment> list) {
        attachments = new HashMap<>(list.size());
        for (Attachment attachment : list) {
            attachments.put(attachment.getName(), attachment);
        }
    }

    public Attachments(Attachments other) {
        this(other.attachments);
    }

    public int size() {
        return attachments.size();
    }

    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    public void add(Attachment e) {
        attachments.put(e.getName(), e);
    }

    public void remove(Attachment att) {
        attachments.remove(att.getName());
    }

    public void clear() {
        attachments.clear();
    }

    public Attachment get(String name) {
        return attachments.get(name);
    }

    public Attachment getOrEmpty(String name) {
        return attachments.getOrDefault(name, Attachment.EMPTY);
    }

    public boolean contains(String key) {
        return attachments.containsKey(key);
    }

    @Override
    public Iterator<Attachment> iterator() {
        return attachments.values().iterator();
    }
}

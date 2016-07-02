package com.noticeditorteam.noticeditor.model;

import java.util.Base64;
import java.util.Locale;

public class Attachment {

    private final String name;
    private final byte[] data;
    private final boolean isImage;

    public Attachment(String name, byte[] data) {
        this.name = name;
        this.data = data;
        final String nameLowerCase = name.toLowerCase(Locale.ENGLISH);
        isImage = nameLowerCase.endsWith(".jpg") ||
                nameLowerCase.endsWith(".gif") ||
                nameLowerCase.endsWith(".png");
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsBase64() {
        return Base64.getEncoder().encodeToString(data);
    }

    public boolean isImage() {
        return isImage;
    }

    @Override
    public String toString() {
        return "Attachment: " + name;
    }
}

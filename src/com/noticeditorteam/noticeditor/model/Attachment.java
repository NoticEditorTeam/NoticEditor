package com.noticeditorteam.noticeditor.model;

import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

public class Attachment {

    public static final Attachment EMPTY = new Attachment("", new byte[0]);

    public static final String PREFIX = "@att:";
    public static final Pattern PATTERN = Pattern.compile(PREFIX + "([a-zA-Z0-9._\\(\\)]+)");

    private final String name;
    private final byte[] data;
    private final boolean isImage;
    private final String base64data;

    public Attachment(String name, byte[] data) {
        this.name = name;
        this.data = data;
        final String nameLowerCase = name.toLowerCase(Locale.ENGLISH);
        isImage = nameLowerCase.endsWith(".jpg") ||
                nameLowerCase.endsWith(".gif") ||
                nameLowerCase.endsWith(".png");
        base64data = isImage ? toBase64(data) : "";
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsBase64() {
        if (!base64data.isEmpty()) return base64data;
        return toBase64(data);
    }

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public boolean isImage() {
        return isImage;
    }

    @Override
    public String toString() {
        return name;
    }
}

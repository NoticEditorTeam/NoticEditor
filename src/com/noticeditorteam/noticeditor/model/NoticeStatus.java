package com.noticeditorteam.noticeditor.model;

/**
 * Representation of notice status
 *
 * @author Maximillian M.
 */
public class NoticeStatus {
    private String name;
    private int code;

    /**
     * Instantiates notice status
     *
     * @param name Status name
     * @param code Status code
     */
    public NoticeStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    /**
     * @return Status code
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return Status name
     */
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}

package com.noticeditorteam.noticeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Preferences helper.
 *
 * @author aNNiMON
 */
public final class Prefs {

    private static final Preferences user = Preferences.userRoot();

    public static String getLastDirectory() {
        return user.get("last_directory", System.getProperty("user.home"));
    }

    public static void setLastDirectory(String path) {
        user.put("last_directory", path);
    }

    public static List<String> getRecentFiles() {
        final List<String> files = new ArrayList<>(10);
        final Preferences recent = user.node("recent_files");
        final int count = recent.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            files.add(recent.get(String.valueOf(i), ""));
        }
        return files;
    }

    public static void addToRecentFiles(String path) {
        final Preferences recent = user.node("recent_files");
        final int count = Math.min(9, recent.getInt("count", 0));
        // Shift values
        for (int i = count - 1; i >= 0; i--) {
            recent.put(String.valueOf(i + 1), recent.get(String.valueOf(i), ""));
        }
        recent.put("0", path);
        recent.putInt("count", count + 1);
    }
}

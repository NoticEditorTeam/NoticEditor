package com.temporaryteam.noticeditor.model;

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
}

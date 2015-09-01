package com.temporaryteam.noticeditor.view;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * File and directory chooser dialog
 * @author aNNiMON
 */
public final class Chooser {
	
	public static final ExtensionFilter SUPPORTED = new ExtensionFilter("Supported Files", "*.zip", "*.txt", "*.md", "*.htm", "*.html", "*.json");
	public static final ExtensionFilter JSON = new ExtensionFilter("Json Files", "*.json");
	public static final ExtensionFilter ZIP = new ExtensionFilter("Zip Files", "*.zip");
	public static final ExtensionFilter ALL = new ExtensionFilter("All Files", "*.*");
	
	private static FileChooser fileChooser;
	private static DirectoryChooser directoryChooser;
	
	private static File lastDirectory;
	private static ExtensionFilter lastSelectedExtensionFilter;
	
	public static Chooser file() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}
		return new Chooser(true);
	}
	
	public static Chooser directory() {
		if (directoryChooser == null) {
			directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select folder to save");
		}
		return new Chooser(false);
	}
	
	public static void setInitialDirectory(File directory) {
		Chooser.lastDirectory = directory;
	}
	
	public static File getLastDirectory() {
		return lastDirectory;
	}
	
	public static ExtensionFilter getLastSelectedExtensionFilter() {
		return lastSelectedExtensionFilter;
	}

	private final boolean fileChooseMode;
	private boolean openMode;
	
	private Chooser(boolean fileChooseMode) {
		this.fileChooseMode = fileChooseMode;
		openMode = true;
	}
	
	public Chooser open() {
		openMode = true;
		return this;
	}
	
	public Chooser save() {
		openMode = false;
		return this;
	}
	
	public Chooser filter(ExtensionFilter... filters) {
		if (fileChooseMode) {
			fileChooser.getExtensionFilters().clear();
			fileChooser.getExtensionFilters().addAll(filters);
		}
		return this;
	}
	
	public Chooser title(String title) {
		if (fileChooseMode) {
			fileChooser.setTitle(title);
		} else {
			directoryChooser.setTitle(title);
		}
		return this;
	}
	
	public File show(Window window) {
		// Set initial directory from last session
		if (lastDirectory != null && lastDirectory.isDirectory() && lastDirectory.exists()) {
			if (fileChooseMode) {
				fileChooser.setInitialDirectory(lastDirectory);
			} else {
				directoryChooser.setInitialDirectory(lastDirectory);
			}
		}
		
		File result;
		if (fileChooseMode) {
			if (openMode) {
				result = fileChooser.showOpenDialog(window);
			} else {
				result = fileChooser.showSaveDialog(window);
			}
			// Save last directory and selected extension filter
			if (result != null) lastDirectory = result.getParentFile();
			lastSelectedExtensionFilter = fileChooser.getSelectedExtensionFilter();
		} else {
			result = directoryChooser.showDialog(window);
			lastDirectory = result;
		}
		
		return result;
	}
}

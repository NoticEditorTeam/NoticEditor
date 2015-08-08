package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import java.io.File;

/**
 * Export notices.strategy.
 * @author aNNiMON
 */
public interface ExportStrategy {
	
	void export(File file, NoticeTreeItem notice) throws ExportException;
}

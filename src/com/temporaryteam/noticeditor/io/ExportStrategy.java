package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;

/**
 * Export notices.strategy.
 * @author aNNiMON
 */
public interface ExportStrategy {
	
	void export(File file, NoticeTree tree) throws ExportException;
}

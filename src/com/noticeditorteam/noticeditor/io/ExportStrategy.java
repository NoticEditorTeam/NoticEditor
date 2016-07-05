package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.model.NoticeTree;
import java.io.File;

/**
 * Export notices.strategy.
 *
 * @author aNNiMON
 */
public interface ExportStrategy {

    void export(File file, NoticeTree tree) throws ExportException;
}

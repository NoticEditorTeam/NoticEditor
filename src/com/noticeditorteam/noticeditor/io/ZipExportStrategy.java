package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.exceptions.ExportException;
import com.noticeditorteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

/**
 * Export document to zip archive with index.json.
 *
 * @author aNNiMON
 */
public class ZipExportStrategy implements ExportStrategy {

    @Override
    public boolean export(File file, NoticeTree notice) {
        try {
            if (file.exists())
                file.delete();
            ZipWithIndexFormat.with(file).export(notice);
            return true;
        } catch (IOException | JSONException e) {
            throw new ExportException(e);
        }
    }
}

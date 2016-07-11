package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

/**
 * Export notices to json.
 *
 * @author aNNiMON
 */
public class JsonExportStrategy implements ExportStrategy {

    @Override
    public boolean export(File file, NoticeTree tree) {
        try {
            JsonFormat.with(file).export(tree);
            return true;
        } catch (IOException | JSONException e) {
            throw new ExportException(e);
        }
    }

}

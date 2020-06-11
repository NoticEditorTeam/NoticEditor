package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.controller.PasswordManager;
import com.noticeditorteam.noticeditor.exceptions.ExportException;
import com.noticeditorteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.json.JSONException;

/**
 * Export document to encrypted zip archive with index.json.
 *
 * @author aNNiMON
 */
public class EncZipExportStrategy implements ExportStrategy {

    @Override
    public boolean export(File file, NoticeTree notice) {
        final Optional<String> password = PasswordManager.askPassword(file.getAbsolutePath());
        if (!password.isPresent()) return false;
        try {
            if (file.exists())
                file.delete();
            ZipWithIndexFormat.with(file, password.get())
                    .encrypted()
                    .export(notice);
            return true;
        } catch (IOException | JSONException e) {
            throw new ExportException(e);
        }
    }
}

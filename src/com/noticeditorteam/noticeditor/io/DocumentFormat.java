package com.noticeditorteam.noticeditor.io;

import com.noticeditorteam.noticeditor.controller.PasswordManager;
import com.noticeditorteam.noticeditor.io.importers.FileImporter;
import com.noticeditorteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;

/**
 * Provides common operations with document.
 *
 * @author aNNiMON
 */
public final class DocumentFormat {

    public static NoticeTree open(File file) throws IOException {
        final boolean isZip = file.getName().toLowerCase().endsWith(".zip");
        try {
            if (isZip) {
                return ZipWithIndexFormat.with(file).importDocument();
            }
            return JsonFormat.with(file).importDocument();
        } catch (IOException | JSONException e) {
            if (isZip) {
                // Prevent to open binary files as text
                PasswordManager.resetPassword();
                throw new IOException(e);
            }
            return FileImporter.Tree.importFrom(file);
        }
    }

    public static boolean save(File file, NoticeTree tree, ExportStrategy strategy) {
        return strategy.export(file, tree);
    }
}

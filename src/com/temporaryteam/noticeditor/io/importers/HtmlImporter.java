package com.temporaryteam.noticeditor.io.importers;

import javafx.application.Platform;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Clears html page. Inserts scripts, styles, images directly to html.
 *
 * @author Naik, aNNiMON
 */
public class HtmlImporter implements Importer<String, HtmlImportMode, String> {

	@Override
	public void importFrom(final String html, final HtmlImportMode mode, final ImportCallback<String, Exception> callback) {
		new Thread(() -> {
            try {
				String result = cleanHtml(html, mode.getWhitelist());
                Platform.runLater(() -> callback.call(result, null));
            } catch (Exception ex) {
                Platform.runLater(() -> callback.call(null, ex));
            }
        }).start();
	}
	
	protected String cleanHtml(String html, Whitelist whitelist) throws Exception {
		if (whitelist == null) return html;
		return Jsoup.clean(html, whitelist);
	}
}
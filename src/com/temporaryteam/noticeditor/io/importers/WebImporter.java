package com.temporaryteam.noticeditor.io.importers;

import com.temporaryteam.noticeditor.io.IOUtil;
import java.net.URL;
import org.jsoup.safety.Whitelist;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 *
 * @author Naik
 */
public class WebImporter extends HtmlImporter {
	
	@Override
	protected String cleanHtml(String url, Whitelist whitelist) throws Exception {
		String html = IOUtil.stringFromStream(new URL(url).openStream());
		return super.cleanHtml(html, whitelist);
	}
}

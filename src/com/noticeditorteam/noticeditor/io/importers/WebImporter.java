package com.noticeditorteam.noticeditor.io.importers;

import com.noticeditorteam.noticeditor.io.IOUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.safety.Whitelist;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 *
 * @author Naik
 */
public class WebImporter extends HtmlImporter {

	private final Map<String, String> cache = new HashMap<>();

	@Override
	protected String cleanHtml(String url, Whitelist whitelist) throws Exception {
		String html;
		if (cache.containsKey(url)) {
			html = cache.get(url);
		} else {
			html = IOUtil.stringFromStream(new URL(url).openStream());
			cache.put(url, html);
		}
		return super.cleanHtml(html, whitelist);
	}
}

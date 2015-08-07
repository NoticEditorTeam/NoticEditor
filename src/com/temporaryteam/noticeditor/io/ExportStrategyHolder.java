package com.temporaryteam.noticeditor.io;

/**
 *
 * @author aNNiMON
 */
public final class ExportStrategyHolder {

	public static final JsonExportStrategy JSON = new JsonExportStrategy();
	public static final ZipExportStrategy ZIP = new ZipExportStrategy();
	public static final HtmlExportStrategy HTML = new HtmlExportStrategy();
}

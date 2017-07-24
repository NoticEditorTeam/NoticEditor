package com.noticeditorteam.noticeditor.io;

/**
 * @author aNNiMON
 */
public final class ExportStrategyHolder {

    public static final JsonExportStrategy JSON = new JsonExportStrategy();
    public static final ZipExportStrategy ZIP = new ZipExportStrategy();
    public static final EncZipExportStrategy ENC_ZIP = new EncZipExportStrategy();
    public static final HtmlExportStrategy HTML = new HtmlExportStrategy();
    public static final SingleHtmlExportStrategy SINGLE_HTML = new SingleHtmlExportStrategy();
}

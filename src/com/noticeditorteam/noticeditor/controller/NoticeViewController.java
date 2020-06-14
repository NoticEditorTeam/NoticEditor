package com.noticeditorteam.noticeditor.controller;

import com.noticeditorteam.noticeditor.Main;
import com.noticeditorteam.noticeditor.io.IOUtil;
import com.noticeditorteam.noticeditor.model.*;
import com.noticeditorteam.noticeditor.view.Chooser;
import com.noticeditorteam.noticeditor.view.Notification;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Matcher;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import static javafx.scene.control.SelectionMode.SINGLE;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Edward Minasyan <mrEDitor@mail.ru>
 */
public class NoticeViewController implements Initializable {

    @FXML
    private MenuItem itemCopyAttachmentTag;
    @FXML
    private MenuItem itemAttachFile;
    @FXML
    private MenuItem itemImportAttachment;
    @FXML
    private MenuItem itemExportAttachment;
    @FXML
    private MenuItem itemDeleteAttachment;

    @FXML
    private ListView<Attachment> attachmentsView;

    private ResourceBundle resources;

    @FXML
    private Tab attachmentsTab;

    @FXML
    private TextArea editor;

    @FXML
    private WebView viewer;

    private final ObjectProperty<Attachment> currentAttachmentProperty = new SimpleObjectProperty<>(null);

    protected final Parser mdParser;
    protected final HtmlRenderer htmlRenderer;
    protected final SyntaxHighlighter highlighter;
    private WebEngine engine;
    private Main main;

    private String codeCssName;


    public NoticeViewController() {
        final MutableDataSet options = new MutableDataSet();
        options.set(Parser.MATCH_CLOSING_FENCE_CHARACTERS, false);
        options.set(TablesExtension.TRIM_CELL_WHITESPACE, false);
        options.set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, false);
        options.set(Parser.EXTENSIONS, List.of(
                AutolinkExtension.create(),
                TablesExtension.create(),
                TaskListExtension.create()
        ));
        mdParser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();
        highlighter = new SyntaxHighlighter();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        highlighter.unpackHighlightJs();
        engine = viewer.getEngine();
        editor.textProperty().addListener((o, oldValue, newValue) -> changeContent(newValue));
        attachmentsView.getSelectionModel().setSelectionMode(SINGLE);
        attachmentsView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            currentAttachmentProperty.setValue((Attachment) newValue);
        });
        itemCopyAttachmentTag.disableProperty().bind(Bindings.isNull(currentAttachmentProperty));
        itemExportAttachment.disableProperty().bind(Bindings.isNull(currentAttachmentProperty));
        itemDeleteAttachment.disableProperty().bind(Bindings.isNull(currentAttachmentProperty));
        resources = rb;
    }

    public Tab getAttachmentsTab() {
        return attachmentsTab;
    }

    public TextArea getEditor() {
        return editor;
    }

    private void changeContent(String newContent) {
        final NoticeTreeItem current = getCurrentNotice();
        String parsed = htmlRenderer.render(mdParser.parse(newContent));
        if (current != null) {
            current.changeContent(newContent);
            parsed = parseAttachments(parsed, current.getAttachments());
        }
        engine.loadContent(highlighter.highlight(parsed, codeCssName));
    }

    private String parseAttachments(String text, Attachments attachments) {
        if (attachments.isEmpty()) return text;

        final Matcher m = Attachment.PATTERN.matcher(text);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String name = m.group(1);
            Attachment att = attachments.get(name);
            if (att != null) {
                m.appendReplacement(sb, "<img src=\"data:image/png;base64, " + att.getDataAsBase64() + "\"/>");
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public final EventHandler<ActionEvent> onPreviewStyleChange = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            final int ordinal = (int) ((RadioMenuItem) e.getSource()).getUserData();
            PreviewStyles style = PreviewStyles.values()[ordinal];
            // CSS path
            String path = style.getCssPath();
            if (path != null) {
                path = getClass().getResource(path).toExternalForm();
            }
            codeCssName = style.getCodeCssName();
            engine.setUserStyleSheetLocation(path);
            changeContent(editor.textProperty().getValue());
        }
    };

    public final EventHandler<ActionEvent> onThemeChange = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            // Remove all previous themes if exists
            for (Themes value : Themes.values()) {
                if (value.getCssPath() != null) {
                    String path = getClass().getResource(value.getCssPath()).toExternalForm();
                    main.getPrimaryStage().getScene().getStylesheets().remove(path);
                }
            }

            // Add new theme
            final int ordinal = (int) ((RadioMenuItem) e.getSource()).getUserData();
            Themes theme = Themes.values()[ordinal];
            String path = theme.getCssPath();
            if (path != null) {
                path = getClass().getResource(path).toExternalForm();
                main.getPrimaryStage().getScene().getStylesheets().add(path);
            }
        }
    };

    public void rebuildAttachsView() {
        onAttachmentsFocused(null);
    }

    @FXML
    private void handleContextMenu(ActionEvent event) {
        final Object source = event.getSource();

        if (source == itemImportAttachment) {
            if (!canAddAttachments()) return;
            openAttachmentImporter();
            return;
        }

        if (source == itemAttachFile) {
            if (!canAddAttachments()) return;
            attachFile();
            return;
        }

        if (source == itemCopyAttachmentTag) {
            final var attachment = currentAttachmentProperty.get();
            if (attachment == null) return;
            final var content = new ClipboardContent();
            content.putString(Attachment.PREFIX + attachment.getName());
            Clipboard.getSystemClipboard().setContent(content);
            return;
        }

        if (source == itemExportAttachment) {
            final var attachment = currentAttachmentProperty.get();
            if (attachment == null) return;
            exportAttachment(attachment);
            return;
        }

        if (source == itemDeleteAttachment) {
            final var attachment = currentAttachmentProperty.get();
            if (attachment == null) return;
            final NoticeTreeItem current = getCurrentNotice();
            current.getAttachments().remove(attachment);
            rebuildAttachsView();
        }
    }

    public void attachFile() {
        File file = Chooser.file().open()
                .filter(Chooser.ALL)
                .title(resources.getString("openfile"))
                .show(main.getPrimaryStage());
        if (file != null) {
            try {
                getCurrentNotice().addAttachment(file);
            } catch (Exception e) {
                NoticeController.getLogger().log(Level.SEVERE, "addFile", e);
            }
        }
        rebuildAttachsView();
    }

    private void openAttachmentImporter() {
        try {
            var resource = ResourceBundle.getBundle(
                    "resources.i18n.AttachmentImport", Locale.getDefault());

            final Stage stage = new Stage();
            stage.setTitle(resource.getString("title"));
            stage.initOwner(main.getPrimaryStage());
            stage.initModality(Modality.WINDOW_MODAL);

            var loader = new FXMLLoader(getClass().getResource("/fxml/AttachmentImport.fxml"), resource);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            NoticeController.getLogger().log(Level.SEVERE, "importAttachmentContextMenu", ex);
        }
    }

    private void exportAttachment(Attachment attachment) {
        File fileSaved = Chooser.file().save()
                .filter(Chooser.ALL)
                .title(resources.getString("exportfile"))
                .show(main.getPrimaryStage());
        if (fileSaved != null) {
            exportAttachmentAs(attachment, fileSaved);
        }
    }

    private void exportAttachmentAs(Attachment attachment, File file) {
        try {
            IOUtil.writeContent(file, attachment.getData());
            Notification.success(resources.getString("export.success"));
        } catch (IOException e) {
            NoticeController.getLogger().log(Level.SEVERE, null, e);
            Notification.error(resources.getString("export.error"));
        }
    }

    @FXML
    private void onAttachmentsFocused(Event event) {
        final NoticeTreeItem current = getCurrentNotice();
        attachmentsView.getItems().clear();
        if (current != null && current.isLeaf()) {
            for (Attachment attachment : current.getAttachments()) {
                attachmentsView.getItems().add(attachment);
            }
        }
    }

    private boolean canAddAttachments() {
        final var note = getCurrentNotice();
        return (note != null) && (!note.isBranch());
    }

    public void setMain(Main main) {
        this.main = main;
    }

    private NoticeTreeItem getCurrentNotice() {
        return NoticeController.getNoticeTreeViewController().getCurrentNotice();
    }
}

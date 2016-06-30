package com.noticeditorteam.noticeditor.controller;

import com.noticeditorteam.noticeditor.Main;
import com.noticeditorteam.noticeditor.model.PreviewStyles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.pegdown.PegDownProcessor;

import java.net.URL;
import java.util.ResourceBundle;

import static org.pegdown.Extensions.*;

/**
 * @author Edward Minasyan <mrEDitor@mail.ru>
 */
public class NoticeViewController implements Initializable {

    @FXML
    private TextArea editor;

    @FXML
    private WebView viewer;

    protected final PegDownProcessor processor;
    protected final SyntaxHighlighter highlighter;
    private WebEngine engine;
    private Main main;

	private String codeCssName;

    public NoticeViewController() {
        processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
        highlighter = new SyntaxHighlighter();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        highlighter.unpackHighlightJs();
        engine = viewer.getEngine();
        editor.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                changeContent(newValue);
            }
        });
    }

    public TextArea getEditor() {
        return editor;
    }

	private void changeContent(String newValue) {
		engine.loadContent(highlighter.highlight(processor.markdownToHtml(newValue), codeCssName));
		if (NoticeController.getNoticeTreeViewController().getCurrentNotice() != null) {
			NoticeController.getNoticeTreeViewController().getCurrentNotice().changeContent(newValue);
		}
	}

    public final EventHandler<ActionEvent> onPreviewStyleChange = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            // CSS path
            final int ordinal = (int) ((RadioMenuItem) e.getSource()).getUserData();
			PreviewStyles style = PreviewStyles.values()[ordinal];
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
            // CSS path
            String path = (String) ((RadioMenuItem) e.getSource()).getUserData();
            if (path != null) {
                path = getClass().getResource(path).toExternalForm();
                main.getPrimaryStage().getScene().getStylesheets().add(path);
            } else {
                main.getPrimaryStage().getScene().getStylesheets().clear();
            }
        }
    };

    public void setMain(Main main) {
        this.main = main;
    }
}

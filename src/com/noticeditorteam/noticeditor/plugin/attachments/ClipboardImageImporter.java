package com.noticeditorteam.noticeditor.plugin.attachments;

import com.noticeditorteam.noticeditor.model.Attachment;
import com.noticeditorteam.noticeditor.model.Attachments;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.imageio.ImageIO;

public final class ClipboardImageImporter extends AttachmentImporter {

    private enum ImageFormat {
        PNG("png", ".png"),
        JPEG("jpeg", ".jpg");

        String format, extension;

        ImageFormat(String format, String visibleFormat) {
            this.format = format;
            this.extension = visibleFormat;
        }

        @Override
        public String toString() {
            return extension;
        }
    }

    private final HBox propertiesBox;
    private final TextField filenameField;
    private final ComboBox<ImageFormat> formatComboBox;
    private final ImageView imageView;

    private final EventHandler<KeyEvent> keyHandler;
    private final EventHandler<MouseEvent> mouseHandler;

    public ClipboardImageImporter(ResourceBundle resources) {
        super(resources);
        filenameField = new TextField(defaultFilename());
        formatComboBox = new ComboBox<ImageFormat>();
        formatComboBox.getItems().addAll(ImageFormat.values());
        formatComboBox.getSelectionModel().selectFirst();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFocusTraversable(true);
        propertiesBox = new HBox();
        propertiesBox.getChildren().add(filenameField);
        propertiesBox.getChildren().add(formatComboBox);
        HBox.setHgrow(filenameField, Priority.ALWAYS);
        mouseHandler = (event) -> {
            container.requestFocus();
        };
        keyHandler = (event) -> {
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                onPaste();
            }
        };
    }

    @Override
    public String name() {
        return resources.getString("import_image_from_clipboard");
    }

    @Override
    protected Task<Attachments> createTask() {
        return new Task<Attachments>() {
            @Override
            protected Attachments call() throws Exception {
                final Attachments result = new Attachments();
                final Image image = imageView.getImage();
                if (image == null) return result;

                final BufferedImage swingImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ImageFormat format = formatComboBox.getValue();
                ImageIO.write(swingImage, format.format, baos);
                baos.close();

                imageView.setImage(null);

                final String filename = Optional.ofNullable(filenameField.getText())
                        .orElse(defaultFilename()) + format.extension;
                result.add(new Attachment(filename, baos.toByteArray()));
                return result;
            }
        };
    }

    @Override
    public void onActivated() {
        super.onActivated();
        container.setFocusTraversable(true);
        container.setTop(propertiesBox);
        container.setCenter(imageView);
        container.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
        container.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
    }

    @Override
    public void onDeactivated() {
        super.onDeactivated();
        container.setTop(null);
        container.setCenter(null);
        container.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
        container.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
    }

    @Override
    public Optional<BooleanBinding> importButtonDisabled() {
        return Optional.of(Bindings.or(
                Bindings.isNull(imageView.imageProperty()),
                Bindings.isEmpty(filenameField.textProperty())));
    }

    private void onPaste() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasImage()) return;

        final Image image = clipboard.getImage();
        imageView.setImage(image);
        imageView.setFitWidth(0);
        imageView.setFitHeight(0);
        if (image.getWidth() > container.getWidth()) {
            imageView.setFitWidth(container.getWidth());
        }
        if (image.getHeight() > container.getHeight()) {
            imageView.setFitHeight(container.getHeight());
        }
    }

    private String defaultFilename() {
        return new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss").format(new Date());
    }
}

package com.noticeditorteam.noticeditor.plugin.attachments;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javax.imageio.ImageIO;

public class ClipboardImageHelper {

    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    public boolean hasImage() {
        return clipboard.hasImage();
    }

    public Optional<Image> findImageInFXClipboard() {
        return Optional.ofNullable(clipboard.getImage());
    }

    public Optional<Image> findImageByMime(String preferedMime) {
        for (var type : clipboard.getContentTypes()) {
            var hasImage = type.getIdentifiers().stream()
                    .anyMatch(mime -> mime.equalsIgnoreCase(preferedMime));
            if (!hasImage) continue;

            final var data = clipboard.getContent(type);
            if (data instanceof ByteBuffer) {
                final byte[] bytes = ((ByteBuffer) data).array();
                final var image = imageFromBytes(bytes);
                if (image != null) {
                    return image;
                }
                continue;
            }
            if (data instanceof Image) {
                return Optional.of((Image) data);
            }
        }
        return Optional.empty();
    }

    public Optional<Image> findImageInAWTClipboard() {
        var transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                var awtImage = (java.awt.Image) transferable.getTransferData(DataFlavor.imageFlavor);
                if (awtImage == null) {
                    return null;
                }
                if (!(awtImage instanceof RenderedImage)) {
                    awtImage = toBufferedImage(awtImage);
                }
                final var baos = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage) awtImage, "png", baos);
                baos.flush();
                return imageFromBytes(baos.toByteArray());
            } catch (UnsupportedFlavorException | IOException ignore) { }
        }
        return Optional.empty();
    }

    private BufferedImage toBufferedImage(java.awt.Image awtImage) {
        var bufferedImage = new BufferedImage(
                awtImage.getWidth(null), awtImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        var g = bufferedImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    private Optional<Image> imageFromBytes(byte[] bytes) {
        try (final var bais = new ByteArrayInputStream(bytes)) {
            final var image = new Image(bais);
            final var isValidImage = !image.isError() && image.getWidth() > 0;
            if (isValidImage) {
                return Optional.of(image);
            }
        } catch (IOException ignore) {}
        return Optional.empty();
    }
}

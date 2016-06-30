package com.noticeditorteam.noticeditor.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * Simple notification.
 *
 * @author aNNiMON
 */
public final class Notification {

    public static final Duration DURATION_SHORT = new Duration(2000);
    public static final Duration DURATION_LONG = new Duration(5000);

    private static final Duration TRANSITION_DURATION = new Duration(300);
    private static final Paint PAINT_MESSAGE = Color.WHITE;
    private static final Paint PAINT_ERROR = Color.rgb(255, 80, 80);
    private static final Paint PAINT_SUCCESS = Color.LIGHTGREEN;

    private static VBox notificationBox;
    private static Label notificationLabel;

    private static Timeline hideTimer;
    private static TranslateTransition transitionIn, transitionOut;

    public static void init(VBox vbox, Label label) {
        notificationBox = vbox;
        notificationLabel = label;

        transitionIn = new TranslateTransition(TRANSITION_DURATION, notificationBox);
        transitionIn.setToY(0);

        transitionOut = new TranslateTransition(TRANSITION_DURATION, notificationBox);
        transitionOut.setFromY(0);
        transitionOut.setOnFinished((e) -> notificationBox.setVisible(false));
    }

    public static void show(String text) {
        show(text, DURATION_SHORT);
    }

    public static void show(String text, Duration duration) {
        show(text, duration, PAINT_MESSAGE);
    }

    public static void error(String text) {
        show(text, DURATION_LONG, PAINT_ERROR);
    }

    public static void success(String text) {
        show(text, DURATION_LONG, PAINT_SUCCESS);
    }

    public static void show(String text, Duration duration, Paint textFill) {
        if (hideTimer != null) {
            // show new notification while previous exists
            hideTimer.stop();
            transitionOut.stop();
        }
        notificationLabel.setTextFill(textFill);
        notificationLabel.setText(text);
        hideTimer = new Timeline(new KeyFrame(duration.add(TRANSITION_DURATION)));
        hideTimer.setOnFinished(Notification::hide);
        hideTimer.playFromStart();
        notificationBox.setVisible(true);

        transitionIn.setFromY(notificationBox.getHeight());
        transitionIn.playFromStart();
    }

    private static void hide(ActionEvent e) {
        if (hideTimer != null) {
            hideTimer.stop();
            hideTimer = null;
        }
        transitionOut.setToY(notificationBox.getHeight());
        transitionOut.playFromStart();
    }
}

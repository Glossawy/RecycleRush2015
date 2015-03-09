package org.usfirst.frc.team1554.lib.installer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

final class FXGuiUtils {

    public static final void setToggleTextSwitch(final ToggleButton btn, final String onText, final String offText) {

        btn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (btn.isSelected()) {
                    btn.setText(onText);
                } else {
                    btn.setText(offText);
                }
            }

        });

        btn.fireEvent(new ActionEvent());
    }

    public static final void setMaxCharCount(final TextInputControl control, final int count) {
        control.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                final String text = control.getText();
                if (text.length() == count) {
                    event.consume();
                } else if (text.length() > count) {
                    control.setText(text.substring(0, count));
                    event.consume();
                }
            }

        });
    }

    public static final boolean addTogglesToGroup(ToggleGroup group, Toggle... toggles) {
        return group.getToggles().addAll(toggles);
    }

    public static final Point2D getScreenCoordinates(Node node) {

        final double x = node.getScene().getWindow().getX();
        final double y = node.getScene().getWindow().getY();

        final Bounds localBounds = node.localToScene(node.getBoundsInLocal());

        return new Point2D(x + localBounds.getMinX(), y + localBounds.getMaxY());
    }

    public static boolean canUseJavaFX() {
        try {
            return Class.forName("javafx.application.Application") != null;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static void makeAlwaysOnTop(final Stage stage) {
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue.booleanValue()) {
                    stage.requestFocus();
                    stage.toFront();
                }
            }

        });
    }

    public static Resource loadImage(String name) {
        return () -> FXGuiUtils.class.getClassLoader().getResource(GUIRef.RES_PACKAGE + name);
    }

    @FunctionalInterface
    static interface Resource {
        URL url() throws MalformedURLException;

        default InputStream stream() throws IOException {
            URL url = url();
            return url == null ? null : url.openStream();
        }

    }

}

package org.csillagkoz;

import javafx.application.Application;
import javafx.stage.Stage;
import org.csillagkoz.visual.SpaceTheme;

/** Opt-in visual launcher. The original App and its controllers stay unchanged. */
public final class VisualApp extends App {
    private SpaceTheme theme;

    @Override
    public void start(Stage stage) throws java.io.IOException {
        super.start(stage);
        theme = new SpaceTheme(stage);
        stage.setMinWidth(680);
        stage.setMinHeight(540);
    }

    @Override
    public void stop() {
        if (theme != null) theme.close();
    }

    public static void main(String[] args) {
        Application.launch(VisualApp.class, args);
    }
}

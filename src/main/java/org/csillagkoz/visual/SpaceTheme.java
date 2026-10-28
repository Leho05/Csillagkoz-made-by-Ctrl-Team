package org.csillagkoz.visual;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Adds a disposable visual wrapper after each original App.setRoot navigation. */
public final class SpaceTheme implements AutoCloseable {
    final SpaceAssets assets = new SpaceAssets();
    final BooleanProperty motion = new SimpleBooleanProperty(true);
    String passengerType;
    String cabin = "ECONOMY";
    String departure;
    String destination;
    private final Stage stage;
    private final Scene scene;
    private SpaceView view;
    private boolean wrapping;
    private final ChangeListener<Parent> roots = (o, oldRoot, newRoot) -> decorate(newRoot);
    private final ChangeListener<Boolean> visibility = (o, a, b) -> refreshMotion();

    public SpaceTheme(Stage stage) {
        this.stage = stage;
        this.scene = stage.getScene();
        java.net.URL css = SpaceTheme.class.getResource("space.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        scene.rootProperty().addListener(roots);
        stage.iconifiedProperty().addListener(visibility);
        stage.showingProperty().addListener(visibility);
        motion.addListener(visibility);
        decorate(scene.getRoot());
    }

    private void decorate(Parent original) {
        if (wrapping || original == view) return;
        wrapping = true;
        try {
            if (view != null) view.close();
            // Detach the old FXML root before placing it into our wrapper.
            scene.setRoot(new Group());
            view = new SpaceView(original, this);
            scene.setRoot(view);
            refreshMotion();
        } finally { wrapping = false; }
    }

    private void refreshMotion() {
        if (view != null) view.setRunning(motion.get() && stage.isShowing() && !stage.isIconified());
    }

    @Override public void close() {
        scene.rootProperty().removeListener(roots);
        stage.iconifiedProperty().removeListener(visibility);
        stage.showingProperty().removeListener(visibility);
        motion.removeListener(visibility);
        if (view != null) view.close();
    }
}

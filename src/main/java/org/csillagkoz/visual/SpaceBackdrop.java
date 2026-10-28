package org.csillagkoz.visual;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/** Decorative layer; never intercepts mouse input. All animations have an owner. */
final class SpaceBackdrop extends Pane implements AutoCloseable {
    private final ImageView background;
    private final List<Animation> animations = new ArrayList<>();
    private final List<Orbit> orbits = new ArrayList<>();
    private final Rectangle clip = new Rectangle();

    SpaceBackdrop(SpaceAssets assets, boolean quiet) {
        setMouseTransparent(true);
        setPickOnBounds(false);
        setClip(clip);
        setStyle("-fx-background-color: #050b17;");
        background = SpaceAssets.view(assets.image("background"), 0, 0);
        getChildren().add(background);
        // Their positions leave the middle of the screen free for the form.
        orbit(assets, "planet.1", .06, .15, .16, 21, -10, 3, .60);
        orbit(assets, "planet.2", .94, .78, .11, 27, 8, -4, .48);
        orbit(assets, "planet.3", .84, .08, .13, 31, -7, 2, .56);
        orbit(assets, "blackhole.1", .96, .38, .13, 34, 9, 2, .40);
        if (!quiet) orbit(assets, "blackhole.2", .03, .80, .14, 38, -8, -3, .34);
        // The third supplied black hole is intentionally not used: two keep the form legible.
        fly(assets, "comet.1", 150, 0, 14, 4, 10, .72);
        fly(assets, "comet.2", 125, 1, 19, 12, 12, .60);
        fly(assets, "comet.3", 105, 2, 25, 21, 15, .65);
        fly(assets, "flyby", 74, 3, 26, 8, 18, .70);
    }

    private void orbit(SpaceAssets assets, String key, double x, double y, double ratio,
                       double seconds, double dy, double angle, double opacity) {
        Image image = assets.image(key);
        if (image == null) return;
        ImageView node = SpaceAssets.view(image, 180, 180);
        node.setOpacity(opacity);
        node.setCache(true);
        node.setCacheHint(CacheHint.SPEED);
        getChildren().add(node);
        orbits.add(new Orbit(node, x, y, ratio));
        TranslateTransition drift = new TranslateTransition(Duration.seconds(seconds), node);
        drift.setFromY(-dy); drift.setToY(dy);
        drift.setInterpolator(Interpolator.EASE_BOTH);
        drift.setAutoReverse(true); drift.setCycleCount(Animation.INDEFINITE);
        RotateTransition tilt = new RotateTransition(Duration.seconds(seconds * 1.4), node);
        tilt.setFromAngle(-angle); tilt.setToAngle(angle);
        tilt.setInterpolator(Interpolator.EASE_BOTH);
        tilt.setAutoReverse(true); tilt.setCycleCount(Animation.INDEFINITE);
        animations.add(new ParallelTransition(drift, tilt));
    }

    private void fly(SpaceAssets assets, String key, double width, int route,
                     double travel, double delay, double rest, double opacity) {
        Image image = assets.image(key);
        if (image == null) return;
        ImageView node = SpaceAssets.view(image, width, width / 2);
        node.setOpacity(0);
        getChildren().add(node);
        Transition movement = new Transition() {
            { setCycleDuration(Duration.seconds(travel + rest)); setInterpolator(Interpolator.LINEAR); }
            @Override protected void interpolate(double fraction) {
                double t = Math.min(1, fraction * (travel + rest) / travel);
                double distance = getWidth() + width + 200;
                double start = route == 1 ? getWidth() + 100 : -width - 100;
                node.setLayoutX(start + (route == 1 ? -distance : distance) * t);
                double y = route == 0 ? .18 : route == 1 ? .30 : route == 2 ? .82 : .60;
                node.setLayoutY(Math.max(75, getHeight() * y) + (route == 3 ? 18 : route == 1 ? 65 : -75) * t);
                node.setOpacity(opacity * Math.max(0, Math.min(1, Math.min(t * 20, (1 - t) * 20))));
            }
        };
        node.setScaleX(route == 1 ? 1 : -1);
        movement.setDelay(Duration.seconds(delay));
        movement.setCycleCount(Animation.INDEFINITE);
        animations.add(movement);
    }

    @Override protected void layoutChildren() {
        double w = getWidth(), h = getHeight();
        clip.setWidth(w); clip.setHeight(h);
        Image image = background.getImage();
        if (image != null && w > 0 && h > 0) {
            // Reveal more of the source at larger sizes; crop around its centre at smaller sizes.
            double scale = Math.max(Math.max(w / image.getWidth(), h / image.getHeight()), .44);
            double sourceW = Math.min(image.getWidth(), w / scale);
            double sourceH = Math.min(image.getHeight(), h / scale);
            background.setViewport(new Rectangle2D((image.getWidth() - sourceW) / 2,
                    (image.getHeight() - sourceH) / 2, sourceW, sourceH));
            background.setFitWidth(w); background.setFitHeight(h);
            background.relocate(0, 0);
        }
        for (Orbit orbit : orbits) {
            double size = Math.max(74, Math.min(230, Math.min(w, h * 1.4) * orbit.ratio));
            orbit.node.setFitWidth(size); orbit.node.setFitHeight(size);
            orbit.node.relocate(w * orbit.x - size / 2, h * orbit.y - size / 2);
        }
    }

    void setRunning(boolean running) {
        for (Animation animation : animations) {
            if (running) animation.play(); else animation.pause();
        }
    }

    @Override public void close() { animations.forEach(Animation::stop); }

    private static final class Orbit {
        final ImageView node; final double x, y, ratio;
        Orbit(ImageView node, double x, double y, double ratio) {
            this.node = node; this.x = x; this.y = y; this.ratio = ratio;
        }
    }
}

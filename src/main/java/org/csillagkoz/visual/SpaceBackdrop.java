package org.csillagkoz.visual;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
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
import java.util.Random;

/** Decorative layer; never intercepts mouse input. All animations have an owner. */
final class SpaceBackdrop extends Pane implements AutoCloseable {
    private final ImageView background;
    private final List<Animation> animations = new ArrayList<>();
    private final List<Orbit> orbits = new ArrayList<>();
    private final Rectangle clip = new Rectangle();
    private final Random random = new Random();

    SpaceBackdrop(SpaceAssets assets, boolean quiet) {
        setMouseTransparent(true);
        setPickOnBounds(false);
        setClip(clip);
        setStyle("-fx-background-color: #050b17;");
        background = SpaceAssets.view(assets.image("background"), 0, 0);
        getChildren().add(background);
        // Their positions leave the middle of the screen free for the form.
        orbit(assets, "planet.1", .06, .15, .16, 21, -17, 8, .60);
        orbit(assets, "planet.2", .94, .78, .11, 27, 15, -9, .48);
        orbit(assets, "planet.3", .84, .08, .13, 31, -14, 7, .56);
        orbit(assets, "blackhole.1", .94, .38, .23, 27, 22, 9, .44);
        if (!quiet) orbit(assets, "blackhole.2", .03, .80, .14, 38, -8, -3, .34);
        // The third supplied black hole is intentionally not used: two keep the form legible.
        fly(assets, "comet.1", 150, 0, 13, 2, 5, .72);
        fly(assets, "comet.2", 125, 1, 17, 7, 6, .60);
        fly(assets, "comet.3", 105, 2, 21, 12, 7, .65);
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
        boolean rightBlackHole = key.equals("blackhole.1");
        orbits.add(new Orbit(node, x, y, ratio, rightBlackHole ? 350 : 230));
        TranslateTransition drift = new TranslateTransition(Duration.seconds(seconds), node);
        drift.setFromY(-dy); drift.setToY(dy);
        drift.setFromX(-dy * .35); drift.setToX(dy * .35);
        drift.setInterpolator(Interpolator.EASE_BOTH);
        drift.setAutoReverse(true); drift.setCycleCount(Animation.INDEFINITE);
        RotateTransition tilt = new RotateTransition(Duration.seconds(seconds * 1.4), node);
        tilt.setFromAngle(-angle); tilt.setToAngle(angle);
        tilt.setInterpolator(Interpolator.EASE_BOTH);
        tilt.setAutoReverse(true); tilt.setCycleCount(Animation.INDEFINITE);
        ParallelTransition motion = new ParallelTransition(drift, tilt);
        if (rightBlackHole) {
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(8), node);
            pulse.setFromX(.94); pulse.setFromY(.94);
            pulse.setToX(1.08); pulse.setToY(1.08);
            pulse.setInterpolator(Interpolator.EASE_BOTH);
            pulse.setAutoReverse(true); pulse.setCycleCount(Animation.INDEFINITE);
            motion.getChildren().add(pulse);
        }
        animations.add(motion);
    }

    private void fly(SpaceAssets assets, String key, double width, int route,
                     double travel, double delay, double rest, double opacity) {
        Image image = assets.image(key);
        if (image == null) return;
        ImageView node = SpaceAssets.view(image, width, width / 2);
        node.setOpacity(0);
        getChildren().add(node);
        Transition movement = new Transition() {
            private FlightPath path;
            private double lastFraction = -1;
            private int direction = route;
            { setCycleDuration(Duration.seconds(travel + rest)); setInterpolator(Interpolator.LINEAR); }
            @Override protected void interpolate(double fraction) {
                if (path == null || fraction < lastFraction) {
                    if (path != null && route != 3) {
                        // Pick a different direction once per cycle, never per frame.
                        direction = (direction + 1 + random.nextInt(7)) % 8;
                    }
                    path = route == 3 ? new FlightPath(-1, .60, 2, .62)
                            : FlightPath.comet(direction, random);
                }
                lastFraction = fraction;
                double t = Math.min(1, fraction * (travel + rest) / travel);
                double padding = width + 80;
                double x0 = FlightPath.coordinate(path.x0, getWidth(), padding);
                double x1 = FlightPath.coordinate(path.x1, getWidth(), padding);
                double y0 = FlightPath.coordinate(path.y0, getHeight(), padding);
                double y1 = FlightPath.coordinate(path.y1, getHeight(), padding);
                node.setLayoutX(x0 + (x1 - x0) * t);
                node.setLayoutY(y0 + (y1 - y0) * t);
                // The supplied sprites point left; orient their heads along the path.
                node.setRotate(Math.toDegrees(Math.atan2(y1 - y0, x1 - x0)) - 180);
                node.setOpacity(opacity * Math.max(0, Math.min(1, Math.min(t * 20, (1 - t) * 20))));
            }
        };
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
            double size = Math.max(74, Math.min(orbit.maxSize, Math.min(w, h * 1.4) * orbit.ratio));
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
        final ImageView node; final double x, y, ratio, maxSize;
        Orbit(ImageView node, double x, double y, double ratio, double maxSize) {
            this.node = node; this.x = x; this.y = y; this.ratio = ratio; this.maxSize = maxSize;
        }
    }

    /** Normalized routes, with -1 and 2 representing a point outside an edge. */
    static final class FlightPath {
        final double x0, y0, x1, y1;
        FlightPath(double x0, double y0, double x1, double y1) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }
        static FlightPath comet(int direction, Random random) {
            double low = .10 + random.nextDouble() * .25;
            double high = .65 + random.nextDouble() * .25;
            switch (direction) {
                case 0: return new FlightPath(-1, low, 2, high);
                case 1: return new FlightPath(2, low, -1, high);
                case 2: return new FlightPath(low, 2, high, -1);
                case 3: return new FlightPath(low, -1, high, 2);
                case 4: return new FlightPath(-1, high, 2, low);
                case 5: return new FlightPath(2, high, -1, low);
                case 6: return new FlightPath(high, -1, low, 2);
                case 7: return new FlightPath(high, 2, low, -1);
                default: throw new IllegalArgumentException("Unknown comet direction");
            }
        }
        static double coordinate(double position, double extent, double padding) {
            return position == -1 ? -padding : position == 2 ? extent + padding : position * extent;
        }
    }
}

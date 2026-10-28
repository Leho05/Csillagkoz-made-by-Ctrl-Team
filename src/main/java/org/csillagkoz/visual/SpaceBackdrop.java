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
        fly(assets, "comet.1", 150, 13, 1, .72, true);
        fly(assets, "comet.2", 125, 17, 3, .60, true);
        fly(assets, "comet.3", 105, 21, 5, .65, true);
        fly(assets, "flyby", 74, 26, 2, .70, false);
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

    private void fly(SpaceAssets assets, String key, double width,
                     double travel, double delay, double opacity, boolean comet) {
        Image image = assets.image(key);
        if (image == null) return;
        FlightStream stream = new FlightStream(travel, comet, random);
        ImageView[] nodes = new ImageView[FlightStream.CAPACITY];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = SpaceAssets.view(image, width, width / 2);
            nodes[i].setOpacity(0);
            getChildren().add(nodes[i]);
        }
        // JavaFX's animation clock freezes while paused, including the spawn schedule.
        Transition movement = new Transition() {
            private double lastFraction = -1;
            private double elapsedCycles;
            { setCycleDuration(Duration.hours(1)); setInterpolator(Interpolator.LINEAR); }
            @Override protected void interpolate(double fraction) {
                if (fraction < lastFraction) elapsedCycles += 3600;
                lastFraction = fraction;
                double now = elapsedCycles + fraction * 3600;
                stream.advance(now);
                for (int i = 0; i < nodes.length; i++) {
                    Flight flight = stream.flights[i];
                    ImageView node = nodes[i];
                    if (flight == null || now >= flight.start + flight.duration) {
                        node.setOpacity(0);
                        continue;
                    }
                    FlightPath path = flight.path;
                    double t = (now - flight.start) / flight.duration;
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
            }
        };
        movement.setDelay(Duration.seconds(delay));
        movement.setCycleCount(Animation.INDEFINITE);
        animations.add(movement);
    }

    /** Bounded, independently timed flights sharing one bitmap per sprite type. */
    static final class FlightStream {
        static final int CAPACITY = 4;
        final Flight[] flights = new Flight[CAPACITY];
        private final double baseDuration;
        private final boolean comet;
        private final Random random;
        private double nextSpawn;
        private int direction;

        FlightStream(double baseDuration, boolean comet, Random random) {
            this.baseDuration = baseDuration;
            this.comet = comet;
            this.random = random;
            direction = random.nextInt(8);
        }

        void advance(double now) {
            if (now < nextSpawn) return;
            for (int i = 0; i < flights.length; i++) {
                Flight previous = flights[i];
                if (previous != null && now < previous.start + previous.duration) continue;
                double multiplier = comet ? 1 + random.nextDouble() * 2 : 1;
                double duration = baseDuration / multiplier;
                FlightPath path;
                if (comet) {
                    direction = (direction + 1 + random.nextInt(7)) % 8;
                    path = FlightPath.comet(direction, random);
                } else {
                    // Distinct heights keep overlapping ships apart without extra files.
                    double y = .32 + random.nextDouble() * .48;
                    path = new FlightPath(-1, y, 2, y + .03);
                }
                flights[i] = new Flight(now, duration, path);
                nextSpawn = now + duration * (.38 + random.nextDouble() * .24);
                return;
            }
            // Heavy overlap never allocates more nodes; retry when a slot can free up.
            nextSpawn = now + .2;
        }
    }

    static final class Flight {
        final double start, duration;
        final FlightPath path;
        Flight(double start, double duration, FlightPath path) {
            this.start = start; this.duration = duration; this.path = path;
        }
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

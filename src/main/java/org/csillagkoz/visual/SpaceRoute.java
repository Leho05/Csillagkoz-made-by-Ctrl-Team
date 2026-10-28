package org.csillagkoz.visual;

import javafx.beans.InvalidationListener;
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;

/** The supplied route image, connected to the actual planet views without stretching it. */
final class SpaceRoute extends Pane implements AutoCloseable {
    // Only the transparent margin is cropped at render time; the PNG remains unchanged.
    static final double IMAGE_WIDTH = 1980;
    static final double CROP_TOP = 337;
    static final double CROP_HEIGHT = 687;
    static final Point2D START = new Point2D(16, 1003 - CROP_TOP);
    static final Point2D END = new Point2D(1970, 1004 - CROP_TOP);
    private final ImageView route;
    private final ImageView departure;
    private final ImageView destination;
    private final java.util.List<Point2D> ink = new java.util.ArrayList<>();
    private final InvalidationListener moved = ignored -> requestLayout();
    private boolean connected;

    SpaceRoute(Image image, ImageView departure, ImageView destination) {
        this.departure = departure;
        this.destination = destination;
        setId("visualRoute");
        setManaged(false);
        setMouseTransparent(true);
        setPickOnBounds(false);
        route = new ImageView(image);
        route.setViewport(new Rectangle2D(0, CROP_TOP, IMAGE_WIDTH, CROP_HEIGHT));
        route.setFitWidth(IMAGE_WIDTH);
        route.setFitHeight(CROP_HEIGHT);
        route.setPreserveRatio(true);
        route.setSmooth(true);
        route.setManaged(false);
        route.setMouseTransparent(true);
        getChildren().add(route);
        if (image != null && image.getPixelReader() != null) {
            // Sample the painted pixels once. Resize layout can then keep every
            // visible loop above the form, including this hand-drawn image's shape.
            for (int y = 0; y < CROP_HEIGHT; y += 4) {
                for (int x = 0; x < IMAGE_WIDTH; x += 4) {
                    if ((image.getPixelReader().getArgb(x, y + (int) CROP_TOP) >>> 24) > 32) {
                        ink.add(new Point2D(x, y));
                    }
                }
            }
        }
        departure.localToSceneTransformProperty().addListener(moved);
        destination.localToSceneTransformProperty().addListener(moved);
        departure.layoutBoundsProperty().addListener(moved);
        destination.layoutBoundsProperty().addListener(moved);
        sceneProperty().addListener(moved);
    }

    void setConnected(boolean connected) {
        this.connected = connected;
        requestLayout();
    }

    /** The side panels reserve just enough headroom to keep the whole arch on screen. */
    double topCorrection() {
        if (getScene() == null || departure.getBoundsInLocal().getWidth() == 0) return 0;
        Affine placement = fit(anchor(departure, .91, .30), anchor(destination, .60, .07));
        Bounds bounds = placement.transform(new BoundingBox(0, 0, IMAGE_WIDTH, CROP_HEIGHT));
        return 24 - localToScene(0, bounds.getMinY()).getY();
    }

    double formTop(double left, double width) {
        if (getScene() == null || getWidth() < 860 || route.getImage() == null) return 6;
        Affine placement = fit(anchor(departure, .91, .30), anchor(destination, .60, .07));
        double bottom = -6;
        for (Point2D point : ink) {
            double x = placement.getMxx() * point.getX() + placement.getMxy() * point.getY() + placement.getTx();
            if (x >= left - 8 && x <= left + width + 8) {
                double y = placement.getMyx() * point.getX() + placement.getMyy() * point.getY() + placement.getTy();
                bottom = Math.max(bottom, y);
            }
        }
        return Math.max(6, bottom + 12);
    }

    private Point2D anchor(ImageView image, double x, double y) {
        Bounds bounds = sceneToLocal(image.localToScene(image.getBoundsInLocal()));
        return new Point2D(bounds.getMinX() + bounds.getWidth() * x,
                bounds.getMinY() + bounds.getHeight() * y);
    }

    @Override protected void layoutChildren() {
        // The narrow layout stacks the form above two scrolling previews. There is
        // no unobstructed bridge there, so it keeps the individual planet previews.
        if (!connected || getWidth() < 860 || getScene() == null || route.getImage() == null) {
            route.setVisible(false);
            return;
        }
        Point2D start = anchor(departure, .91, .30);
        Point2D end = anchor(destination, .60, .07);
        boolean onScreen = start.getY() >= 0 && end.getY() >= 0
                && start.getY() <= getHeight() && end.getY() <= getHeight();
        route.setVisible(onScreen && end.getX() > start.getX());
        if (route.isVisible()) route.getTransforms().setAll(fit(start, end));
    }

    /** Uniform scale plus rotation: both painted ends stay on their planet anchors. */
    static Affine fit(Point2D start, Point2D end) {
        Point2D source = END.subtract(START);
        Point2D target = end.subtract(start);
        double divisor = source.dotProduct(source);
        double a = target.dotProduct(source) / divisor;
        double b = (target.getY() * source.getX() - target.getX() * source.getY()) / divisor;
        return new Affine(a, -b, start.getX() - a * START.getX() + b * START.getY(),
                b, a, start.getY() - b * START.getX() - a * START.getY());
    }

    @Override public void close() {
        departure.localToSceneTransformProperty().removeListener(moved);
        destination.localToSceneTransformProperty().removeListener(moved);
        departure.layoutBoundsProperty().removeListener(moved);
        destination.layoutBoundsProperty().removeListener(moved);
        sceneProperty().removeListener(moved);
    }
}

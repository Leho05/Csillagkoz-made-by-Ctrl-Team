package org.csillagkoz.visual;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

/** Layout-only adapter around the original controls, ids and event handlers. */
final class SpaceView extends StackPane implements AutoCloseable {
    private final SpaceBackdrop backdrop;
    private final List<Runnable> detach = new ArrayList<>();
    private TicketPreview ticket;
    private SpaceRoute route;

    SpaceView(Parent original, SpaceTheme theme) {
        getStyleClass().add("space-shell");
        boolean booking = find(original, "departureComboBox", ComboBox.class) != null;
        boolean personal = find(original, "lastNameField", javafx.scene.control.TextField.class) != null;
        backdrop = new SpaceBackdrop(theme.assets, !booking);
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("space-layout");
        layout.setTop(header(theme, booking ? "01  /  UTAZÁS" : personal ? "03  /  UTASADATOK" : "02  /  KABIN"));
        styleOriginal(original);
        ScrollPane form = scroll(original, true);
        form.setId("visualFormScroll");
        form.getStyleClass().add("form-card");
        VBox left = new VBox(14);
        VBox right = new VBox(14);
        left.setAlignment(Pos.CENTER);
        right.setAlignment(Pos.CENTER);
        left.setFillWidth(true);
        right.setFillWidth(true);
        if (booking) {
            setupBooking(original, theme, left, right);
        } else if (personal) {
            ticket = new TicketPreview(original, theme);
            left.getChildren().add(ticket);
            right.getChildren().add(illustration("UTASTÁRSAD", passengerCaption(theme.passengerType),
                    theme.assets.passenger(theme.passengerType), 210, 330));
        } else {
            left.getChildren().addAll(
                    illustration("KIVÁLASZTOTT KABIN", cabinCaption(theme.cabin), theme.assets.image("cabin." + theme.cabin), 230, 155),
                    illustration("ÚTI CÉL", display(theme.destination, "Válassz úti célt"), theme.assets.destination(theme.destination), 135, 135));
            right.getChildren().add(illustration("UTASTÁRSAD", passengerCaption(theme.passengerType),
                    theme.assets.passenger(theme.passengerType), 190, 300));
        }
        ScrollPane leftScroll = scroll(left, false);
        ScrollPane rightScroll = scroll(right, false);
        leftScroll.setId("visualLeftScroll");
        rightScroll.setId("visualRightScroll");
        layout.setCenter(new Workspace(leftScroll, form, rightScroll, route));
        Label footer = new Label(personal ? "A jegyelőnézet a kitöltés közben frissül." : "A következő állomás: egy új világ.");
        footer.getStyleClass().add("footer-copy");
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(6, 16, 12, 16));
        layout.setBottom(footer);
        getChildren().addAll(backdrop, layout);
    }

    private Node header(SpaceTheme theme, String step) {
        Label brand = new Label("CSILLAGKÖZ");
        brand.getStyleClass().add("brand");
        Label subtitle = new Label("TÁVOLABB A MEGSZOKOTTÓL");
        subtitle.getStyleClass().add("brand-subtitle");
        VBox wordmark = new VBox(2, brand, subtitle);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label progress = new Label(step);
        progress.getStyleClass().add("step-label");
        Button pause = new Button();
        pause.setId("visualMotionToggle");
        pause.getStyleClass().add("motion-button");
        ChangeListener<Boolean> caption = (o, a, b) -> pause.setText(b ? "Animációk: be" : "Animációk: szünet");
        theme.motion.addListener(caption);
        detach.add(() -> theme.motion.removeListener(caption));
        caption.changed(theme.motion, false, theme.motion.get());
        pause.setOnAction(event -> theme.motion.set(!theme.motion.get()));
        HBox header = new HBox(20, wordmark, spacer, progress, pause);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.getStyleClass().add("space-header");
        return header;
    }

    @SuppressWarnings("unchecked")
    private void setupBooking(Parent form, SpaceTheme theme, VBox left, VBox right) {
        Illustration departure = illustration("INDULÁS", "Válassz indulási helyet", theme.assets.destination(null), 185, 185);
        Illustration destination = illustration("ÉRKEZÉS", "Melyik világ vár rád?", theme.assets.destination(null), 185, 185);
        Illustration passenger = illustration("UTASTÁRSAD", passengerCaption(theme.passengerType), theme.assets.passenger(theme.passengerType), 108, 150);
        Illustration cabin = illustration("AZ UTAZÁSOD", "Válassz kabinosztályt", theme.assets.image("cabin.ECONOMY"), 205, 125);
        departure.setId("visualDeparture"); destination.setId("visualDestination");
        route = new SpaceRoute(theme.assets.image("route"), departure.image, destination.image);
        passenger.setId("visualPassenger"); cabin.setId("visualCabin");
        left.getChildren().addAll(departure, passenger);
        right.getChildren().addAll(destination, cabin);
        ComboBox<String> from = find(form, "departureComboBox", ComboBox.class);
        ComboBox<String> to = find(form, "destinationComboBox", ComboBox.class);
        ComboBox<String> type = find(form, "passengerTypeComboBox", ComboBox.class);
        observe(from.valueProperty(), value -> {
            theme.departure = value;
            departure.update(theme.assets.destination(value), display(value, "Válassz indulási helyet"));
            updateRoute(theme);
        });
        observe(to.valueProperty(), value -> {
            theme.destination = value;
            destination.update(theme.assets.destination(value), display(value, "Melyik világ vár rád?"));
            updateRoute(theme);
        });
        observe(type.valueProperty(), value -> {
            theme.passengerType = value;
            passenger.update(theme.assets.passenger(value), passengerCaption(value));
        });
        String[][] choices = {{"economyRadioButton", "ECONOMY"}, {"businessRadioButton", "BUSINESS"}, {"firstClassRadioButton", "FIRST_CLASS"}};
        for (String[] choice : choices) {
            RadioButton button = find(form, choice[0], RadioButton.class);
            if (button == null) continue;
            observe(button.selectedProperty(), selected -> {
                if (selected) {
                    theme.cabin = choice[1];
                    cabin.update(theme.assets.image("cabin." + choice[1]), cabinCaption(choice[1]));
                } else if (button.getToggleGroup() != null && button.getToggleGroup().getSelectedToggle() == null) {
                    cabin.update(theme.assets.image("cabin.ECONOMY"), "Válassz kabinosztályt");
                }
            });
        }
    }

    private void updateRoute(SpaceTheme theme) {
        route.setConnected(theme.departure != null && !theme.departure.trim().isEmpty()
                && theme.destination != null && !theme.destination.trim().isEmpty());
    }

    private <T> void observe(ObservableValue<T> property, java.util.function.Consumer<T> update) {
        ChangeListener<T> listener = (o, a, b) -> update.accept(b);
        property.addListener(listener);
        detach.add(() -> property.removeListener(listener));
        update.accept(property.getValue());
    }

    private static String display(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static String passengerCaption(String value) {
        switch (SpaceAssets.passengerKey(value)) {
            case "alien.base": return "Válassz utastípust";
            case "alien.young": return "Fiatal felfedező";
            case "alien.senior": return "Tapasztalt utazó";
            default: return "Felnőtt felfedező";
        }
    }

    private static String cabinCaption(String cabin) {
        if ("BUSINESS".equals(cabin)) return "Business";
        if ("FIRST_CLASS".equals(cabin)) return "First Class";
        return "Economy";
    }

    private static Illustration illustration(String caption, String value, Image image, double width, double height) {
        return new Illustration(caption, value, image, width, height);
    }

    private static ScrollPane scroll(Node content, boolean form) {
        ScrollPane pane = new ScrollPane(content);
        pane.getStyleClass().add("space-scroll");
        pane.setFitToWidth(true);
        pane.setFitToHeight(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setMinSize(0, 0);
        pane.setPannable(false);
        if (content instanceof Region) {
            Region region = (Region) content;
            region.setMinWidth(0);
            region.setMaxWidth(Double.MAX_VALUE);
        }
        return pane;
    }

    private void styleOriginal(Parent original) {
        original.getStyleClass().add("space-form");
        if (original instanceof VBox) {
            VBox box = (VBox) original;
            box.setSpacing(7);
            box.setPadding(new Insets(14, 16, 14, 16));
        }
        styleChildren(original);
        VBox radios = find(original, "radioContainer", VBox.class);
        if (radios != null) {
            observe(radios.styleProperty(), style -> radios.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("visual-error"), style != null && style.contains("#fff0f0")));
        }
    }

    private void styleChildren(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                ((Button) node).setWrapText(true);
                ((Button) node).setMinHeight(Region.USE_PREF_SIZE);
            }
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setMinHeight(Region.USE_PREF_SIZE);
                String id = label.getId();
                if (id != null && id.endsWith("ErrorLabel")) {
                    label.setWrapText(true);
                    label.setMinWidth(0);
                    label.setMaxWidth(170);
                    // Reserve no blank error lines; visibility is still controlled by the original controller.
                    observe(label.visibleProperty(), label::setManaged);
                } else {
                    label.setStyle(label.getStyle().replace("#666666", "#a8b9ce"));
                    label.setWrapText(true);
                    label.setMaxWidth(320);
                }
            }
            if (node instanceof HBox) {
                ((HBox) node).setMinWidth(0);
            }
            if (node instanceof Parent) styleChildren((Parent) node);
        }
    }

    static <T extends Node> T find(Parent root, String id, Class<T> type) {
        if (id.equals(root.getId()) && type.isInstance(root)) return type.cast(root);
        for (Node node : root.getChildrenUnmodifiable()) {
            if (id.equals(node.getId()) && type.isInstance(node)) return type.cast(node);
            if (node instanceof Parent) {
                T found = find((Parent) node, id, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    void setRunning(boolean running) { backdrop.setRunning(running); }

    @Override public void close() {
        backdrop.close();
        detach.forEach(Runnable::run);
        if (ticket != null) ticket.close();
        if (route != null) route.close();
    }

    private static final class Illustration extends VBox {
        private final ImageView image;
        private final Label value;
        private final double fullWidth, fullHeight;
        Illustration(String caption, String value, Image asset, double width, double height) {
            super(6);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(8));
            getStyleClass().add("illustration");
            Label heading = new Label(caption);
            heading.getStyleClass().add("eyebrow");
            this.image = SpaceAssets.view(asset, width, height);
            this.fullWidth = width;
            this.fullHeight = height;
            this.value = new Label(value);
            this.value.getStyleClass().add("illustration-title");
            this.value.setWrapText(true);
            this.value.setAlignment(Pos.CENTER);
            this.value.setMaxWidth(260);
            getChildren().addAll(heading, image, this.value);
            setAccessibleText(caption + ": " + value);
        }
        void update(Image asset, String label) {
            image.setImage(asset);
            value.setText(label);
            setAccessibleText(label);
        }
        void setCompact(boolean compact) {
            image.setFitWidth(compact ? Math.min(fullWidth, 110) : fullWidth);
            image.setFitHeight(compact ? Math.min(fullHeight, 100) : fullHeight);
        }
    }

    /** Three columns at 960 px and above; form above two scrollable previews below 860 px. */
    private static final class Workspace extends Region {
        private final Node left, centre, right;
        private final SpaceRoute route;
        private Boolean compact;
        private double routeWidth = -1, routeHeight = -1, routeInset;
        Workspace(Node left, Node centre, Node right, SpaceRoute route) {
            this.left = left; this.centre = centre; this.right = right;
            this.route = route;
            if (route != null) {
                getChildren().add(route);
                ((VBox) ((ScrollPane) left).getContent()).setAlignment(Pos.TOP_CENTER);
                ((VBox) ((ScrollPane) right).getContent()).setAlignment(Pos.TOP_CENTER);
            }
            getChildren().addAll(left, centre, right);
        }
        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            boolean narrow = w < 860;
            if (compact == null || compact != narrow) {
                compact = narrow;
                compactImages(left, narrow);
                compactImages(right, narrow);
            }
            if (w >= 860) {
                double centreWidth = route == null ? Math.min(440, w * .435)
                        : Math.min(440, Math.max(350, w * .36));
                double sideWidth = (w - centreWidth - 72) / 2;
                left.resizeRelocate(16, 6, sideWidth, h - 12);
                centre.resizeRelocate(sideWidth + 36, 6, centreWidth, h - 12);
                right.resizeRelocate(sideWidth + centreWidth + 56, 6, sideWidth, h - 12);
            } else {
                double formHeight = Math.max(240, h * .64);
                centre.resizeRelocate(16, 4, w - 32, formHeight);
                left.resizeRelocate(16, formHeight + 14, (w - 44) / 2, Math.max(80, h - formHeight - 20));
                right.resizeRelocate(w / 2 + 6, formHeight + 14, (w - 44) / 2, Math.max(80, h - formHeight - 20));
            }
            if (route != null) {
                route.resizeRelocate(0, 0, w, h);
                // Resolve scroll-view content placement before measuring the planets.
                ((ScrollPane) left).layout();
                ((ScrollPane) right).layout();
                if (routeWidth != w || routeHeight != h) {
                    routeInset = narrow ? 0 : Math.max(0, routeInset + route.topCorrection());
                    ((VBox) ((ScrollPane) left).getContent()).setPadding(new Insets(routeInset, 0, 0, 0));
                    ((VBox) ((ScrollPane) right).getContent()).setPadding(new Insets(routeInset, 0, 0, 0));
                    ((ScrollPane) left).layout();
                    ((ScrollPane) right).layout();
                    routeWidth = w; routeHeight = h;
                }
                route.requestLayout();
                route.layout();
                if (!narrow) {
                    double centreWidth = Math.min(440, Math.max(350, w * .36));
                    double centreX = (w - centreWidth) / 2;
                    double top = Math.min(Math.max(6, h - 220), route.formTop(centreX, centreWidth));
                    centre.resizeRelocate(centreX, top, centreWidth, Math.max(180, h - top - 6));
                }
            }
        }
        @Override protected double computeMinWidth(double height) { return 0; }
        @Override protected double computeMinHeight(double width) { return 0; }
        private static void compactImages(Node node, boolean compact) {
            if (node instanceof Illustration) ((Illustration) node).setCompact(compact);
            else if (node instanceof ScrollPane) compactImages(((ScrollPane) node).getContent(), compact);
            else if (node instanceof Parent) {
                for (Node child : ((Parent) node).getChildrenUnmodifiable()) compactImages(child, compact);
            }
        }
    }
}

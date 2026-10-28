package org.csillagkoz.visual;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

/** Display-only live preview. No persistence, business calls or personal-data logging. */
final class TicketPreview extends VBox implements AutoCloseable {
    private final List<Label> boundLabels = new ArrayList<>();

    TicketPreview(Parent form, SpaceTheme theme) {
        super(8);
        setId("visualTicketPreview");
        setPadding(new Insets(20));
        getStyleClass().add("ticket-preview");
        Label title = new Label("ŰRJEGY");
        title.getStyleClass().add("ticket-title");
        Label status = new Label("ÉLŐ ELŐNÉZET");
        status.getStyleClass().add("eyebrow");
        Label route = new Label(text(theme.departure, "Indulás") + "\n↓\n" + text(theme.destination, "Úti cél"));
        route.setWrapText(true);
        route.getStyleClass().add("ticket-route");
        getChildren().addAll(status, title, route);
        field("VEZETÉKNÉV", form, "lastNameField", "Vezetéknév");
        field("KERESZTNÉV", form, "firstNameField", "Keresztnév");
        DatePicker birthday = SpaceView.find(form, "birthDatePicker", DatePicker.class);
        Label birthValue = line("SZÜLETÉSI DÁTUM", "ÉÉÉÉ. HH. NN.");
        if (birthday != null) {
            birthValue.textProperty().bind(Bindings.createStringBinding(() -> {
                String editor = birthday.getEditor().getText();
                return text(editor, "ÉÉÉÉ. HH. NN.");
            }, birthday.valueProperty(), birthday.getEditor().textProperty()));
        }
        field("OKMÁNYSZÁM", form, "passportField", "Okmányszám");
        field("E-MAIL", form, "emailField", "E-mail-cím");
        field("TELEFONSZÁM", form, "phoneField", "Telefonszám");
        HBox bars = new HBox(3);
        bars.setPadding(new Insets(10, 0, 0, 0));
        bars.setMouseTransparent(true);
        int[] widths = {2, 5, 2, 3, 6, 2, 4, 2, 5, 3, 2, 6, 3, 5, 2, 3, 5, 2, 4, 2};
        for (int width : widths) bars.getChildren().add(new Rectangle(width, 20, Color.web("#69d6e6", .58)));
        Label note = new Label("Előnézet • még nem érvényes jegy");
        note.setWrapText(true);
        note.getStyleClass().add("ticket-note");
        getChildren().addAll(bars, note);
    }

    private static String text(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private void field(String caption, Parent form, String id, String placeholder) {
        TextField field = SpaceView.find(form, id, TextField.class);
        Label value = line(caption, placeholder);
        if (field != null) value.textProperty().bind(Bindings.createStringBinding(
                () -> text(field.getText(), placeholder), field.textProperty()));
    }

    private Label line(String caption, String placeholder) {
        Label heading = new Label(caption);
        heading.getStyleClass().add("ticket-caption");
        Label value = new Label(placeholder);
        value.setWrapText(true);
        value.setMaxWidth(Double.MAX_VALUE);
        value.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        value.getStyleClass().add("ticket-value");
        VBox row = new VBox(2, heading, value);
        getChildren().add(row);
        boundLabels.add(value);
        return value;
    }

    @Override public void close() { boundLabels.forEach(label -> label.textProperty().unbind()); }
}

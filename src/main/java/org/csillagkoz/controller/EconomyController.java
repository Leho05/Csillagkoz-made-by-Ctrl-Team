package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;

public class EconomyController implements ModelAware {
    private BookingModel model;

    @FXML private GridPane seatGrid;
    @FXML private Label selectedSeatLabel;
    @FXML private Label errorLabel;
    @FXML private Button errorReportButton;

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

    @Override
    public void setModel(BookingModel model) {
        this.model = model;
        SeatSelectionHelper.setupSeats(seatGrid, selectedSeatLabel, errorLabel, model, "ECONOMY", 6, 4);
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
    }

    @FXML
    private void handleNext() throws IOException {
        if (model == null || model.getSelectedSeat() == null) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }
        model.setPreviousView("economy-seats");
        App.setRoot("personal-data", "Csillagköz - Személyes adatok");
    }
}
package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;

public class FirstClassController implements ModelAware {
    private BookingModel model;

    @FXML
    private Button errorReportButton;

    @Override
    public void setModel(BookingModel model) {
        this.model = model;
    }

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
    }

    @FXML
    private void handleNext() throws IOException {
        if (model != null) {
            model.setPreviousView("firstclass-seats");
        }
        App.setRoot("personal-data", "Csillagköz - Személyes adatok");
    }
}
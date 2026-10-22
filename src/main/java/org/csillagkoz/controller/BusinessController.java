package org.csillagkoz.controller;

import javafx.fxml.FXML;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;

public class BusinessController implements ModelAware {
    private BookingModel model;

    @Override
    public void setModel(BookingModel model) {
        this.model = model;
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
    }

    @FXML
    private void handleNext() throws IOException {
        if (model != null) {
            model.setPreviousView("business-seats");
        }
        App.setRoot("personal-data", "Csillagköz - Személyes adatok");
    }
}
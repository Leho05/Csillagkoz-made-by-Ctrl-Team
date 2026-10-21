package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

public class PrimaryController {

    @FXML
    private DatePicker flightDatePicker;

    public void initialize() {
        if (flightDatePicker != null) {
            flightDatePicker.setValue(LocalDate.now());
        }
    }
}
package org.csillagkoz.controller;

import javafx.fxml.FXML;
import org.csillagkoz.App;
import java.io.IOException;

public class EconomyController {
    @FXML
    private void handleBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void handleNext() throws IOException {
        PersonalDataController.setPreviousView("economy-seats");
        App.setRoot("personal-data");
    }
}
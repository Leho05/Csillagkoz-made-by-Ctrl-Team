package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.csillagkoz.App;
import java.io.IOException;

public class PersonalDataController {

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("primary");
    }
}
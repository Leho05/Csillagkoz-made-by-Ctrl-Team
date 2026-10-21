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
    private TextField passportField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void handleFinalize() {
        System.out.println("Foglalás sikeresen véglegesítve!");
        System.out.println("Név: " + (lastNameField != null ? lastNameField.getText() : "") + " " + (firstNameField != null ? firstNameField.getText() : ""));
        System.out.println("E-mail: " + (emailField != null ? emailField.getText() : ""));
        System.out.println("Telefon: " + (phoneField != null ? phoneField.getText() : ""));
    }
}
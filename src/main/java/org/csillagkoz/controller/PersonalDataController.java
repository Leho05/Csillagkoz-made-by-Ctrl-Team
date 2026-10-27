package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PersonalDataController implements ModelAware {

    private BookingModel model;
    private String selectedIdType = "PASSPORT"; // Alapértelmezett az útlevél

    @Override
    public void setModel(BookingModel model) {
        this.model = model;

        if (model != null && model.getLastName() != null) {
            if (lastNameField != null) lastNameField.setText(model.getLastName());
            if (firstNameField != null) firstNameField.setText(model.getFirstName());
            if (birthDatePicker != null) birthDatePicker.setValue(model.getBirthDate());
            if (passportField != null) passportField.setText(model.getPassportNumber());
            if (emailField != null) emailField.setText(model.getEmail());
            if (phoneField != null) phoneField.setText(model.getPhone());
        }
    }

    @FXML
    private TextField lastNameField;
    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private TextField firstNameField;
    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Label birthDateErrorLabel;

    @FXML
    private Button passportTypeBtn;
    @FXML
    private Button idCardTypeBtn;
    @FXML
    private TextField passportField;
    @FXML
    private Label passportErrorLabel;

    @FXML
    private TextField emailField;
    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField phoneField;
    @FXML
    private Label phoneErrorLabel;

    @FXML
    private void handleSelectPassport() {
        selectedIdType = "PASSPORT";
        passportTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        idCardTypeBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
        passportField.setPromptText("Pl. AB123456 (2 betű, 6 szám)");
    }

    @FXML
    private void handleSelectIdCard() {
        selectedIdType = "IDCARD";
        idCardTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        passportTypeBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
        passportField.setPromptText("Pl. 123456AB (6 szám, 2 betű)");
    }

    private void saveInputToModel() {
        if (model != null) {
            if (lastNameField != null && lastNameField.getText() != null) {
                model.setLastName(lastNameField.getText().trim());
            }
            if (firstNameField != null && firstNameField.getText() != null) {
                model.setFirstName(firstNameField.getText().trim());
            }
            if (birthDatePicker != null) {
                model.setBirthDate(birthDatePicker.getValue());
            }
            if (passportField != null && passportField.getText() != null) {
                model.setPassportNumber(passportField.getText().trim().toUpperCase());
            }
            if (emailField != null && emailField.getText() != null) {
                model.setEmail(emailField.getText().trim());
            }
            if (phoneField != null && phoneField.getText() != null) {
                model.setPhone(phoneField.getText().trim());
            }
        }
    }

    @FXML
    private void handleBack() throws IOException {
        saveInputToModel();
        String targetView;
        String targetTitle;

        if (model != null && model.getPreviousView() != null) {
            targetView = model.getPreviousView();
        } else {
            targetView = "ticket-booking";
        }

        if ("economy-seats".equals(targetView)) {
            targetTitle = "Csillagköz - Harmad osztály";
        } else if ("business-seats".equals(targetView)) {
            targetTitle = "Csillagköz - Másod osztály";
        } else if ("firstclass-seats".equals(targetView)) {
            targetTitle = "Csillagköz - Első osztály";
        } else {
            targetTitle = "Csillagköz - Jegyfoglalás";
        }

        App.setRoot(targetView, targetTitle);
    }

    @FXML
    private void handleFinalize() {
        // Logika a következő lépésekben teljesedik ki
    }
}
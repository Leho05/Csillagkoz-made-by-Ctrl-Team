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

        // Ha a modellben már el vannak mentve adatok (pl. visszalépett majd újra idejött), automatikusan kitöltjük a mezőket:
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

    private void clearErrors() {
        String defaultStyle = "";
        lastNameField.setStyle(defaultStyle);
        lastNameErrorLabel.setVisible(false);

        firstNameField.setStyle(defaultStyle);
        firstNameErrorLabel.setVisible(false);

        birthDatePicker.setStyle(defaultStyle);
        birthDateErrorLabel.setVisible(false);

        passportField.setStyle(defaultStyle);
        passportErrorLabel.setVisible(false);

        emailField.setStyle(defaultStyle);
        emailErrorLabel.setVisible(false);

        phoneField.setStyle(defaultStyle);
        phoneErrorLabel.setVisible(false);
    }

    private void setError(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: #ff4d4d; -fx-background-color: #fff0f0;");
        label.setText(message);
        label.setVisible(true);
    }

    private void setDatePickerError(DatePicker datePicker, Label label, String message) {
        datePicker.setStyle("-fx-border-color: #ff4d4d; -fx-background-color: #fff0f0;");
        label.setText(message);
        label.setVisible(true);
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
        clearErrors();
        boolean hasEmptyError = false;

        // 1. Ellenőrzés: Üresek-e a mezők?
        if (lastNameField == null || lastNameField.getText().trim().isEmpty()) {
            setError(lastNameField, lastNameErrorLabel, "* A mező kitöltése kötelező");
            hasEmptyError = true;
        }
        if (firstNameField == null || firstNameField.getText().trim().isEmpty()) {
            setError(firstNameField, firstNameErrorLabel, "* A mező kitöltése kötelező");
            hasEmptyError = true;
        }
        if (birthDatePicker == null || birthDatePicker.getValue() == null) {
            setDatePickerError(birthDatePicker, birthDateErrorLabel, "* A születési dátum megadása kötelező");
            hasEmptyError = true;
        }
        if (passportField == null || passportField.getText().trim().isEmpty()) {
            setError(passportField, passportErrorLabel, "* A mező kitöltése kötelező");
            hasEmptyError = true;
        }
        if (emailField == null || emailField.getText().trim().isEmpty()) {
            setError(emailField, emailErrorLabel, "* A mező kitöltése kötelező");
            hasEmptyError = true;
        }
        if (phoneField == null || phoneField.getText().trim().isEmpty()) {
            setError(phoneField, phoneErrorLabel, "* A mező kitöltése kötelező");
            hasEmptyError = true;
        }

        if (hasEmptyError) {
            System.out.println("Hiba: Kérjük, töltsön ki minden mezőt a továbblépéshez!");
            // TODO szebb hiba kiírás
            return;
        }

        // 2. Ellenőrzés: Elmúlt-e 18 éves?
        LocalDate birthDate = birthDatePicker.getValue();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18) {
            setDatePickerError(birthDatePicker, birthDateErrorLabel, "Hiba: Csak 18 év felett!");
            System.out.println("Hiba: A jegyvásárló nem múlt el 18 éves, a foglalás nem véglegesíthető!");
            // TODO szebb hiba kiírás
            return;
        }

        //Sikeres validációnál mentjük a modellbe
        saveInputToModel();

        //Adatok megjelenítése a képernyőn
        System.out.println("A jegy vásárlása sikeresen megtörtént!");
        if (model != null) {
            System.out.println("Utas: " + model.getLastName() + " " + model.getFirstName());
            System.out.println("E-mail: " + model.getEmail());
            System.out.println("Telefon szám: " + model.getPhone());
            System.out.println("Úti cél: " + model.getDestination() + " (" + model.getSelectedClass() + ")");
        }

        // GUI ablak bezárása
        Stage stage = (Stage) lastNameField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
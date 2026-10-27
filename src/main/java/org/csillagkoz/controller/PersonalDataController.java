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
        // 1. Ellenőrzés: Üresek-e a mezők?
        if (lastNameField == null || lastNameField.getText().trim().isEmpty() ||
                firstNameField == null || firstNameField.getText().trim().isEmpty() ||
                birthDatePicker == null || birthDatePicker.getValue() == null ||
                passportField == null || passportField.getText().trim().isEmpty() ||
                emailField == null || emailField.getText().trim().isEmpty() ||
                phoneField == null || phoneField.getText().trim().isEmpty()) {

            System.out.println("Hiba: Kérjük, töltsön ki minden mezőt a továbblépéshez!");
            // TODO szebb hiba kiírás
            return;
        }

        // 2. Ellenőrzés: Elmúlt-e 18 éves?
        LocalDate birthDate = birthDatePicker.getValue();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18) {
            System.out.println("Hiba: A jegyvásárló nem múlt el 18 éves, a foglalás nem véglegesíthető!");
            // TODO szebb hiba kiírás
            return;
        }

        // 3. Ellenőrzés: E-mail cím formátuma (pontosan egy @ karakter)
        String email = emailField.getText().trim();
        long atCount = email.chars().filter(ch -> ch == '@').count();
        if (atCount != 1 || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("Hibás e-mail! Kérjük, érvényes e-mail címet adjon meg (pl. nev@domain.com)!");
            // TODO szebb hiba kiírás
            return;
        }

        // 4. Ellenőrzés: Telefonszám (+CC... vagy 06...)
        String phone = phoneField.getText().trim();

        if (!phone.matches("^(\\+\\d{8,15}|0\\d{8,14})$")) {
            System.out.println("Hiba: Érvénytelen telefonszám! Elvárt formátum: nemzetközi (+ és országhívó) vagy belföldi (0-val kezdődő) számjegyek.");
            // TODO szebb hiba kiírás
            return;
        }

        // 5. Ellenőrzés: Útlevél vagy Személyi igazolvány kiválasztott típus szerint
        String passport = passportField.getText().trim().toUpperCase();
        boolean isValidId = false;
        if ("PASSPORT".equals(selectedIdType)) {
            // Útlevél: 2 betű elöl, utána 6 számjegy (pl. AB123456)
            isValidId = passport.matches("^[A-Z]{2}[0-9]{6}$");
        } else {
            // Személyi igazolvány: 6 számjegy elöl, utána 2 betű (pl. 123456AB)
            isValidId = passport.matches("^[0-9]{6}[A-Z]{2}$");
        }

        if (!isValidId) {
            System.out.println("Hiba: Érvénytelen okmányszám a kiválasztott típushoz!");
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
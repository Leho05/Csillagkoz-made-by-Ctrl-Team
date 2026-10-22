package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PersonalDataController implements ModelAware {

    private BookingModel model;

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
    private TextField firstNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField passportField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

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
                model.setPassportNumber(passportField.getText().trim());
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

        // 3. Ellenőrzés: E-mail cím formátuma
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Hiba: Érvénytelen e-mail cím formatum!");
            // TODO szebb hiba kiírás
            return;
        }

        // 4. Ellenőrzés: Telefonszám (+CC... vagy 06...)
        String phone = phoneField.getText().trim();

        if (!phone.matches("^(\\+\\d{8,15}|06\\d{9})$")) {
            System.out.println("Hiba: Érvénytelen telefonszám! Elvárt formátum: nemzetközi (+ és országhívó) vagy belföldi (06...), szóközök és kötőjelek nélkül.\"");
            // TODO szebb hiba kiírás
            return;
        }

        // 5. Ellenőrzés: Útlevél / Személyi igazolvány száma (minimum 6 karakter)
        String passport = passportField.getText().trim();
        if (passport.length() < 8) {
            System.out.println("Hiba: Érvénytelen útlevél/személyi szám! Minimum 6 karakter szükséges.");
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
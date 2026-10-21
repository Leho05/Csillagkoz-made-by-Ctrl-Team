package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.csillagkoz.App;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PersonalDataController {

    // --- Tároljuk, hogy melyik kabinból érkeztünk ---
    private static String previousView = "primary";

    public static void setPreviousView(String viewName) {
        previousView = viewName;
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

    @FXML
    private void handleBack() throws IOException {
        // Dinamikusan arra az oldalra megy vissza, ahonnan jöttünk
        App.setRoot(previousView);
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
            return;
        }

        // 2. Ellenőrzés: Elmúlt-e 18 éves?
        LocalDate birthDate = birthDatePicker.getValue();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18) {
            System.out.println("Hiba: A jegyvásárló nem múlt el 18 éves, a foglalás nem véglegesíthető!");
            return;
        }

        // Ha minden feltétel teljesül:
        System.out.println("Foglalás sikeresen véglegesítve!");
        System.out.println("Név: " + lastNameField.getText() + " " + firstNameField.getText());
        System.out.println("E-mail: " + emailField.getText());
        System.out.println("Telefon: " + phoneField.getText());
    }
}
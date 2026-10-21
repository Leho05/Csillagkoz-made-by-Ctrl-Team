package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.csillagkoz.App;

import java.io.IOException;
import java.time.LocalDate;

public class PrimaryController {

    @FXML
    private DatePicker flightDatePicker;

    // --- A rádiógombok által más-más a kiválasztott osztálynak megfelelő ablak megnyitása ---
    @FXML
    private ToggleGroup classToggleGroup;

    @FXML
    private RadioButton economyRadioButton;

    @FXML
    private RadioButton businessRadioButton;

    @FXML
    private RadioButton firstClassRadioButton;

    public void initialize() {
        if (flightDatePicker != null) {
            flightDatePicker.setValue(LocalDate.now());
        }
    }

    // --- A mégse gomb létrehozása ---
    @FXML
    private void handleCancel() {
        // Itt alaphelyzetbe állíthatjuk a mezőket vagy kezelhetjük a kilépést
        System.out.println("Mégse gomb megnyomva.");
    }

    // --- Új eseménykezelő a Járatok keresése gombhoz ---
    @FXML
    private void handleSearchFlights() throws IOException {
        // Ellenőrizzük, hogy melyik kabin osztály lett kiválasztva
        if (economyRadioButton != null && economyRadioButton.isSelected()) {
            App.setRoot("economy-seats");
        } else if (businessRadioButton != null && businessRadioButton.isSelected()) {
            App.setRoot("business-seats");
        } else if (firstClassRadioButton != null && firstClassRadioButton.isSelected()) {
            App.setRoot("firstclass-seats");
        } else {
            // Ha nincsen kiválasztva kabin osztály
            System.out.println("Kérjük, válasszon kabin osztályt a továbblépéshez!");
        }
    }
}
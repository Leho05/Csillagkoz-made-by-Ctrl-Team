package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import org.csillagkoz.App;

import java.io.IOException;
import java.time.LocalDate;

public class TicketBookingController {

    @FXML
    private ComboBox<String> departureComboBox;

    @FXML
    private ComboBox<String> destinationComboBox;

    @FXML
    private ComboBox<String> passengerTypeComboBox;

    @FXML
    private DatePicker flightDatePicker;

    // --- A rádiógombok és a csoport ---
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

            // --- Korábbi napok letiltása a naptárban ---
            flightDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(DatePicker param) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isBefore(LocalDate.now())) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffcccc;"); // Opcionális: halvány piros háttér a múltbeli napoknak
                            }
                        }
                    };
                }
            });
        }
    }

    // --- A Mégse gomb: mindent alaphelyzetbe/üresre állít ---
    @FXML
    private void handleCancel() {
        if (departureComboBox != null) {
            departureComboBox.getSelectionModel().clearSelection();
            departureComboBox.setValue(null);
        }
        if (destinationComboBox != null) {
            destinationComboBox.getSelectionModel().clearSelection();
            destinationComboBox.setValue(null);
        }
        if (passengerTypeComboBox != null) {
            passengerTypeComboBox.getSelectionModel().clearSelection();
            passengerTypeComboBox.setValue(null);
        }
        if (flightDatePicker != null) {
            flightDatePicker.setValue(LocalDate.now()); // Vagy null, ha teljesen üresen szeretnéd: flightDatePicker.setValue(null);
        }
        if (classToggleGroup != null && classToggleGroup.getSelectedToggle() != null) {
            classToggleGroup.getSelectedToggle().setSelected(false);
        }

        System.out.println("Mezők alaphelyzetbe állítva."); // Megjegyzés: a System.out-hoz igazítva
    }

    // --- Járatok keresése gomb eseménykezelője ---
    @FXML
    private void handleSearchFlights() throws IOException {
        if (economyRadioButton != null && economyRadioButton.isSelected()) {
            App.setRoot("economy-seats");
        } else if (businessRadioButton != null && businessRadioButton.isSelected()) {
            App.setRoot("business-seats");
        } else if (firstClassRadioButton != null && firstClassRadioButton.isSelected()) {
            App.setRoot("firstclass-seats");
        } else {
            System.out.println("Kérjük, válasszon kabin osztályt a továbblépéshez!");
        }
    }
}
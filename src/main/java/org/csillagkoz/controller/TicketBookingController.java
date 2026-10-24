package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import org.csillagkoz.App;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.model.Flight;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

    private final FlightDao flightDao = new FlightDao();

    public void initialize() {
        // --- ComboBoxok feltöltése ---
        if (departureComboBox != null) {
            departureComboBox.getItems().addAll("Föld (Terra Központ)", "Hold (Luna Bázis)", "Mars Kolónia Alpha");
        }

        if (passengerTypeComboBox != null) {
            passengerTypeComboBox.getItems().addAll("Teljes árú (Felnőtt)", "Gyermek / Diák", "Nyugdíjas");
        }

        if (destinationComboBox != null) {
            try {
                List<Flight> flights = flightDao.getAllFlights();
                for (Flight flight : flights) {
                    String destination = flight.getDestination().getName();
                    if (!destinationComboBox.getItems().contains(destination)) {
                        destinationComboBox.getItems().add(destination);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Nem sikerült lekérni a célállomásokat: " + e.getMessage());
            }
        }

        // --- Korábbi napok letiltása a naptárban ---
        if (flightDatePicker != null) {
            flightDatePicker.setValue(LocalDate.now());

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
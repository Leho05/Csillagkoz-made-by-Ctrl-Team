package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;
import java.time.LocalDate;

public class TicketBookingController implements ModelAware {

    private BookingModel model;

    @Override
    public void setModel(BookingModel model) {
        this.model = model;

        if (model != null) {
            // Legördülő listák és dátumválasztó értékének visszatöltése
            if (departureComboBox != null && model.getDeparture() != null) {
                departureComboBox.setValue(model.getDeparture());
            }
            if (destinationComboBox != null && model.getDestination() != null) {
                destinationComboBox.setValue(model.getDestination());
            }
            if (flightDatePicker != null && model.getFlightDate() != null) {
                flightDatePicker.setValue(model.getFlightDate());
            }
            if (passengerTypeComboBox != null && model.getPassengerType() != null) {
                passengerTypeComboBox.setValue(model.getPassengerType());
            }

            //  Jegy tipus visszatöltése
            if (model.getSelectedClass() != null) {
                if ("ECONOMY".equals(model.getSelectedClass())) {
                    if (economyRadioButton != null) {
                        economyRadioButton.setSelected(true);
                    }
                } else if ("BUSINESS".equals(model.getSelectedClass())) {
                    if (businessRadioButton != null) {
                        businessRadioButton.setSelected(true);
                    }
                } else if ("FIRST_CLASS".equals(model.getSelectedClass())) {
                    if (firstClassRadioButton != null) {
                        firstClassRadioButton.setSelected(true);
                    }
                }
            }
        }
    }

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
        if (model != null) {
            if (departureComboBox != null) model.setDeparture(departureComboBox.getValue());
            if (destinationComboBox != null) model.setDestination(destinationComboBox.getValue());
            if (flightDatePicker != null) model.setFlightDate(flightDatePicker.getValue());
            if (passengerTypeComboBox != null) model.setPassengerType(passengerTypeComboBox.getValue());
        }

        if (economyRadioButton != null && economyRadioButton.isSelected()) {
            if (model != null) model.setSelectedClass("ECONOMY");
            App.setRoot("economy-seats", "Csillagköz - Harmad osztály");
        } else if (businessRadioButton != null && businessRadioButton.isSelected()) {
            if (model != null) model.setSelectedClass("BUSINESS");
            App.setRoot("business-seats", "Csillagköz - Másod osztály");
        } else if (firstClassRadioButton != null && firstClassRadioButton.isSelected()) {
            if (model != null) model.setSelectedClass("FIRST_CLASS");
            App.setRoot("firstclass-seats", "Csillagköz - Első osztály");
        } else {
            System.out.println("Kérjük, válasszon kabin osztályt a továbblépéshez!");
            // TODO szebb hiba kiírás
        }
    }
}
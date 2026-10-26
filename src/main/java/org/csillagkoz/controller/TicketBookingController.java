package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.model.Flight;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

    // --- Hibaüzenet címkék FXML kötései ---
    @FXML
    private Label departureErrorLabel;

    @FXML
    private Label destinationErrorLabel;

    @FXML
    private Label dateErrorLabel;

    @FXML
    private Label passengerErrorLabel;

    @FXML
    private Label classErrorLabel;

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
            departureComboBox.setStyle("");
        }
        if (departureErrorLabel != null) {
            departureErrorLabel.setVisible(false);
            departureErrorLabel.setManaged(false);
        }

        if (destinationComboBox != null) {
            destinationComboBox.getSelectionModel().clearSelection();
            destinationComboBox.setValue(null);
            destinationComboBox.setStyle("");
        }
        if (destinationErrorLabel != null) {
            destinationErrorLabel.setVisible(false);
            destinationErrorLabel.setManaged(false);
        }

        if (passengerTypeComboBox != null) {
            passengerTypeComboBox.getSelectionModel().clearSelection();
            passengerTypeComboBox.setValue(null);
            passengerTypeComboBox.setStyle("");
        }
        if (passengerErrorLabel != null) {
            passengerErrorLabel.setVisible(false);
            passengerErrorLabel.setManaged(false);
        }

        if (flightDatePicker != null) {
            flightDatePicker.setValue(LocalDate.now()); // Vagy null, ha teljesen üresen szeretnéd: flightDatePicker.setValue(null);
            flightDatePicker.setStyle("");
        }
        if (dateErrorLabel != null) {
            dateErrorLabel.setVisible(false);
            dateErrorLabel.setManaged(false);
        }

        if (classToggleGroup != null && classToggleGroup.getSelectedToggle() != null) {
            classToggleGroup.getSelectedToggle().setSelected(false);
        }
        if (classErrorLabel != null) {
            classErrorLabel.setVisible(false);
            classErrorLabel.setManaged(false);
        }

        System.out.println("Mezők alaphelyzetbe állítva."); // Megjegyzés: a System.out-hoz igazítva
    }

    // --- Járatok keresése gomb eseménykezelője ---
    @FXML
    private void handleSearchFlights() throws IOException {
        boolean isValid = true;

        // --- Mezők validálása a továbblépés előtt ---
        if (departureComboBox == null || departureComboBox.getValue() == null || departureComboBox.getValue().trim().isEmpty()) {
            if (departureComboBox != null) {
                departureComboBox.setStyle("-fx-background-color: #fff0f0; -fx-border-color: #ff4d4d; -fx-border-width: 1.5px; -fx-border-radius: 4px;");
            }
            if (departureErrorLabel != null) {
                departureErrorLabel.setVisible(true);
                departureErrorLabel.setManaged(true);
            }
            isValid = false;
        } else {
            departureComboBox.setStyle("");
            if (departureErrorLabel != null) {
                departureErrorLabel.setVisible(false);
                departureErrorLabel.setManaged(false);
            }
        }

        if (destinationComboBox == null || destinationComboBox.getValue() == null || destinationComboBox.getValue().trim().isEmpty()) {
            if (destinationComboBox != null) {
                destinationComboBox.setStyle("-fx-background-color: #fff0f0; -fx-border-color: #ff4d4d; -fx-border-width: 1.5px; -fx-border-radius: 4px;");
            }
            if (destinationErrorLabel != null) {
                destinationErrorLabel.setVisible(true);
                destinationErrorLabel.setManaged(true);
            }
            isValid = false;
        } else {
            destinationComboBox.setStyle("");
            if (destinationErrorLabel != null) {
                destinationErrorLabel.setVisible(false);
                destinationErrorLabel.setManaged(false);
            }
        }

        if (flightDatePicker == null || flightDatePicker.getValue() == null) {
            if (flightDatePicker != null) {
                flightDatePicker.setStyle("-fx-background-color: #fff0f0; -fx-border-color: #ff4d4d; -fx-border-width: 1.5px; -fx-border-radius: 4px;");
            }
            if (dateErrorLabel != null) {
                dateErrorLabel.setVisible(true);
                dateErrorLabel.setManaged(true);
            }
            isValid = false;
        } else {
            flightDatePicker.setStyle("");
            if (dateErrorLabel != null) {
                dateErrorLabel.setVisible(false);
                dateErrorLabel.setManaged(false);
            }
        }

        if (passengerTypeComboBox == null || passengerTypeComboBox.getValue() == null || passengerTypeComboBox.getValue().trim().isEmpty()) {
            if (passengerTypeComboBox != null) {
                passengerTypeComboBox.setStyle("-fx-background-color: #fff0f0; -fx-border-color: #ff4d4d; -fx-border-width: 1.5px; -fx-border-radius: 4px;");
            }
            if (passengerErrorLabel != null) {
                passengerErrorLabel.setVisible(true);
                passengerErrorLabel.setManaged(true);
            }
            isValid = false;
        } else {
            passengerTypeComboBox.setStyle("");
            if (passengerErrorLabel != null) {
                passengerErrorLabel.setVisible(false);
                passengerErrorLabel.setManaged(false);
            }
        }

        if (classToggleGroup == null || classToggleGroup.getSelectedToggle() == null) {
            if (classErrorLabel != null) {
                classErrorLabel.setVisible(true);
                classErrorLabel.setManaged(true);
            }
            isValid = false;
        } else {
            if (classErrorLabel != null) {
                classErrorLabel.setVisible(false);
                classErrorLabel.setManaged(false);
            }
        }

        if (!isValid) {
            return;
        }

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
        }
    }
}
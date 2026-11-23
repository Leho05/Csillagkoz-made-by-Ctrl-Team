package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.csillagkoz.App;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.database.TicketDao;
import org.csillagkoz.model.Flight;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private Button errorReportButton;
    @FXML private ListView<String> bookingsListView;
    @FXML private ListView<String> flightsListView;
    @FXML private Label statsLabel;

    private final TicketDao ticketDao = new TicketDao();
    private final FlightDao flightDao = new FlightDao();

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

    @FXML
    public void initialize() {
        loadAdminData();
    }

    private void loadAdminData() {
        // 1. Lefoglalt jegyek betöltése
        bookingsListView.getItems().clear();
        List<String> bookings = ticketDao.getAllBookingsSummary();
        if (bookings.isEmpty()) {
            bookingsListView.getItems().add("Jelenleg nincs egyetlen rögzített jegyfoglalás sem az adatbázisban.");
        } else {
            bookingsListView.getItems().addAll(bookings);
        }

        // 2. Járatok és szabad helyek betöltése (SQLException kezeléssel)
        flightsListView.getItems().clear();
        int flightCount = 0;
        try {
            List<Flight> flights = flightDao.getAllFlights();
            flightCount = flights.size();
            for (Flight f : flights) {
                flightsListView.getItems().add(f.toString());
            }
        } catch (SQLException e) {
            flightsListView.getItems().add("Hiba a járatok betöltésekor: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Rövid statisztika kiírása
        statsLabel.setText("|  Aktív foglalások száma: " + bookings.size() + " db  |  Járatok száma: " + flightCount + " db");
    }

    @FXML
    private void handleRefresh() {
        loadAdminData();
    }

    @FXML
    private void handleBack() {
        try {
            App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
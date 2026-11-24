package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.csillagkoz.App;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.database.TicketDao;
import org.csillagkoz.database.UserDao;
import org.csillagkoz.model.Flight;
import org.csillagkoz.model.User;
import org.csillagkoz.service.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminController {

    private enum SelectionType {
        NONE, TICKET, FLIGHT, USER
    }

    @FXML private Button errorReportButton;
    @FXML private Button deleteButton;
    @FXML private ListView<String> bookingsListView;
    @FXML private ListView<String> flightsListView;
    @FXML private ListView<String> usersListView;
    @FXML private Label statsLabel;

    private final TicketDao ticketDao = new TicketDao();
    private final FlightDao flightDao = new FlightDao();
    private final UserDao userDao = new UserDao();

    private SelectionType activeSelection = SelectionType.NONE;
    private boolean updatingSelection = false;

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

    @FXML
    public void initialize() {
        setupSelectionListeners();
        loadAdminData();
    }

    private void setupSelectionListeners() {
        if (bookingsListView != null) {
            bookingsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (!updatingSelection && newVal != null) {
                    selectCategory(SelectionType.TICKET);
                }
            });
        }
        if (flightsListView != null) {
            flightsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (!updatingSelection && newVal != null) {
                    selectCategory(SelectionType.FLIGHT);
                }
            });
        }
        if (usersListView != null) {
            usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (!updatingSelection && newVal != null) {
                    selectCategory(SelectionType.USER);
                }
            });
        }
    }

    private void selectCategory(SelectionType type) {
        updatingSelection = true;
        try {
            activeSelection = type;
            if (type != SelectionType.TICKET && bookingsListView != null) bookingsListView.getSelectionModel().clearSelection();
            if (type != SelectionType.FLIGHT && flightsListView != null) flightsListView.getSelectionModel().clearSelection();
            if (type != SelectionType.USER && usersListView != null) usersListView.getSelectionModel().clearSelection();
            updateDeleteButtonText();
        } finally {
            updatingSelection = false;
        }
    }

    private void updateDeleteButtonText() {
        if (deleteButton == null) return;
        switch (activeSelection) {
            case TICKET:
                deleteButton.setText("Kijelölt jegy törlése");
                break;
            case FLIGHT:
                deleteButton.setText("Kijelölt járat törlése");
                break;
            case USER:
                deleteButton.setText("Kijelölt fiók törlése");
                break;
            default:
                deleteButton.setText("Kijelölt elem törlése");
                break;
        }
    }

    private void loadAdminData() {
        selectCategory(SelectionType.NONE);

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

        // 3. Felhasználói fiókok betöltése
        if (usersListView != null) {
            usersListView.getItems().clear();
            try {
                List<User> users = userDao.getAllUsers();
                for (User u : users) {
                    usersListView.getItems().add(u.getUsername() + " (" + u.getRole() + ")");
                }
            } catch (SQLException e) {
                usersListView.getItems().add("Hiba a fiókok betöltésekor: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 4. Rövid statisztika kiírása
        statsLabel.setText("|  Aktív foglalások száma: " + bookings.size() + " db  |  Járatok száma: " + flightCount + " db");
    }

    @FXML
    private void handleDeleteSelected() {
        switch (activeSelection) {
            case TICKET:
                deleteSelectedTicket();
                break;
            case FLIGHT:
                deleteSelectedFlight();
                break;
            case USER:
                deleteSelectedUser();
                break;
            default:
                showAlert(Alert.AlertType.WARNING, "Nincs kijelölve elem",
                        "Kérjük, válassz ki egy jegyet, járatot vagy fiókot valamelyik listából!");
                break;
        }
    }

    private void deleteSelectedTicket() {
        String selected = bookingsListView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.startsWith("Jegy: ") || !selected.contains(" |")) {
            showAlert(Alert.AlertType.WARNING, "Nincs törölhető jegy", "Kérjük, válassz ki egy érvényes jegyet!");
            return;
        }

        String ticketId = selected.substring("Jegy: ".length(), selected.indexOf(" |")).trim();
        if (!confirmDelete("Jegy törlése", "Biztosan törlöd a(z) '" + ticketId + "' jegyet?",
                "A jegy törlődik.")) {
            return;
        }

        try {
            ticketDao.deleteTicket(ticketId);
            loadAdminData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Nem sikerült törölni a jegyet: " + e.getMessage());
        }
    }

    private void deleteSelectedFlight() {
        String selected = flightsListView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.contains("[") || !selected.contains("]")) {
            showAlert(Alert.AlertType.WARNING, "Nincs kijelölve járat", "Kérjük, válassz ki egy érvényes járatot!");
            return;
        }

        String flightId = selected.substring(selected.indexOf("[") + 1, selected.indexOf("]")).trim();
        if (!confirmDelete("Járat törlése", "Biztosan törlöd a(z) '" + flightId + "' járatot?",
                "A járat és a hozzá tartozó összes foglalás is törlődik.")) {
            return;
        }

        try {
            flightDao.deleteFlight(flightId);
            loadAdminData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Nem sikerült törölni a járatot: " + e.getMessage());
        }
    }

    private void deleteSelectedUser() {
        String selected = usersListView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.contains(" (")) {
            showAlert(Alert.AlertType.WARNING, "Nincs kijelölve fiók", "Kérjük, válassz ki egy felhasználói fiókot!");
            return;
        }

        String username = selected.split(" \\(")[0];
        User currentUser = SessionManager.getCurrentUser();
        if ("admin".equalsIgnoreCase(username) || (currentUser != null && username.equals(currentUser.getUsername()))) {
            showAlert(Alert.AlertType.WARNING, "Nem törölhető", "Az alapértelmezett admin vagy a saját fiókod nem törölhető!");
            return;
        }

        if (!confirmDelete("Fiók törlése", "Biztosan törlöd a(z) '" + username + "' fiókot?",
                "Ez a művelet nem vonható vissza.")) {
            return;
        }

        try {
            userDao.deleteUser(username);
            loadAdminData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Nem sikerült törölni a fiókot: " + e.getMessage());
        }
    }

    private boolean confirmDelete(String title, String header, String content) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(content);
        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

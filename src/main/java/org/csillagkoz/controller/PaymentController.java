package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.csillagkoz.App;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.model.BookingModel;
import org.csillagkoz.model.CartItem;
import org.csillagkoz.model.Flight;
import org.csillagkoz.model.Passenger;
import org.csillagkoz.model.Ticket;
import org.csillagkoz.service.BookingService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.List;

public class PaymentController implements ModelAware {

    private BookingModel model;
    private final FlightDao flightDao = new FlightDao();
    private final BookingService bookingService = new BookingService();

    @FXML private VBox cartSection;
    @FXML private ListView<CartItem> cartListView;
    @FXML private Label amountLabel;
    @FXML private TextField cardHolderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private PasswordField cvcField;
    @FXML private Label errorLabel;

    // Statikus segédváltozó az ár megjelenítéséhez
    public static double currentAmountToPay = 0;

    @Override
    public void setModel(BookingModel model) {
        this.model = model;
        refreshCartView();
    }

    @FXML
    public void initialize() {
        amountLabel.setText("Fizetendő összeg: " + String.format("%.0f", currentAmountToPay) + " Ft");
    }

    private void refreshCartView() {
        if (cartSection == null || cartListView == null) {
            return;
        }
        if (model != null && !model.getCart().isEmpty()) {
            cartSection.setVisible(true);
            cartSection.setManaged(true);
            cartListView.getItems().setAll(model.getCart());
            currentAmountToPay = model.getTotalPrice();
        } else {
            cartSection.setVisible(false);
            cartSection.setManaged(false);
            cartListView.getItems().clear();
            if (model != null && model.getDestination() == null) {
                currentAmountToPay = 0;
            }
        }
        if (amountLabel != null) {
            amountLabel.setText("Fizetendő összeg: " + String.format("%.0f", currentAmountToPay) + " Ft");
        }
    }

    @FXML
    private void handleRemoveSelected() {
        if (model == null || cartListView == null) {
            return;
        }
        CartItem selected = cartListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            model.removeFromCart(selected);
            refreshCartView();
        }
    }

    @FXML
    private void handlePayment() {
        errorLabel.setVisible(false);

        String holder = cardHolderField.getText().trim();
        // Eltávolítjuk az esetleges szóközöket vagy kötőjeleket a kártyaszámból
        String cardNumber = cardNumberField.getText().replaceAll("[\\s-]", "");
        String expiry = expiryField.getText().trim();
        String cvc = cvcField.getText().trim();

        // 1. Egyszerű formai ellenőrzések (Validáció)
        if (holder.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvc.isEmpty()) {
            showError("Minden mező kitöltése kötelező!");
            return;
        }

        if (!cardNumber.matches("\\d{16}")) {
            showError("A kártyaszámnak pontosan 16 számjegyből kell állnia!");
            return;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            showError("A lejárati dátum formátuma HH/ÉÉ legyen (pl. 05/28)!");
            return;
        }

        if (isCardExpired(expiry)) {
            showError("A megadott bankkártya már lejárt!");
            return;
        }

        if (!cvc.matches("\\d{3}")) {
            showError("A CVC kódnak pontosan 3 számjegyből kell állnia!");
            return;
        }

        // 2. MENTÉS AZ ADATBÁZISBA TRANZAKCIÓVAL
        if (!saveBooking()) {
            return;
        }

        // 3. Ha minden adat helyes és a mentés is sikeres, sikeres fizetés üzenet
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sikeres fizetés");
        alert.setHeaderText("A tranzakció sikeresen megtörtént!");
        alert.setContentText("Köszönjük a vásárlást, a jegyét rögzítettük a rendszerben.");
        alert.showAndWait();

        // 4. Visszairányítás a főoldalra
        try {
            App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isCardExpired(String expiry) {
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    private boolean saveBooking() {
        // Ha a kosárban vannak jegyek, az egész kosarat egyetlen tranzakcióban mentjük el
        if (model != null && !model.getCart().isEmpty()) {
            if (!bookingService.processCartBooking(model.getCart())) {
                showError("Nem sikerült rögzíteni a kosárban lévő foglalásokat az adatbázisban!");
                return false;
            }
            model.clearCart();
            model.clearCurrentBooking();
            return true;
        }

        if (model == null || model.getDestination() == null || model.getPassportNumber() == null) {
            showError("Hiányzó foglalási adatok! Kérjük, kezdje újra a foglalást.");
            return false;
        }

        Flight flight;
        try {
            flight = findBookableFlight(model.getDestination());
        } catch (SQLException e) {
            showError("Adatbázis hiba a járat lekérdezésekor!");
            e.printStackTrace();
            return false;
        }

        if (flight == null || !bookingService.canBookFlight(flight) || !"FOGLALHATÓ".equalsIgnoreCase(flight.getStatus())) {
            showError("A kiválasztott célállomásra jelenleg nincs foglalható járat!");
            return false;
        }

        String fullName = ((model.getLastName() != null ? model.getLastName() : "") + " "
                + (model.getFirstName() != null ? model.getFirstName() : "")).trim();
        int age = model.getBirthDate() != null
                ? Period.between(model.getBirthDate(), LocalDate.now()).getYears()
                : 18;

        Passenger passenger = new Passenger(fullName, model.getPassportNumber(), age);
        String ticketId = "TKT-" + System.currentTimeMillis();
        String travelClass = model.getSelectedClass() != null ? model.getSelectedClass() : "ECONOMY";
        Ticket ticket = new Ticket(ticketId, passenger, flight, travelClass, currentAmountToPay);

        if (!bookingService.processBooking(passenger, ticket, flight, 0)) {
            showError("Nem sikerült rögzíteni a foglalást az adatbázisban!");
            return false;
        }
        model.clearCurrentBooking();
        return true;
    }

    private Flight findBookableFlight(String destinationName) throws SQLException {
        List<Flight> flights = flightDao.getAllFlights();
        for (Flight flight : flights) {
            if (flight.getDestination() != null
                    && destinationName.equalsIgnoreCase(flight.getDestination().getName())
                    && "FOGLALHATÓ".equalsIgnoreCase(flight.getStatus())
                    && flight.getAvailableSeats() > 0) {
                return flight;
            }
        }
        return null;
    }

    @FXML
    private void handleCancel() {
        try {
            if (model != null && model.getDestination() == null) {
                App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
            } else {
                App.setRoot("personal-data", "Csillagköz - Személyes adatok");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

package org.csillagkoz.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.csillagkoz.App;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.database.UserDao;
import org.csillagkoz.model.BookingModel;
import org.csillagkoz.model.CartItem;
import org.csillagkoz.model.Flight;
import org.csillagkoz.model.Passenger;
import org.csillagkoz.model.User;
import org.csillagkoz.service.BookingService;
import org.csillagkoz.service.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PersonalDataController implements ModelAware {

    private BookingModel model;
    private String selectedIdType = "PASSPORT"; // Alapértelmezett az útlevél
    private final FlightDao flightDao = new FlightDao();
    private final BookingService bookingService = new BookingService();
    private final UserDao userDao = new UserDao();

    @FXML
    private Button errorReportButton;

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

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
            return;
        }

        // Ha be van jelentkezve a felhasználó és vannak mentett adatai, bekötjük a weboldalas lenyíló autofill menüt
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.hasPersonalData()) {
            setupAutofillMenu(currentUser);
        }
    }

    @FXML
    private TextField lastNameField;
    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private TextField firstNameField;
    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Label birthDateErrorLabel;

    @FXML
    private Button passportTypeBtn;
    @FXML
    private Button idCardTypeBtn;

    @FXML
    private TextField passportField;
    @FXML
    private Label passportErrorLabel;

    @FXML
    private TextField emailField;
    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField phoneField;
    @FXML
    private Label phoneErrorLabel;

    @FXML
    private void initialize() {
        // Jövőbeni dátumok letiltása a naptárban (csak a mai nap és a múlt engedélyezett)
        if (birthDatePicker != null) {
            birthDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffcccc;"); // Opcionálisan halvány pirosas hátteret is kaphatnak
                    }
                }
            });
        }
    }

    // Útlevél okmánytípus kiválasztása és a gombok állapotának/stílusának frissítése
    @FXML
    private void handleSelectPassport() {
        selectedIdType = "PASSPORT";
        passportTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        idCardTypeBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
        passportField.setPromptText("Pl. AB123456 (2 betű, 6 szám)");
    }

    // Személyi igazolvány okmánytípus kiválasztása és a gombok állapotának/stílusának frissítése
    @FXML
    private void handleSelectIdCard() {
        selectedIdType = "IDCARD";
        idCardTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        passportTypeBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
        passportField.setPromptText("Pl. 123456AB (6 szám, 2 betű)");
    }

    private void clearErrors() {
        String defaultStyle = "";
        lastNameField.setStyle(defaultStyle);
        lastNameErrorLabel.setVisible(false);

        firstNameField.setStyle(defaultStyle);
        firstNameErrorLabel.setVisible(false);

        birthDatePicker.setStyle(defaultStyle);
        birthDateErrorLabel.setVisible(false);

        passportField.setStyle(defaultStyle);
        passportErrorLabel.setVisible(false);

        emailField.setStyle(defaultStyle);
        emailErrorLabel.setVisible(false);

        phoneField.setStyle(defaultStyle);
        phoneErrorLabel.setVisible(false);
    }

    private void setError(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: #ff4d4d; -fx-background-color: #fff0f0;");
        label.setText(message);
        label.setVisible(true);
    }

    private void setDatePickerError(DatePicker datePicker, Label label, String message) {
        datePicker.setStyle("-fx-border-color: #ff4d4d; -fx-background-color: #fff0f0;");
        label.setText(message);
        label.setVisible(true);
    }

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
                model.setPassportNumber(passportField.getText().trim().toUpperCase());
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
    private void handleAddToCart() {
        if (!validateAndSaveInputs()) {
            return;
        }
        if (model == null) {
            return;
        }

        try {
            Flight flight = findFlightForDestination(model.getDestination());
            if (flight == null) {
                setError(lastNameField, lastNameErrorLabel, "Nem található járat a kiválasztott célállomásra!");
                return;
            }

            String fullName = (model.getLastName() + " " + model.getFirstName()).trim();
            int age = Period.between(model.getBirthDate(), LocalDate.now()).getYears();
            Passenger passenger = new Passenger(fullName, model.getPassportNumber(), age);
            String travelClass = model.getSelectedClass() != null ? model.getSelectedClass() : "ECONOMY";
            double price = calculateFinalPrice();

            model.addToCart(new CartItem(flight, passenger, travelClass, price));
            model.clearCurrentBooking();

            App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
        } catch (SQLException | IOException e) {
            System.err.println("Hiba a kosárba tételkor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFinalize() {
        if (!validateAndSaveInputs()) {
            return;
        }

        //Adatok megjelenítése a képernyőn
        System.out.println("A jegy vásárlása sikeresen megtörtént!");
        if (model != null) {
            System.out.println("Utas: " + model.getLastName() + " " + model.getFirstName());
            System.out.println("E-mail: " + model.getEmail());
            System.out.println("Telefon szám: " + model.getPhone());
            System.out.println("Úti cél: " + model.getDestination() + " (" + model.getSelectedClass() + ")");
        }
        double finalPrice = calculateFinalPrice();
        PaymentController.currentAmountToPay = finalPrice;

        // Átiranyítás a fizetési ablakra
        try {
            App.setRoot("payment", "Csillagköz - Fizetés");
        } catch (IOException e)
        {
            System.err.println("Hiba a fizetési ablak betöltésekor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateAndSaveInputs() {
        clearErrors();
        boolean hasError = false;

        // 1. Ellenőrzés: Üresek-e a mezők?
        if (lastNameField == null || lastNameField.getText().trim().isEmpty()) {
            setError(lastNameField, lastNameErrorLabel, "* A mező kitöltése kötelező");
            hasError = true;
        }
        if (firstNameField == null || firstNameField.getText().trim().isEmpty()) {
            setError(firstNameField, firstNameErrorLabel, "* A mező kitöltése kötelező");
            hasError = true;
        }
        if (birthDatePicker == null || birthDatePicker.getValue() == null) {
            setDatePickerError(birthDatePicker, birthDateErrorLabel, "* A születési dátum megadása kötelező");
            hasError = true;
        }
        if (passportField == null || passportField.getText().trim().isEmpty()) {
            setError(passportField, passportErrorLabel, "* A mező kitöltése kötelező");
            hasError = true;
        }
        if (emailField == null || emailField.getText().trim().isEmpty()) {
            setError(emailField, emailErrorLabel, "* A mező kitöltése kötelező");
            hasError = true;
        }
        if (phoneField == null || phoneField.getText().trim().isEmpty()) {
            setError(phoneField, phoneErrorLabel, "* A mező kitöltése kötelező");
            hasError = true;
        }

        // Segédfüggvény a név formátum ellenőrzésére (minden szó nagybetűvel kezdődik, a többi kisbetű, támogatja a szóközt és kötőjelet is)
        java.util.function.Predicate<String> isValidNameFormat = name -> {
            if (name == null || name.trim().isEmpty()) return false;
            String trimmed = name.trim();
            // Csak betűket, szóközt és kötőjelet tartalmazhat
            if (!trimmed.matches("^[\\p{L}\\s-]+$")) return false;

            // Szavakra bontjuk (szóköz vagy kötőjel mentén)
            String[] words = trimmed.split("[\\s-]+");
            for (String word : words) {
                if (word.isEmpty()) continue;
                if (!Character.isUpperCase(word.charAt(0))) {
                    return false;
                }
                for (int i = 1; i < word.length(); i++) {
                    if (!Character.isLowerCase(word.charAt(i))) {
                        return false;
                    }
                }
            }
            return true;
        };

        // Vezetéknév formátum ellenőrzése
        if (lastNameField != null && lastNameField.getText() != null && !lastNameField.getText().trim().isEmpty()) {
            if (!isValidNameFormat.test(lastNameField.getText())) {
                setError(lastNameField, lastNameErrorLabel, "Helytelen formátum! (Pl. Kovács)");
                hasError = true;
            }
        }

        // Keresztnév formátum ellenőrzése
        if (firstNameField != null && firstNameField.getText() != null && !firstNameField.getText().trim().isEmpty()) {
            if (!isValidNameFormat.test(firstNameField.getText())) {
                setError(firstNameField, firstNameErrorLabel, "Helytelen formátum! (Pl. János)");
                hasError = true;
            }
        }

        // 2. Ellenőrzés: Elmúlt-e 18 éves? (Csak akkor vizsgáljuk, ha nem üres a dátum)
        if (birthDatePicker != null && birthDatePicker.getValue() != null) {
            LocalDate birthDate = birthDatePicker.getValue();
            int age = Period.between(birthDate, LocalDate.now()).getYears();

            if (age < 18) {
                setDatePickerError(birthDatePicker, birthDateErrorLabel, "Hiba: Csak 18 év felett!");
                hasError = true;
            }
        }

        // 3. Ellenőrzés: E-mail cím formátuma
        if (emailField != null && emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
            String email = emailField.getText().trim();
            long atCount = email.chars().filter(ch -> ch == '@').count();
            if (atCount != 1 || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                setError(emailField, emailErrorLabel, "Helytelen formátum! (Pl. nev@domain.com)");
                hasError = true;
            }
        }

        // 4. Ellenőrzés: Telefonszám (+CC... vagy 06...)
        if (phoneField != null && phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
            String phone = phoneField.getText().trim();
            if (!phone.matches("^(\\+\\d{8,15}|0\\d{8,14})$")) {
                setError(phoneField, phoneErrorLabel, "Helytelen formátum! (Pl. +3630... vagy 0630...)");
                hasError = true;
            }
        }

        // 5. Ellenőrzés: Útlevél /Személyi igazolvány száma
        if (passportField != null && passportField.getText() != null && !passportField.getText().trim().isEmpty()) {
            String passport = passportField.getText().trim().toUpperCase();
            boolean isValidId = false;
            String expectedPatternMsg = "";

            if ("PASSPORT".equals(selectedIdType)) {
                isValidId = passport.matches("^[A-Z]{2}[0-9]{6}$");
                expectedPatternMsg = "Helytelen formátum! (Pl. AB123456)";
            } else {
                isValidId = passport.matches("^[0-9]{6}[A-Z]{2}$");
                expectedPatternMsg = "Helytelen formátum! (Pl. 123456AB)";
            }

            if (!isValidId) {
                setError(passportField, passportErrorLabel, expectedPatternMsg);
                hasError = true;
            }
        }

        // Ha bármilyen hiba van
        if (hasError) {
            return false;
        }

        //Sikeres validációnál mentjük a modellbe
        saveInputToModel();

        // Ha be van jelentkezve, megkérdezzük, hogy mentse-e az adatokat a fiókjához
        askToSaveToProfile();
        return true;
    }

    private double calculateFinalPrice() {
        double fallbackPrice = 150000;
        if (model == null || model.getDestination() == null) {
            return fallbackPrice;
        }

        try {
            Flight flight = findFlightForDestination(model.getDestination());
            if (flight != null) {
                String passengerTypeCode = mapPassengerType(model.getPassengerType(), model.getBirthDate());
                return bookingService.calculateTicketPrice(
                        flight,
                        model.getSelectedClass(),
                        passengerTypeCode,
                        model.getBirthDate()
                );
            }
        } catch (SQLException e) {
            System.err.println("Nem sikerült lekérni a járat árát: " + e.getMessage());
        }
        return fallbackPrice;
    }

    private Flight findFlightForDestination(String destinationName) throws SQLException {
        List<Flight> flights = flightDao.getAllFlights();
        Flight fallback = null;
        for (Flight flight : flights) {
            if (flight.getDestination() != null
                    && destinationName.equalsIgnoreCase(flight.getDestination().getName())) {
                if ("FOGLALHATÓ".equalsIgnoreCase(flight.getStatus()) && flight.getAvailableSeats() > 0) {
                    return flight;
                }
                if (fallback == null) {
                    fallback = flight;
                }
            }
        }
        return fallback;
    }

    private String mapPassengerType(String uiPassengerType, LocalDate birthDate) {
        if (uiPassengerType == null) {
            return "ADULT";
        }
        String lower = uiPassengerType.toLowerCase();
        if (lower.contains("gyermek") || lower.contains("diák")) {
            return bookingService.isChild(birthDate) ? "CHILD" : "STUDENT";
        }
        if (lower.contains("nyugdíjas")) {
            return "SENIOR";
        }
        return "ADULT";
    }

    private void setupAutofillMenu(User user) {
        ContextMenu autofillMenu = new ContextMenu();
        MenuItem item = new MenuItem("👤 " + user.getLastName() + " " + user.getFirstName()
                + "  (" + user.getPassportNumber() + ")");
        item.setOnAction(e -> fillFieldsFromUser(user));
        autofillMenu.getItems().add(item);

        TextField[] fields = {lastNameField, firstNameField, passportField, emailField, phoneField};
        for (TextField field : fields) {
            if (field == null) continue;

            field.setOnMouseClicked(e -> {
                if (field.getText() == null || field.getText().trim().isEmpty()) {
                    showAutofillMenu(autofillMenu, field);
                }
            });

            field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused && (field.getText() == null || field.getText().trim().isEmpty())) {
                    showAutofillMenu(autofillMenu, field);
                } else if (!isFocused) {
                    autofillMenu.hide();
                }
            });

            field.textProperty().addListener((obs, oldText, newText) -> {
                if (newText != null && !newText.trim().isEmpty()) {
                    autofillMenu.hide();
                }
            });
        }


        Platform.runLater(() -> {
            if (lastNameField != null && lastNameField.isFocused()
                    && (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty())) {
                showAutofillMenu(autofillMenu, lastNameField);
            }
        });
    }

    private void showAutofillMenu(ContextMenu menu, TextField field) {
        if (field.getScene() != null && field.getScene().getWindow() != null
                && field.getScene().getWindow().isShowing()) {
            menu.show(field, Side.BOTTOM, 0, 2);
        }
    }

    private void fillFieldsFromUser(User user) {
        if (lastNameField != null) lastNameField.setText(user.getLastName());
        if (firstNameField != null) firstNameField.setText(user.getFirstName());
        if (birthDatePicker != null && user.getBirthDate() != null && !user.getBirthDate().isEmpty()) {
            birthDatePicker.setValue(LocalDate.parse(user.getBirthDate()));
        }
        if ("IDCARD".equalsIgnoreCase(user.getIdType())) {
            handleSelectIdCard();
        } else {
            handleSelectPassport();
        }
        if (passportField != null) passportField.setText(user.getPassportNumber());
        if (emailField != null) emailField.setText(user.getEmail());
        if (phoneField != null) phoneField.setText(user.getPhone());
    }

    private void askToSaveToProfile() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || !hasProfileDataChanged(currentUser)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adatok mentése");
        alert.setHeaderText("Személyes adatok mentése a fiókhoz");
        alert.setContentText("Szeretnéd elmenteni ezeket a személyes adatokat a felhasználói fiókodhoz a következő foglalásokhoz?");

        ButtonType yesBtn = new ButtonType("Igen");
        ButtonType noBtn = new ButtonType("Nem");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesBtn) {
            currentUser.setLastName(lastNameField.getText().trim());
            currentUser.setFirstName(firstNameField.getText().trim());
            currentUser.setBirthDate(birthDatePicker.getValue().toString());
            currentUser.setIdType(selectedIdType);
            currentUser.setPassportNumber(passportField.getText().trim().toUpperCase());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setPhone(phoneField.getText().trim());

            try {
                userDao.savePersonalData(currentUser);
            } catch (SQLException e) {
                System.err.println("Nem sikerült elmenteni a felhasználó adatait: " + e.getMessage());
            }
        }
    }

    private boolean hasProfileDataChanged(User user) {
        String birthStr = birthDatePicker.getValue() != null ? birthDatePicker.getValue().toString() : "";
        return !Objects.equals(user.getLastName(), lastNameField.getText().trim())
                || !Objects.equals(user.getFirstName(), firstNameField.getText().trim())
                || !Objects.equals(user.getBirthDate(), birthStr)
                || !Objects.equals(user.getIdType(), selectedIdType)
                || !Objects.equals(user.getPassportNumber(), passportField.getText().trim().toUpperCase())
                || !Objects.equals(user.getEmail(), emailField.getText().trim())
                || !Objects.equals(user.getPhone(), phoneField.getText().trim());
    }
}
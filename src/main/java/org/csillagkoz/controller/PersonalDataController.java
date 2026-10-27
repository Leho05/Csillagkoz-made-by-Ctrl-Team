package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.csillagkoz.App;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PersonalDataController implements ModelAware {

    private BookingModel model;
    private String selectedIdType = "PASSPORT"; // Alapértelmezett az útlevél

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

    @FXML
    private void handleSelectPassport() {
        selectedIdType = "PASSPORT";
        passportTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        idCardTypeBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
        passportField.setPromptText("Pl. AB123456 (2 betű, 6 szám)");
    }

    @FXML
    private void handleSelectIdCard() {
        selectedIdType = "IDCARD";
        idCardTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;");
        passportTypeBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: black;");
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
    private void handleFinalize() {
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
                // Az első betűnek nagybetűnek kell lennie
                if (!Character.isUpperCase(word.charAt(0))) {
                    return false;
                }
                // A többi betűnek kisbetűnek kell lennie
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
                setError(lastNameField, lastNameErrorLabel, "Helytelen formátum!");
                System.out.println("Hiba: Érvénytelen vezetéknév formátum!");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // Keresztnév formátum ellenőrzése
        if (firstNameField != null && firstNameField.getText() != null && !firstNameField.getText().trim().isEmpty()) {
            if (!isValidNameFormat.test(firstNameField.getText())) {
                setError(firstNameField, firstNameErrorLabel, "Helytelen formátum!");
                System.out.println("Hiba: Érvénytelen keresztnév formátum!");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // 2. Ellenőrzés: Elmúlt-e 18 éves? (Csak akkor vizsgáljuk, ha nem üres a dátum)
        if (birthDatePicker != null && birthDatePicker.getValue() != null) {
            LocalDate birthDate = birthDatePicker.getValue();
            int age = Period.between(birthDate, LocalDate.now()).getYears();

            if (age < 18) {
                setDatePickerError(birthDatePicker, birthDateErrorLabel, "Hiba: Csak 18 év felett!");
                System.out.println("Hiba: A jegyvásárló nem múlt el 18 éves, a foglalás nem véglegesíthető!");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // 3. Ellenőrzés: E-mail cím formátuma (csak ha nem üres)
        if (emailField != null && emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
            String email = emailField.getText().trim();
            long atCount = email.chars().filter(ch -> ch == '@').count();
            if (atCount != 1 || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                setError(emailField, emailErrorLabel, "Helytelen formátum!");
                System.out.println("Hibás e-mail! Kérjük, érvényes e-mail címet adjon meg (pl. nev@domain.com)!");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // 4. Ellenőrzés: Telefonszám (+CC... vagy 06...) (csak ha nem üres)
        if (phoneField != null && phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
            String phone = phoneField.getText().trim();
            if (!phone.matches("^(\\+\\d{8,15}|0\\d{8,14})$")) {
                setError(phoneField, phoneErrorLabel, "Helytelen formátum!");
                System.out.println("Hiba: Érvénytelen telefonszám! Elvárt formátum: nemzetközi (+ és országhívó) vagy belföldi (0-val kezdődő) számjegyek.");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // 5. Ellenőrzés: Útlevél / Személyi igazolvány száma (csak ha nem üres)
        if (passportField != null && passportField.getText() != null && !passportField.getText().trim().isEmpty()) {
            String passport = passportField.getText().trim().toUpperCase();
            boolean isValidId = false;

            if ("PASSPORT".equals(selectedIdType)) {
                isValidId = passport.matches("^[A-Z]{2}[0-9]{6}$");
            } else {
                isValidId = passport.matches("^[0-9]{6}[A-Z]{2}$");
            }

            if (!isValidId) {
                setError(passportField, passportErrorLabel, "Helytelen formátum!");
                System.out.println("Hiba: Érvénytelen útlevél/személyi igazolvány szám! Csak betűket és számokat tartalmazhat (6-10 karakter).");
                // TODO szebb hiba kiírás
                hasError = true;
            }
        }

        // Ha bármilyen hiba van, itt állunk meg egyszerre, kiírva az összeset a mezőkhöz
        if (hasError) {
            System.out.println("Hiba: Kérjük, javítsa a hibásan kitöltött mezőket a továbblépéshez!");
            return;
        }

        //Sikeres validációnál mentjük a modellbe
        saveInputToModel();

        //Adatok megjelenítése a képernyőn
        System.out.println("A jegy vásárlása sikeresen megtörtént!");
        if (model != null) {
            System.out.println("Utas: " + model.getLastName() + " " + model.getFirstName());
            System.out.println("E-mail: " + model.getEmail());
            System.out.println("Telefon szám: " + model.getPhone());
            System.out.println("Úti cél: " + model.getDestination() + " (" + model.getSelectedClass() + ")");
        }

        // GUI ablak bezárása
        Stage stage = (Stage) lastNameField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
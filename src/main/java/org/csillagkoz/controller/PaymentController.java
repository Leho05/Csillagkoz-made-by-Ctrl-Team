package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.csillagkoz.App;

import java.io.IOException;

public class PaymentController {

    @FXML private Label amountLabel;
    @FXML private TextField cardHolderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private PasswordField cvcField;
    @FXML private Label errorLabel;

    // Statikus segédváltozó az ár megjelenítéséhez
    public static double currentAmountToPay = 0;

    @FXML
    public void initialize() {
        amountLabel.setText("Fizetendő összeg: " + String.format("%.0f", currentAmountToPay) + " Ft");
    }

    @FXML
    private void handlePayment() {
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

        if (!cvc.matches("\\d{3}")) {
            showError("A CVC kódnak pontosan 3 számjegyből kell állnia!");
            return;
        }

        // 2. Ha minden adat helyes, sikeres fizetés üzenet
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sikeres fizetés");
        alert.setHeaderText("A tranzakció sikeresen megtörtént!");
        alert.setContentText("Köszönjük a vásárlást, a jegyét rögzítettük a rendszerben.");
        alert.showAndWait();

        // 3. Visszairányítás a főoldalra
        try {
            App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            App.setRoot("personal-data", "Személyes adatok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
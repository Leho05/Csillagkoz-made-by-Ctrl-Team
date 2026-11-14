package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.csillagkoz.App;
import org.csillagkoz.database.UserDao;
import org.csillagkoz.model.User;
import org.csillagkoz.service.SessionManager;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    // 1. Ha be akar jelentkezni
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Kérlek, add meg a felhasználónevet és a jelszót!");
            return;
        }

        try {
            User user = userDao.authenticate(username, password);
            if (user != null) {
                SessionManager.login(user); // Elmentjük a belépett felhasználót
                goToMainPage();
            } else {
                showError("Hibás felhasználónév vagy jelszó!");
            }
        } catch (SQLException e) {
            showError("Adatbázis hiba történt a belépéskor!");
            e.printStackTrace();
        }
    }

    // 2. Ha új fiókot akar regisztrálni
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("A regisztrációhoz töltsd ki a mezőket!");
            return;
        }

        try {
            boolean success = userDao.registerUser(username, password);
            if (success) {
                // Regisztráció után automatikusan be is léptetjük USER-ként
                SessionManager.login(new User(username, password, "USER"));
                goToMainPage();
            } else {
                showError("Ez a felhasználónév már foglalt!");
            }
        } catch (SQLException e) {
            showError("Adatbázis hiba a regisztráció során!");
            e.printStackTrace();
        }
    }

    // 3. Ha NEM akar bejelentkezni (Vendég mód)
    @FXML
    private void handleGuestContinue() {
        SessionManager.logout(); // nincs senki belépve
        goToMainPage();
    }

    private void goToMainPage() {
        try {
            App.setRoot("ticket-booking", "Csillagköz - Jegyfoglalás");
        } catch (IOException e) {
            showError("Nem sikerült betölteni a főoldalt!");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
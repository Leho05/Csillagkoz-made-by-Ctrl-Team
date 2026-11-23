package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.csillagkoz.App;
import org.csillagkoz.database.UserDao;
import org.csillagkoz.model.User;
import org.csillagkoz.service.SessionManager;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private enum AuthMode {
        USER_LOGIN,
        ADMIN_LOGIN,
        REGISTER
    }

    @FXML private VBox choiceBox;
    @FXML private VBox credentialsBox;
    @FXML private Label subtitleLabel;
    @FXML private Label modeTitleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;
    @FXML private Button errorReportButton;

    private final UserDao userDao = new UserDao();
    private AuthMode currentMode = AuthMode.USER_LOGIN;

    @FXML
    private void handleOpenErrorReport() {
        javafx.stage.Window owner = errorReportButton != null && errorReportButton.getScene() != null
                ? errorReportButton.getScene().getWindow()
                : null;
        ErrorReportDialog.show(owner);
    }

    @FXML
    private void showUserLogin() {
        switchToForm(AuthMode.USER_LOGIN,
                "Felhasználói bejelentkezés",
                "Adja meg felhasználónevét és jelszavát.",
                "Belépés");
    }

    @FXML
    private void showAdminLogin() {
        switchToForm(AuthMode.ADMIN_LOGIN,
                "Adminisztrátori bejelentkezés",
                "Adja meg admin fiókja adatait.",
                "Belépés adminként");
    }

    @FXML
    private void showRegister() {
        switchToForm(AuthMode.REGISTER,
                "Új fiók regisztrációja",
                "Hozzon létre egy új felhasználói fiókot.",
                "Regisztráció");
    }

    @FXML
    private void handleBackToChoices() {
        clearForm();
        credentialsBox.setVisible(false);
        credentialsBox.setManaged(false);
        choiceBox.setVisible(true);
        choiceBox.setManaged(true);
        subtitleLabel.setText("Válasszon belépési módot, vagy folytassa vendégként a jegyfoglalást.");
    }

    @FXML
    private void handleSubmit() {
        switch (currentMode) {
            case USER_LOGIN:
                handleLogin(false);
                break;
            case ADMIN_LOGIN:
                handleLogin(true);
                break;
            case REGISTER:
                handleRegister();
                break;
        }
    }

    private void switchToForm(AuthMode mode, String title, String subtitle, String buttonText) {
        currentMode = mode;
        clearForm();
        modeTitleLabel.setText(title);
        subtitleLabel.setText(subtitle);
        submitButton.setText(buttonText);

        choiceBox.setVisible(false);
        choiceBox.setManaged(false);
        credentialsBox.setVisible(true);
        credentialsBox.setManaged(true);
    }

    private void handleLogin(boolean requireAdmin) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Kérlek, add meg a felhasználónevet és a jelszót!");
            return;
        }

        try {
            User user = userDao.authenticate(username, password);
            if (user == null) {
                showError("Hibás felhasználónév vagy jelszó!");
                return;
            }
            if (requireAdmin && !user.isAdmin()) {
                showError("Ehhez a fiókhoz nincs adminisztrátori jogosultság!");
                return;
            }

            SessionManager.login(user);
            if (requireAdmin) {
                App.setRoot("admin", "Csillagköz - Adminisztráció");
            } else {
                goToMainPage();
            }
        } catch (SQLException e) {
            showError("Adatbázis hiba történt a belépéskor!");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Nem sikerült megnyitni a céloldalt!");
            e.printStackTrace();
        }
    }

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

    @FXML
    private void handleGuestContinue() {
        SessionManager.logout();
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

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
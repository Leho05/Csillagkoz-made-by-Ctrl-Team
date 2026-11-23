package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorReportController {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @FXML
    private TextArea errorDescriptionArea;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button submitBtn;

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleSubmit() {
        String description = errorDescriptionArea != null && errorDescriptionArea.getText() != null
                ? errorDescriptionArea.getText().trim()
                : "";

        if (description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Hiányzó leírás");
            alert.setHeaderText(null);
            alert.setContentText("Kérjük, írja le a hibát az elküldés előtt!");
            alert.showAndWait();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DISPLAY_FORMATTER);
        String fileName = "hiba_" + now.format(FILE_NAME_FORMATTER) + ".txt";

        Path reportDir = Paths.get("hibabejelentes");
        Path reportFile = reportDir.resolve(fileName);

        String fileContent = formattedDate + System.lineSeparator() + description + System.lineSeparator();

        try {
            Files.createDirectories(reportDir);
            Files.writeString(reportFile, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Mentési hiba");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Nem sikerült menteni a hibabejelentést: " + e.getMessage());
            errorAlert.showAndWait();
            return;
        }

        System.out.println("Beérkezett hibabejelentés (" + reportFile.toAbsolutePath() + "):\n" + fileContent);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Köszönjük");
        info.setHeaderText(null);
        info.setContentText("A hibabejelentését rögzítettük.");
        info.showAndWait();

        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
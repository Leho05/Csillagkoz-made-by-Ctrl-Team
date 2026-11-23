package org.csillagkoz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorReportController {

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

        System.out.println("Beérkezett hibabejelentés:\n" + description);

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
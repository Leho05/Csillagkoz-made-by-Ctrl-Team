package org.csillagkoz.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public final class ErrorReportDialog {

    private ErrorReportDialog() {
    }

    public static void show(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ErrorReportDialog.class.getResource("/org/csillagkoz/error-report.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Csillagköz - Hibabejelentés");
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Nem sikerült megnyitni a hibabejelentő ablakot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
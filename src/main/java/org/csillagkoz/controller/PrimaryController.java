package org.csillagkoz.controller;

import javafx.fxml.FXML;
import org.csillagkoz.App;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}

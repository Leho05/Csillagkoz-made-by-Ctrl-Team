package org.csillagkoz.controller;

import javafx.fxml.FXML;
import org.csillagkoz.App;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
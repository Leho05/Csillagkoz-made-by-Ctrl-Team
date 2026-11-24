package org.csillagkoz.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.csillagkoz.database.TicketDao;
import org.csillagkoz.model.BookingModel;
import org.csillagkoz.model.CartItem;

import java.util.List;

public class SeatSelectionHelper {

    public static void setupSeats(GridPane seatGrid, Label selectedSeatLabel, Label errorLabel,
                                  BookingModel model, String travelClass, int rows, int cols) {
        seatGrid.getChildren().clear();

        // 1. Foglalt székek lekérése az adatbázisból + a jelenlegi kosárból
        TicketDao ticketDao = new TicketDao();
        String destination = (model != null) ? model.getDestination() : null;
        List<String> occupied = (destination != null)
                ? ticketDao.getOccupiedSeats(destination, travelClass)
                : new java.util.ArrayList<>();

        if (model != null) {
            for (CartItem item : model.getCart()) {
                if (item.getFlight() != null && item.getFlight().getDestination() != null
                        && item.getFlight().getDestination().getName().equalsIgnoreCase(destination)
                        && travelClass.equalsIgnoreCase(item.getTravelClass())
                        && item.getSeatNumber() != null) {
                    occupied.add(item.getSeatNumber());
                }
            }
        }

        final Button[] currentSelectedBtn = { null };
        String preselected = (model != null) ? model.getSelectedSeat() : null;

        // 2. Szék gombok legenerálása két for ciklussal
        for (int r = 1; r <= rows; r++) {
            for (int c = 0; c < cols; c++) {
                String seatCode = r + String.valueOf((char) ('A' + c)); // pl. "1A", "1B"
                Button btn = new Button(seatCode);
                btn.setPrefWidth(55);
                btn.setPrefHeight(36);

                if (occupied.contains(seatCode)) {
                    // Már foglalt szék: piros és nem kattintható
                    btn.setDisable(true);
                    btn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #880000;");
                } else if (seatCode.equals(preselected)) {
                    // Korábban már kiválasztott szék (pl. visszalépés után): kék
                    btn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold;");
                    currentSelectedBtn[0] = btn;
                    selectedSeatLabel.setText("Kiválasztott szék: " + seatCode);
                } else {
                    // Szabad szék: szürke
                    btn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
                }

                btn.setOnAction(e -> {
                    if (currentSelectedBtn[0] != null) {
                        currentSelectedBtn[0].setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
                    }
                    btn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold;");
                    currentSelectedBtn[0] = btn;

                    if (model != null) {
                        model.setSelectedSeat(seatCode);
                    }
                    selectedSeatLabel.setText("Kiválasztott szék: " + seatCode);
                    if (errorLabel != null) {
                        errorLabel.setVisible(false);
                        errorLabel.setManaged(false);
                    }
                });

                // Kis folyosóhézag a székek közepén: a jobb oldali oszlopokat 1-gyel eltoljuk
                int gridCol = (c >= cols / 2) ? c + 1 : c;
                seatGrid.add(btn, gridCol, r - 1);
            }
        }
    }
}
package org.csillagkoz.service;

import org.csillagkoz.model.Flight;
import java.time.LocalDateTime;

/**
 * Üzleti logika és számítások
 */
public class BookingService {


    public double calculateTicketPrice(Flight flight, String travelClass) {
        double price=flight.getDestination().getBasePrice();

        if ("MOON".equalsIgnoreCase(flight.getDestination().getType())) {
            price *= 0.75; // 25% kedvezmény
        }

        // Utazási osztály szerinti szorzó
        switch (travelClass.toUpperCase()) {
            case "BUSINESS":
                price *= 1.5;
                break;
            case "FIRST_CLASS":
                price *= 2.5;
                break;
            default: // ECONOMY (Standard)
                price *= 1.0;
                break;
        }

        return price;
    }

    /**
     * Ellenőrzi, hogy a járatra lehetséges-e még a jegyfoglalás.
     */
    public boolean canBookFlight(Flight flight) {
        LocalDateTime now = LocalDateTime.now();
        // Ha lejárt a jegyértékesítési határidő
        if (now.isAfter(flight.getBookingDeadline())) {
            return false;
        }
        // Ha elfogytak a szabad helyek
        if (flight.getAvailableSeats() <= 0) {
            return false;
        }

        return true;
    }
}
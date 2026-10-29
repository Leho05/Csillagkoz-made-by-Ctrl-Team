package org.csillagkoz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import org.csillagkoz.model.Flight;

/**
 * Üzleti logika
 */
public class BookingService {

    /**
     * Ellenőrzi, hogy az utas gyermek-e (18 év alatti).
     */
    public boolean isChild(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        // Kiszámolja a mai nap és a születési dátum közötti időtartamot
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 18 év alatt van-e (és nem jövőbeli dátum)
        return age >= 0 && age < 18;
    }

    /**
     * Kiszámítja a jegy árát az alapár, az utazási osztály és az utastípus (korosztály) alapján.
     */
    public double calculateTicketPrice(Flight flight, String travelClass, String passengerType, LocalDate birthDate) {
        double price = flight.getDestination().getBasePrice();

        if ("MOON".equalsIgnoreCase(flight.getDestination().getType())) {
            price *= 0.75; // 25% kedvezmény a Hold járatokra
        }

        // 1. Utazási osztály szerinti szorzó
        if (travelClass != null) {
            switch (travelClass.toUpperCase()) {
                case "BUSINESS":
                    price *= 1.5;
                    break;
                case "FIRST_CLASS":
                    price *= 2.5;
                    break;
                default: // ECONOMY
                    price *= 1.0;
                    break;
            }
        }

        // 2. Utastípus (kedvezmény) és korosztály ellenőrzése
        if (passengerType != null) {
            switch (passengerType.toUpperCase()) {
                case "CHILD":
                    // Ellenőrizzük, hogy a megadott születési dátum alapján valóban 18 év alatti-e
                    if (!isChild(birthDate)) {
                        throw new IllegalArgumentException("A gyermek kedvezmény csak 18 év alatti utasok számára érhető el!");
                    }
                    price *= 0.5; // 50% kedvezmény
                    break;
                case "STUDENT":
                    price *= 0.8; // 20% kedvezmény
                    break;
                case "SENIOR":
                    price *= 0.7; // 30% kedvezmény (opcionális)
                    break;
                default: // ADULT / FELNŐTT
                    price *= 1.0; // Teljes ár
                    break;
            }
        }

        return price;
    }

    /**
     * Ellenőrzi, hogy a járatra lehetséges-e még a jegyfoglalás.
     */
    public boolean canBookFlight(Flight flight) {
        // Ha elfogytak a szabad helyek
        if (flight.getAvailableSeats() <= 0) {
            return false;
        }

        return true;
    }
}
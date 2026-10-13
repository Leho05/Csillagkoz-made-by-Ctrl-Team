package org.csillagkoz.model;

import java.time.LocalDateTime;

/**
 * Járat entitás
 */
public class Flight {
    private String flightId;               // Járat azonosító (pl. "MARS-2026-01")
    private Destination destination;       // Hova megy (Destination objektum)
    private LocalDateTime departureTime;   // Indulási időpont
    private LocalDateTime bookingDeadline; // Jegyértékesítési határidő (pl. indulás előtt 48 óra)
    private int maxCapacity;               // Max férőhely az űrhajón
    private int availableSeats;          // Aktuális szabad helyek száma
    private String status;                 // Státusz: "TERVEZETT", "FOGLALHATÓ", "MEGTELT", "LEZÁRT"

    // Konstruktor
    public Flight(String flightId, Destination destination, LocalDateTime departureTime, LocalDateTime bookingDeadline, int maxCapacity) {
        this.flightId = flightId;
        this.destination = destination;
        this.departureTime = departureTime;
        this.bookingDeadline = bookingDeadline;
        this.maxCapacity = maxCapacity;
        this.availableSeats = maxCapacity; // Kezdetben minden hely szabad
        this.status = "FOGLALHATÓ";
    }

    /**
     * Jegyfoglalás logika: csökkenti a szabad helyek számát, ha van még hely.
     */
    public boolean bookSeat() {
        if (availableSeats > 0) {
            availableSeats--;
            if (availableSeats == 0) {
                this.status = "MEGTELT";
            }
            return true;
        }
        return false;
    }

    public String getFlightId() {
        return flightId;
    }

    public Destination getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getBookingDeadline() {
        return bookingDeadline;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Járat [" + flightId + "] -> Cél: " + destination.getName() + " | Szabad helyek: " + availableSeats + "/" + maxCapacity + " | Státusz: " + status;
    }
}
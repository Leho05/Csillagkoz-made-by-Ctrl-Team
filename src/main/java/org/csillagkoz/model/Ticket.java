package org.csillagkoz.model;

import java.time.LocalDateTime;

/**
 * Jegy / Foglalás
 */
public class Ticket {
    private String ticketId;         // Jegy egyedi azonosítója
    private Passenger passenger;     // Ki foglalta (Utas objektum)
    private Flight flight;           // Melyik járatra (Flight objektum)
    private String travelClass;      // Választott osztály (ECONOMY, BUSINESS, FIRST_CLASS)
    private double finalPrice;       // Végső kiszámított ár (kedvezményekkel)
    private LocalDateTime bookingDate;// Vásárlás/Foglalás dátuma

    public Ticket(String ticketId, Passenger passenger, Flight flight, String travelClass, double finalPrice) {
        this.ticketId = ticketId;
        this.passenger = passenger;
        this.flight = flight;
        this.travelClass = travelClass;
        this.finalPrice = finalPrice;
        this.bookingDate = LocalDateTime.now();
    }

    public String getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    @Override
    public String toString() {
        return "Jegy #" + ticketId + " | Utas: " + passenger.getName() + " | Cél: " + flight.getDestination().getName() + " | Osztály: " + travelClass + " | Ár: " + finalPrice + " 💳";
    }
}
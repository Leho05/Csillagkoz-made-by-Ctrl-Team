package org.csillagkoz.model;

/**
 * Egy kosárba tett jegy adatait tároló modell osztály.
 */
public class CartItem {
    private final Flight flight;
    private final Passenger passenger;
    private final String travelClass;
    private final double price;
    private final String seatNumber;

    public CartItem(Flight flight, Passenger passenger, String travelClass, String seatNumber, double price) {
        this.flight = flight;
        this.passenger = passenger;
        this.travelClass = travelClass;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public Flight getFlight() {
        return flight;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public double getPrice() {
        return price;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    @Override
    public String toString() {
        String destName = (flight != null && flight.getDestination() != null)
                ? flight.getDestination().getName()
                : "Ismeretlen úti cél";
        String passengerName = (passenger != null) ? passenger.getName() : "Ismeretlen utas";
        return String.format("%s | %s | %s | %s | %.0f Ft", destName, travelClass, passengerName, seatNumber, price);
    }
}

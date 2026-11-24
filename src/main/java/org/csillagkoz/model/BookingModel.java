package org.csillagkoz.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingModel {
    private String previousView = "ticket-booking";

    // Keresési és foglalási adatok
    private String departure;
    private String destination;
    private LocalDate flightDate;
    private String passengerType;
    private String selectedClass; // ECONOMY, BUSINESS, FIRST_CLASS
    private String selectedSeat;

    // Személyes adatok
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String passportNumber;
    private String email;
    private String phone;

    // Kosár a több jegy együttes megvásárlásához
    private List<CartItem> cart = new ArrayList<>();

    public List<CartItem> getCart() {
        return cart;
    }
    public void addToCart(CartItem item) {
        if (item != null) {
            cart.add(item);
        }
    }
    public void removeFromCart(CartItem item) {
        cart.remove(item);
    }
    public void clearCart() {
        cart.clear();
    }
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getPrice();
        }
        return total;
    }

    public void clearCurrentBooking() {
        this.previousView = "ticket-booking";

        this.departure = null;
        this.destination = null;
        this.flightDate = null;
        this.passengerType = null;
        this.selectedClass = null;
        this.selectedSeat = null;

        this.lastName = null;
        this.firstName = null;
        this.birthDate = null;
        this.passportNumber = null;
        this.email = null;
        this.phone = null;
    }
    public String getPreviousView() {
        return previousView;
    }

    public void setPreviousView(String previousView) {
        this.previousView = previousView;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSelectedSeat() { return selectedSeat; }

    public void setSelectedSeat(String selectedSeat) { this.selectedSeat = selectedSeat; }
}
package org.csillagkoz.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csillagkoz.database.Database;
import org.csillagkoz.database.FlightDao;
import org.csillagkoz.database.PassengerDao;
import org.csillagkoz.database.TicketDao;
import org.csillagkoz.model.CartItem;
import org.csillagkoz.model.Flight;
import org.csillagkoz.model.Passenger;
import org.csillagkoz.model.Ticket;

/**
 * Üzleti logika
 */
public class BookingService {

    private final PassengerDao passengerDao = new PassengerDao();
    private final TicketDao ticketDao = new TicketDao();
    private final FlightDao flightDao = new FlightDao();

    /**
     * Ellenőrzi, hogy az utas gyermek-e (18 év alatti).
     */
    public boolean isChild(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        return age >= 0 && age < 18;
    }

    /**
     * Kiszámítja a jegy árát az alapár, az utazási osztály és az utastípus (korosztály) alapján.
     */
    public double calculateTicketPrice(Flight flight, String travelClass, String passengerType, LocalDate birthDate) {
        if (flight == null || flight.getDestination() == null) {
            throw new IllegalArgumentException("A járat vagy az úticél adata nem lehet üres!");
        }

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
                    price *= 0.7; // 30% kedvezmény
                    break;
                default: // FELNŐTT
                    price *= 1.0;
                    break;
            }
        }

        return price;
    }

    /**
     * Ellenőrzi, hogy a járatra lehetséges-e még a jegyfoglalás.
     */
    public boolean canBookFlight(Flight flight) {
        return flight != null && flight.getAvailableSeats() > 0;
    }

    /**
     * TELJES FOGLALÁSI FOLYAMAT TRANZAKCIÓVAL:
     * - Elmenti az utast (vagy lekéri az ID-ját)
     * - Elmenti a jegyet
     * - Csökkenti a szabad helyek számát a járaton
     * - Ha bármi hiba történik, a tranzakció visszavonásra kerül!!
     */
    public boolean processBooking(Passenger passenger, Ticket ticket, Flight flight, int flightId) {

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                //Utas rögzítése az adatbázisban
                passengerDao.saveOrUpdatePassenger(connection, passenger);
                ticketDao.saveTicket(connection, ticket);

                // Szabad helyek csökkentése a járaton
                if (!flight.bookSeat()) {
                    throw new SQLException("Sikertelen foglalás: Ezen a járaton már nincsenek szabad helyek!");
                }
                flightDao.updateSeats(connection, flight);

                // Ha minden sikeres volt, véglegesítjük a változtatásokat
                connection.commit();
                return true;

            } catch (SQLException e) {
                // Hiba esetén vissza az egészet az eredeti állapotra!!
                connection.rollback();
                System.err.println("Hiba történt a foglalás mentésekor, visszavonva: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Adatbázis kapcsolódási hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * TÖBB JEGY (KOSÁR) MENTÉSE EGYETLEN TRANZAKCIÓBAN:

     */
    public boolean processCartBooking(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {

                Map<String, Flight> flightCache = new HashMap<>();
                int index = 0;

                for (CartItem item : cart) {
                    index++;
                    Passenger passenger = item.getPassenger();
                    Flight flight = flightCache.computeIfAbsent(
                            item.getFlight().getFlightId(),
                            id -> item.getFlight()
                    );

                    passengerDao.saveOrUpdatePassenger(connection, passenger);

                    String ticketId = "TKT-" + System.currentTimeMillis() + "-" + index;
                    Ticket ticket = new Ticket(ticketId, passenger, flight, item.getTravelClass(), item.getPrice(), item.getSeatNumber());
                    ticketDao.saveTicket(connection, ticket);

                    if (!flight.bookSeat()) {
                        throw new SQLException("Sikertelen foglalás: A(z) " + flight.getFlightId() + " járaton elfogytak a szabad helyek!");
                    }
                    flightDao.updateSeats(connection, flight);
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Hiba történt a kosár mentésekor, minden visszavonva: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Adatbázis kapcsolódási hiba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
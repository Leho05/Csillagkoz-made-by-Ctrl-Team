package org.csillagkoz.database;

import org.csillagkoz.model.Destination;
import org.csillagkoz.model.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightDao {

    // Új járat elmentése az adatbázisba
    public void saveFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_id, destination_name, destination_type, base_price, "
                + "departure_time, booking_deadline, max_capacity, available_seats, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flight.getFlightId());
            pstmt.setString(2, flight.getDestination().getName());
            pstmt.setString(3, flight.getDestination().getType());
            pstmt.setInt(4, (int) flight.getDestination().getBasePrice());
            pstmt.setString(5, flight.getDepartureTime().toString());
            pstmt.setString(6, flight.getBookingDeadline().toString());
            pstmt.setInt(7, flight.getMaxCapacity());
            pstmt.setInt(8, flight.getAvailableSeats());
            pstmt.setString(9, flight.getStatus());

            pstmt.executeUpdate();
        }
    }

    // Összes járat lekérése
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Destination dest = new Destination(
                        rs.getString("destination_name"),
                        rs.getString("destination_type"),
                        rs.getInt("base_price"),
                        0
                );

                Flight flight = new Flight(
                        rs.getString("flight_id"),
                        dest,
                        LocalDateTime.parse(rs.getString("departure_time")),
                        LocalDateTime.parse(rs.getString("booking_deadline")),
                        rs.getInt("max_capacity")
                );
                flight.setStatus(rs.getString("status"));
                flights.add(flight);
            }
        }
        return flights;
    }

    // Szabad helyek és státusz frissítése jegyvásárlás után
    public void updateSeats(Flight flight) throws SQLException {
        String sql = "UPDATE flights SET available_seats = ?, status = ? WHERE flight_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flight.getAvailableSeats());
            pstmt.setString(2, flight.getStatus());
            pstmt.setString(3, flight.getFlightId());

            pstmt.executeUpdate();
        }
    }
}
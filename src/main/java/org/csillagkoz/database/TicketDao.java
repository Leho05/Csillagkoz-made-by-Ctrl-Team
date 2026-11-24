package org.csillagkoz.database;

import org.csillagkoz.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketDao {

    // Új jegy elmentése (a kiválasztott székszámmal együtt)
    public void saveTicket(Connection conn, Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (ticket_id, flight_id, galactic_id, travel_class, final_price, booking_date, seat_number) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getFlight().getFlightId());
            pstmt.setString(3, ticket.getPassenger().getGalacticId());
            pstmt.setString(4, ticket.getTravelClass());
            pstmt.setDouble(5, ticket.getFinalPrice());
            pstmt.setString(6, ticket.getBookingDate().toString());
            pstmt.setString(7, ticket.getSeatNumber());

            pstmt.executeUpdate();
        }
    }

    // Már lefoglalt székek lekérése adott úti célra és kabinosztályra
    public List<String> getOccupiedSeats(String destinationName, String travelClass) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT t.seat_number FROM tickets t "
                + "JOIN flights f ON t.flight_id = f.flight_id "
                + "WHERE f.destination_name = ? AND t.travel_class = ? AND t.seat_number IS NOT NULL";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, destinationName);
            pstmt.setString(2, travelClass);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(rs.getString("seat_number"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Hiba a foglalt székek lekérdezésekor: " + e.getMessage());
        }
        return seats;
    }

    // Foglalások összesítése az admin felülethez
    public List<String> getAllBookingsSummary() {
        List<String> bookings = new ArrayList<>();
        String sql = "SELECT t.ticket_id, t.flight_id, f.destination_name, p.name, p.galactic_id, "
                + "t.travel_class, t.seat_number, t.final_price, t.booking_date "
                + "FROM tickets t "
                + "LEFT JOIN passengers p ON t.galactic_id = p.galactic_id "
                + "LEFT JOIN flights f ON t.flight_id = f.flight_id "
                + "ORDER BY t.booking_date DESC;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String seat = rs.getString("seat_number") != null ? rs.getString("seat_number") : "-";
                String summary = String.format(
                        "Jegy: %s | Járat: %s (%s) | Utas: %s (%s) | Osztály: %s (Szék: %s) | Ár: %.0f Ft | Dátum: %s",
                        rs.getString("ticket_id"),
                        rs.getString("flight_id"),
                        rs.getString("destination_name") != null ? rs.getString("destination_name") : "Ismeretlen",
                        rs.getString("name") != null ? rs.getString("name") : "Ismeretlen",
                        rs.getString("galactic_id"),
                        rs.getString("travel_class"),
                        seat,
                        rs.getDouble("final_price"),
                        rs.getString("booking_date")
                );
                bookings.add(summary);
            }
        } catch (SQLException e) {
            System.err.println("Hiba a foglalások lekérdezésekor: " + e.getMessage());
        }
        return bookings;
    }

    // Jegy törlése és a járat szabad helyének visszaadása tranzakcióban
    public boolean deleteTicket(String ticketId) throws SQLException {
        String findFlightSql = "SELECT flight_id FROM tickets WHERE ticket_id = ?;";
        String deleteSql = "DELETE FROM tickets WHERE ticket_id = ?;";
        String restoreSeatSql = "UPDATE flights SET available_seats = MIN(max_capacity, available_seats + 1), "
                + "status = 'FOGLALHATÓ' WHERE flight_id = ? AND status = 'MEGTELT' OR (flight_id = ? AND available_seats < max_capacity);";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String flightId = null;
                try (PreparedStatement findStmt = conn.prepareStatement(findFlightSql)) {
                    findStmt.setString(1, ticketId);
                    try (ResultSet rs = findStmt.executeQuery()) {
                        if (rs.next()) {
                            flightId = rs.getString("flight_id");
                        }
                    }
                }

                int deleted;
                try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                    delStmt.setString(1, ticketId);
                    deleted = delStmt.executeUpdate();
                }

                if (deleted > 0 && flightId != null) {
                    try (PreparedStatement updStmt = conn.prepareStatement(restoreSeatSql)) {
                        updStmt.setString(1, flightId);
                        updStmt.setString(2, flightId);
                        updStmt.executeUpdate();
                    }
                }

                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
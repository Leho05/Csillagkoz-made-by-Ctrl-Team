package org.csillagkoz.database;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.csillagkoz.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDao {

    // Új jegy elmentése
    public void saveTicket(Connection conn, Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (ticket_id, flight_id, galactic_id, travel_class, final_price, booking_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getFlight().getFlightId());
            pstmt.setString(3, ticket.getPassenger().getGalacticId());
            pstmt.setString(4, ticket.getTravelClass());
            pstmt.setDouble(5, ticket.getFinalPrice());
            pstmt.setString(6, ticket.getBookingDate().toString());

            pstmt.executeUpdate();
        }
    }
    public List<String> getAllBookingsSummary() {
        List<String> bookings = new ArrayList<>();
        String sql = "SELECT t.ticket_id, t.flight_id, f.destination_name, p.name, p.galactic_id, "
                + "t.travel_class, t.final_price, t.booking_date "
                + "FROM tickets t "
                + "LEFT JOIN passengers p ON t.galactic_id = p.galactic_id "
                + "LEFT JOIN flights f ON t.flight_id = f.flight_id "
                + "ORDER BY t.booking_date DESC;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String summary = String.format(
                        "Jegy: %s | Járat: %s (%s) | Utas: %s (%s) | Osztály: %s | Ár: %.0f Ft | Dátum: %s",
                        rs.getString("ticket_id"),
                        rs.getString("flight_id"),
                        rs.getString("destination_name") != null ? rs.getString("destination_name") : "Ismeretlen",
                        rs.getString("name") != null ? rs.getString("name") : "Ismeretlen",
                        rs.getString("galactic_id"),
                        rs.getString("travel_class"),
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
}
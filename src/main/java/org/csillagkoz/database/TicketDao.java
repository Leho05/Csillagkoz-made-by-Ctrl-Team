package org.csillagkoz.database;

import org.csillagkoz.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDao {

    // Új jegy elmentése
    public void saveTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (ticket_id, flight_id, galactic_id, travel_class, final_price, booking_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
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
}
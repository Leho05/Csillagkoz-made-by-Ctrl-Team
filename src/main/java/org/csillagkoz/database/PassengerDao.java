package org.csillagkoz.database;

import org.csillagkoz.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerDao {

    // Utas mentése vagy frissítése (ha már létezik a galacticId)
    public void saveOrUpdatePassenger(Connection conn, Passenger passenger) throws SQLException {
        String sql = "INSERT INTO passengers (galactic_id, name, age) "
                + "VALUES (?, ?, ?) "
                + "ON CONFLICT(galactic_id) DO UPDATE SET "
                + "name = excluded.name, age = excluded.age";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passenger.getGalacticId());
            pstmt.setString(2, passenger.getName());
            pstmt.setInt(3, passenger.getAge());

            pstmt.executeUpdate();
        }
    }

    // Utas lekérdezése azonosító (útlevélszám) alapján
    public Passenger getPassengerById(String galacticId) throws SQLException {
        String sql = "SELECT * FROM passengers WHERE galactic_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, galacticId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Passenger(
                            rs.getString("name"),
                            rs.getString("galactic_id"),
                            rs.getInt("age")
                    );
                }
            }
        }
        return null; // Ha nincs ilyen utas
    }
}

package org.csillagkoz.database;

import org.csillagkoz.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerDao {

    // Utas mentése vagy frissítése (ha már létezik a galacticId)
    public void saveOrUpdatePassenger(Passenger passenger) throws SQLException {
        String sql = "INSERT INTO passengers (galactic_id, name, age, health_certificate) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(galactic_id) DO UPDATE SET "
                + "name = excluded.name, age = excluded.age, health_certificate = excluded.health_certificate";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passenger.getGalacticId());
            pstmt.setString(2, passenger.getName());
            pstmt.setInt(3, passenger.getAge());
            pstmt.setInt(4, passenger.isHealthCertificate() ? 1 : 0);

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
                            rs.getInt("age"),
                            rs.getInt("health_certificate") == 1
                    );
                }
            }
        }
        return null; // Ha nincs ilyen utas
    }
}
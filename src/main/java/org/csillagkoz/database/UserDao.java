package org.csillagkoz.database;

import org.csillagkoz.model.User;
import java.sql.*;

public class UserDao {

    // Bejelentkezés ellenőrzése (a mentett személyes adatokkal együtt)
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setLastName(rs.getString("last_name"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setBirthDate(rs.getString("birth_date"));
                    user.setIdType(rs.getString("id_type"));
                    user.setPassportNumber(rs.getString("passport_number"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    return user;
                }
            }
        }
        return null; // Hibás felhasználónév vagy jelszó
    }

    // Új felhasználó regisztrációja (alapból USER jogosultsággal)
    public boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'USER')";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // Pl. már foglalt a felhasználónév
        }
    }

    // Bejelentkezett felhasználó személyes adatainak elmentése
    public void savePersonalData(User user) throws SQLException {
        String sql = "UPDATE users SET last_name = ?, first_name = ?, birth_date = ?, "
                + "id_type = ?, passport_number = ?, email = ?, phone = ? "
                + "WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLastName());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getBirthDate());
            pstmt.setString(4, user.getIdType());
            pstmt.setString(5, user.getPassportNumber());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPhone());
            pstmt.setString(8, user.getUsername());
            pstmt.executeUpdate();
        }
    }
}

package org.csillagkoz.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:csillagkoz.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase() {
        String createFlightTable = "CREATE TABLE IF NOT EXISTS flights ("
                + "flight_id TEXT PRIMARY KEY, "
                + "destination_name TEXT NOT NULL, "
                + "destination_type TEXT NOT NULL, "
                + "base_price INTEGER NOT NULL, "
                + "departure_time TEXT NOT NULL, "
                + "booking_deadline TEXT NOT NULL, "
                + "max_capacity INTEGER NOT NULL, "
                + "available_seats INTEGER NOT NULL, "
                + "status TEXT NOT NULL"
                + ");";

        String createPassengerTable = "CREATE TABLE IF NOT EXISTS passengers ("
                + "galactic_id TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "age INTEGER NOT NULL, "
                + "health_certificate INTEGER NOT NULL"
                + ");";

        String createTicketTable = "CREATE TABLE IF NOT EXISTS tickets ("
                + "ticket_id TEXT PRIMARY KEY, "
                + "flight_id TEXT NOT NULL, "
                + "galactic_id TEXT NOT NULL, "
                + "travel_class TEXT NOT NULL, "
                + "final_price REAL NOT NULL, "
                + "booking_date TEXT NOT NULL, "
                + "FOREIGN KEY (flight_id) REFERENCES flights(flight_id), "
                + "FOREIGN KEY (galactic_id) REFERENCES passengers(galactic_id)"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createFlightTable);
            stmt.execute(createPassengerTable);
            stmt.execute(createTicketTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
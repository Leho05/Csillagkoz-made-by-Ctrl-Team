package org.csillagkoz.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

            seedInitialData(stmt);

        } catch (SQLException e) {
            System.err.println("Hiba az adatbázis inicializálásakor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flights;")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertFlights = "INSERT INTO flights ("
                        + "flight_id, destination_name, destination_type, base_price, "
                        + "departure_time, booking_deadline, max_capacity, available_seats, status"
                        + ") VALUES "
                        + "('MARS-2026-01', 'Mars Kolónia Alpha', 'PLANET', 180000, '2026-11-10T08:00', '2026-11-08T08:00', 40, 40, 'FOGLALHATÓ'), "
                        + "('MOON-2026-01', 'Luna Bázis', 'MOON', 65000, '2026-11-05T14:30', '2026-11-03T14:30', 50, 50, 'FOGLALHATÓ'), "
                        + "('TITAN-2026-01', 'Titan Kutatóállomás', 'MOON', 320000, '2026-12-01T10:00', '2026-11-28T10:00', 30, 30, 'FOGLALHATÓ'), "
                        + "('EUROPA-2026-01', 'Europa Óceánbázis', 'MOON', 290000, '2026-12-15T09:15', '2026-12-12T09:15', 30, 30, 'FOGLALHATÓ');";

                stmt.executeUpdate(insertFlights);
                System.out.println("Kezdő járatadatok sikeresen inicializálva az adatbázisban.");
            }
        }
    }
}
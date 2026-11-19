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
        String dropTickets = "DROP TABLE IF EXISTS tickets;";
        String dropPassengers = "DROP TABLE IF EXISTS passengers;";
        String dropFlights = "DROP TABLE IF EXISTS flights;";

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
                + "age INTEGER NOT NULL"
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

        String createUserTable = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT PRIMARY KEY, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL, " // "USER" vagy "ADMIN"
                + "last_name TEXT, "
                + "first_name TEXT, "
                + "birth_date TEXT, "
                + "id_type TEXT, "
                + "passport_number TEXT, "
                + "email TEXT, "
                + "phone TEXT"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(dropTickets);
            stmt.execute(dropPassengers);
            stmt.execute(dropFlights);
            //Ezeket kikommentelni ha a foglalásokat meg akarjuk tartani
            stmt.execute(createFlightTable);
            stmt.execute(createPassengerTable);
            stmt.execute(createTicketTable);
            stmt.execute(createUserTable);
            ensureUserProfileColumns(stmt);
            seedInitialData(stmt);

        } catch (SQLException e) {
            System.err.println("Hiba az adatbázis inicializálásakor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ha már létezett egy korábbi users tábla, pótoljuk a hiányzó profil oszlopokat
    private static void ensureUserProfileColumns(Statement stmt) throws SQLException {
        java.util.Set<String> columns = new java.util.HashSet<>();
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(users);")) {
            while (rs.next()) {
                columns.add(rs.getString("name"));
            }
        }
        String[] profileColumns = {
                "last_name", "first_name", "birth_date",
                "id_type", "passport_number", "email", "phone"
        };
        for (String col : profileColumns) {
            if (!columns.contains(col)) {
                stmt.execute("ALTER TABLE users ADD COLUMN " + col + " TEXT;");
            }
        }
    }

    private static void seedInitialData(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flights;")) { //Ezt kivenni ha menteni akarjuk a foglalásokat
            if (rs.next() && rs.getInt(1) == 0) {
                String insertFlights = "INSERT INTO flights ("
                        + "flight_id, destination_name, destination_type, base_price, "
                        + "departure_time, booking_deadline, max_capacity, available_seats, status"
                        + ") VALUES "
                        + "('MARS-2026-01', 'Mars Kolónia Alpha', 'PLANET', 180000, '2026-11-10T08:00', '2026-11-08T08:00', 40, 40, 'FOGLALHATÓ'), "
                        + "('MARS-2026-02', 'Mars Új Kairó', 'PLANET', 195000, '2026-11-15T10:00', '2026-11-13T10:00', 35, 35, 'FOGLALHATÓ'), "
                        + "('MOON-2026-01', 'Luna Bázis (Hold)', 'MOON', 65000, '2026-11-05T14:30', '2026-11-03T14:30', 50, 50, 'FOGLALHATÓ'), "
                        + "('MOON-2026-02', 'Tycho Kráter Kutatóközpont (Hold)', 'MOON', 75000, '2026-11-18T16:00', '2026-11-16T16:00', 45, 45, 'FOGLALHATÓ'), "
                        + "('MOON-2026-03', 'Copernicus Űrkikötő (Hold)', 'MOON', 70000, '2026-11-25T09:00', '2026-11-23T09:00', 60, 60, 'FOGLALHATÓ'), "
                        + "('TITAN-2026-01', 'Titan Kutatóállomás (Hold)', 'MOON', 320000, '2026-12-01T10:00', '2026-11-28T10:00', 30, 30, 'FOGLALHATÓ'), "
                        + "('EUROPA-2026-01', 'Europa Óceánbázis (Hold)', 'MOON', 290000, '2026-12-15T09:15', '2026-12-12T09:15', 30, 30, 'FOGLALHATÓ'), "
                        + "('VENUS-2026-01', 'Vénusz Felhőváros', 'PLANET', 210000, '2026-10-15T06:00', '2026-10-13T06:00', 25, 25, 'FOGLALHATÓ'), "
                        + "('CERES-2026-01', 'Ceres Bányaközpont', 'PLANET', 140000, '2026-11-20T12:00', '2026-11-18T12:00', 20, 20, 'FOGLALHATÓ'), "
                        + "('VESTA-2026-01', 'Vesta Kutatóállomás', 'PLANET', 155000, '2026-11-28T15:30', '2026-11-26T15:30', 20, 20, 'FOGLALHATÓ');";
                String insertUsers = "INSERT OR IGNORE INTO users (username, password, role) VALUES "
                        + "('admin', 'admin123', 'ADMIN'), "
                        + "('utas', 'utas123', 'USER');";
                String seedDefaultPassengerProfile = "UPDATE users SET "
                        + "last_name = 'Kovács', "
                        + "first_name = 'János', "
                        + "birth_date = '1995-05-15', "
                        + "id_type = 'PASSPORT', "
                        + "passport_number = 'AB123456', "
                        + "email = 'utas@csillagkoz.hu', "
                        + "phone = '+36301234567' "
                        + "WHERE username = 'utas' AND (last_name IS NULL OR last_name = '');";

                stmt.executeUpdate(insertFlights);
                stmt.executeUpdate(insertUsers);
                stmt.executeUpdate(seedDefaultPassengerProfile);

                System.out.println("Járatok sikeresen inicializálva az adatbázisba.");
            }
        }
    }
}
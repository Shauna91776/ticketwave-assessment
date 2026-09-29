package za.co.ticketwave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * These tests seed a small database and run the queries in EventQueries.
 * They need a correct DatabaseSchema (Q7.2) to pass.
 *
 * Seed data:
 *   customers : 1 Lerato Dlamini, 2 Kabelo Nkosi, 3 Megan Jacobs
 *   events    : 1 Jazz Night    (2026-11-14, capacity 10)
 *               2 Tech Summit   (2026-10-02, capacity 5)
 *               3 Comedy Club   (2026-12-01, capacity 50)
 *               4 Food Festival (2026-10-20, capacity 4)
 *               5 Rock Fest     (2027-01-15, capacity 8)
 *   bookings  : Lerato -> Jazz Night x6, Tech Summit x3      (CONFIRMED)
 *               Kabelo -> Jazz Night x4, Tech Summit x2      (CONFIRMED)
 *               Megan  -> Food Festival x4 (CANCELLED), Rock Fest x5 (CONFIRMED)
 */
class EventQueriesTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
        seed();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void bookingsForCustomer_listsTheirBookingsEarliestEventFirst() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(EventQueries.BOOKINGS_FOR_CUSTOMER)) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(rs.getString("title") + ":" + rs.getInt("quantity"));
                }
            }
        }
        assertEquals(List.of("Tech Summit:3", "Jazz Night:6"), rows);
    }

    @Test
    void bookingsForCustomer_includesCancelledBookings() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(EventQueries.BOOKINGS_FOR_CUSTOMER)) {
            ps.setInt(1, 3);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(rs.getString("title") + ":" + rs.getInt("quantity"));
                }
            }
        }
        assertEquals(List.of("Food Festival:4", "Rock Fest:5"), rows);
    }

    @Test
    void bookingsForCustomer_isEmptyForUnknownCustomer() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(EventQueries.BOOKINGS_FOR_CUSTOMER)) {
            ps.setInt(1, 99);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next());
            }
        }
    }

    @Test
    void ticketsSoldPerEvent_countsConfirmedOnlyAndKeepsEmptyEvents() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(EventQueries.TICKETS_SOLD_PER_EVENT)) {
            while (rs.next()) {
                rows.add(rs.getString("title") + ":" + rs.getInt("tickets_sold"));
            }
        }
        assertEquals(List.of(
                "Jazz Night:10", "Rock Fest:5", "Tech Summit:5", "Comedy Club:0", "Food Festival:0"), rows);
    }

    @Test
    void soldOutEvents_usesConfirmedTicketsAgainstCapacity() throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(EventQueries.SOLD_OUT_EVENTS)) {
            while (rs.next()) {
                rows.add(rs.getString("title"));
            }
        }
        assertEquals(List.of("Jazz Night", "Tech Summit"), rows);
    }

    private void seed() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (1, 'Lerato Dlamini', 'lerato@example.com')");
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (2, 'Kabelo Nkosi', 'kabelo@example.com')");
            s.execute("INSERT INTO customers (id, full_name, email) VALUES (3, 'Megan Jacobs', 'megan@example.com')");

            s.execute("INSERT INTO events (id, title, city, event_date, capacity) VALUES (1, 'Jazz Night', 'Cape Town', '2026-11-14', 10)");
            s.execute("INSERT INTO events (id, title, city, event_date, capacity) VALUES (2, 'Tech Summit', 'Johannesburg', '2026-10-02', 5)");
            s.execute("INSERT INTO events (id, title, city, event_date, capacity) VALUES (3, 'Comedy Club', 'Pretoria', '2026-12-01', 50)");
            s.execute("INSERT INTO events (id, title, city, event_date, capacity) VALUES (4, 'Food Festival', 'Durban', '2026-10-20', 4)");
            s.execute("INSERT INTO events (id, title, city, event_date, capacity) VALUES (5, 'Rock Fest', 'Bloemfontein', '2027-01-15', 8)");

            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (1, 1, 1, 6, 'CONFIRMED')");
            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (2, 1, 2, 3, 'CONFIRMED')");
            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (3, 2, 1, 4, 'CONFIRMED')");
            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (4, 2, 2, 2, 'CONFIRMED')");
            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (5, 3, 4, 4, 'CANCELLED')");
            s.execute("INSERT INTO bookings (id, customer_id, event_id, quantity, status) VALUES (6, 3, 5, 5, 'CONFIRMED')");
        }
    }
}

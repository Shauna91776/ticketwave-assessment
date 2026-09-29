package za.co.ticketwave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // ---- customers -----------------------------------------------------

    @Test
    void customers_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("customers");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("TEXT", types.get("full_name"));
        assertEquals("TEXT", types.get("email"));
    }

    @Test
    void customers_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("customers", "pk").get("id"));
    }

    @Test
    void customers_nameAndEmailAreNotNullAndEmailIsUnique() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("customers", "notnull");
        assertTrue(notNull.get("full_name"));
        assertTrue(notNull.get("email"));
        execute("INSERT INTO customers (full_name, email) VALUES ('Lerato Dlamini', 'lerato@example.com')");
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO customers (full_name, email) VALUES ('Other', 'lerato@example.com')"));
    }

    // ---- events --------------------------------------------------------

    @Test
    void events_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("events");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("TEXT", types.get("title"));
        assertEquals("TEXT", types.get("city"));
        assertEquals("TEXT", types.get("event_date"));
        assertEquals("INTEGER", types.get("capacity"));
    }

    @Test
    void events_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("events", "pk").get("id"));
    }

    @Test
    void events_everyColumnExceptIdIsNotNull() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("events", "notnull");
        assertTrue(notNull.get("title"));
        assertTrue(notNull.get("city"));
        assertTrue(notNull.get("event_date"));
        assertTrue(notNull.get("capacity"));
    }

    @Test
    void events_capacityMustBePositive() {
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO events (title, city, event_date, capacity) VALUES ('X', 'Y', '2026-11-01', 0)"));
    }

    // ---- bookings ------------------------------------------------------

    @Test
    void bookings_columnTypes() throws SQLException {
        Map<String, String> types = columnTypes("bookings");
        assertEquals("INTEGER", types.get("id"));
        assertEquals("INTEGER", types.get("customer_id"));
        assertEquals("INTEGER", types.get("event_id"));
        assertEquals("INTEGER", types.get("quantity"));
        assertEquals("TEXT", types.get("status"));
    }

    @Test
    void bookings_hasIdAsPrimaryKey() throws SQLException {
        assertTrue(columnFlags("bookings", "pk").get("id"));
    }

    @Test
    void bookings_requiredColumnsAreNotNull() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("bookings", "notnull");
        assertTrue(notNull.get("customer_id"));
        assertTrue(notNull.get("event_id"));
        assertTrue(notNull.get("quantity"));
        assertTrue(notNull.get("status"));
    }

    @Test
    void bookings_statusDefaultsToConfirmed() throws SQLException {
        seedCustomerAndEvent();
        execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 2)");
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT status FROM bookings")) {
            assertTrue(rs.next());
            assertEquals("CONFIRMED", rs.getString("status"));
        }
    }

    @Test
    void bookings_quantityMustBeBetweenOneAndEight() throws SQLException {
        seedCustomerAndEvent();
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 0)"));
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 9)"));
        execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 8)");
    }

    @Test
    void bookings_aCustomerCannotBookTheSameEventTwice() throws SQLException {
        seedCustomerAndEvent();
        execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 2)");
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 1, 3)"));
    }

    @Test
    void bookings_declaresBothForeignKeys() throws SQLException {
        Set<String> foreignKeys = new HashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA foreign_key_list(bookings)")) {
            while (rs.next()) {
                foreignKeys.add(rs.getString("from") + "->" + rs.getString("table") + "." + rs.getString("to"));
            }
        }
        assertEquals(Set.of("customer_id->customers.id", "event_id->events.id"), foreignKeys);
    }

    @Test
    void bookings_cannotReferenceACustomerOrEventThatDoesNotExist() throws SQLException {
        seedCustomerAndEvent();
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (99, 1, 2)"));
        assertThrows(SQLException.class, () ->
                execute("INSERT INTO bookings (customer_id, event_id, quantity) VALUES (1, 99, 2)"));
    }

    // ---- helpers -------------------------------------------------------

    private void seedCustomerAndEvent() throws SQLException {
        execute("INSERT INTO customers (full_name, email) VALUES ('Lerato Dlamini', 'lerato@example.com')");
        execute("INSERT INTO events (title, city, event_date, capacity) VALUES ('Jazz Night', 'Cape Town', '2026-11-14', 100)");
    }

    private void execute(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private Map<String, String> columnTypes(String table) throws SQLException {
        Map<String, String> types = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                types.put(rs.getString("name"), rs.getString("type").toUpperCase());
            }
        }
        return types;
    }

    private Map<String, Boolean> columnFlags(String table, String flagColumn) throws SQLException {
        Map<String, Boolean> flags = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                flags.put(rs.getString("name"), rs.getInt(flagColumn) > 0);
            }
        }
        return flags;
    }
}

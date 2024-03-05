package at.fhtw.httpserver.database;

import at.fhtw.sampleapp.persistence.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseManagerTest {

    @Test
    public void testGetConnection() {
        DatabaseManager databaseManager = DatabaseManager.INSTANCE;
        Connection connection = databaseManager.getConnection();
        assertNotNull(connection, "The database connection should not be null");
    }
}
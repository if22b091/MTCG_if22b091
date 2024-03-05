package at.fhtw.sampleapp.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnitOfWork implements AutoCloseable{

    private Connection connection;

    // Constructor: Initializes a new UnitOfWork and disables auto-commit to manage transactions manually
    public UnitOfWork() {
        // Retrieve a database connection from the DatabaseManager singleton
        this.connection = DatabaseManager.INSTANCE.getConnection();

        try {
            // Disables auto-commit to allow manual transaction management
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            // Throws a custom exception if setting auto-commit to false fails
            throw new DataAccessException("Failed to disable auto-commit", e);
        }
    }

    // Commits the current transaction to the database
    public void commitTransaction() {
        if (this.connection != null) {
            try {
                // Attempt to commit the transaction
                this.connection.commit();
            } catch (SQLException e) {
                // Throws a custom exception if the commit fails
                throw new DataAccessException("Transaction commit failed", e);
            }
        }
    }

    // Rolls back the current transaction
    public void rollbackTransaction() {
        if (this.connection != null) {
            try {
                // Attempt to rollback the transaction
                this.connection.rollback();
            } catch (SQLException e) {
                // Throws a custom exception if the rollback fails
                throw new DataAccessException("Transaction rollback failed", e);
            }
        }
    }

    // Closes the database connection and cleans up resources
    public void finishWork() {
        if (this.connection != null) {
            try {
                // Attempt to close the connection
                this.connection.close();
                // Nullify the connection object to mark it as closed
                this.connection = null;
            } catch (SQLException e) {
                // Throws a custom exception if closing the connection fails
                throw new DataAccessException("Failed to close the connection", e);
            }
        }
    }

    // Prepares a SQL statement for execution
    public PreparedStatement prepareStatement(String sql) {
        if (this.connection != null) {
            try {
                // Prepare and return the SQL statement
                return this.connection.prepareStatement(sql);
            } catch (SQLException e) {
                // Throws a custom exception if preparing the statement fails
                throw new DataAccessException("Preparing a PreparedStatement failed", e);
            }
        }
        // Throws an exception if there is no active connection
        throw new DataAccessException("UnitOfWork has no active connection available.");
    }

    // Ensures resources are cleaned up when the object is closed or goes out of scope
    @Override
    public void close() {
        this.finishWork();
    }

    // Allows setting a different database connection if needed
    public void setConnection(Connection conn) {
        this.connection = conn;
    }
}

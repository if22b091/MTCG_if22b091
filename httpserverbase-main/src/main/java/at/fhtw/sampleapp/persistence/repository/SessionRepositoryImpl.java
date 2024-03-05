package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Implementation of the SessionRepository interface for finding users in the database.
public class SessionRepositoryImpl implements SessionRepository {
    private UnitOfWork unitOfWork;

    public SessionRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Attempts to find a user in the database matching the provided username and password.
    public boolean findUser(User user) {
        // SQL query to search for a user with a specific username and password.
        String sql = "SELECT * FROM public.users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            // Logs the SQL query being executed for debugging purposes.
            System.out.println("Executing SQL query: " + preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            // If the query does not find a match, logs this fact and returns false.
            if (!resultSet.next()) {
                System.out.println("No user found");
                return false;
            } else {
                // If a matching user is found, logs this and returns true.
                System.out.println("User found");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // If there's a SQL exception, wraps and throws it as a custom DataAccessException.
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }
}

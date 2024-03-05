package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Implements the UserRepository interface for database operations related to users.
public class UserRepositoryImpl implements UserRepository {

    private UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Searches for a user in the database by their username.
    public User searchByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?"; // SQL to fetch user details.
            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                User user = null;
                // Creates a User object if a matching record is found.
                while (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"));
                    // Sets additional properties of the User object.
                    user.setCoins(resultSet.getInt("coins"));
                    user.setName(resultSet.getString("name"));
                    user.setBio(resultSet.getString("bio"));
                    user.setImage(resultSet.getString("image"));
                }

                if (user == null) {
                    throw new DataAccessException("No user found");
                }

                return user;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Select operation failed", e);
        }
    }

    // Adds a new user to the database, ensuring the username does not already exist.
    public void addUser(User user) {
        try {
            // Checks if the user already exists to prevent duplicate entries.
            User existingUser = null;
            try {
                existingUser = searchByUsername(user.getUsername());
            } catch (DataAccessException e) {
                if (!e.getMessage().equals("No user found")) {
                    throw e;
                }
            }
            if (existingUser != null) {
                throw new DataAccessException("Username already taken");
            }

            // Inserts the new user into the database.
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.executeUpdate();
            }

            this.unitOfWork.commitTransaction(); // Commits the transaction after successful insertion.

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back in case of failure.
            throw new DataAccessException("Insert operation failed", e);
        }
    }

    // Inserts a new username into the 'deck' table to initialize their deck.
    public void insertUsernameToDeck(String username) {
        try {
            String insertSql = "INSERT INTO deck (username) VALUES (?)"; // SQL to insert username into 'deck'.
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                insertStatement.setString(1, username);
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into deck table");

            this.unitOfWork.commitTransaction(); // Commits the transaction after successful insertion.

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into deck", e);
        }
    }

    // Similarly inserts a username into the 'stats' table to initialize their stats.
    public void insertUsernameToStats(String username) {
        try {
            String insertSql = "INSERT INTO stats (username) VALUES (?)"; // SQL to insert username into 'stats'.
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                insertStatement.setString(1, username);
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into stats table");

            this.unitOfWork.commitTransaction(); // Commits the transaction after successful insertion.

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into stats", e);
        }
    }

    // Updates user details in the database.
    public void editUser(String username, User user) {
        try {
            // Checks if the user exists before attempting to update.
            User existingUser = searchByUsername(user.getUsername());
            if (existingUser == null) {
                throw new DataAccessException("No user found");
            }

            // SQL to update user details.
            String sql = "UPDATE public.users SET name = ?, bio = ?, image = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getBio());
                preparedStatement.setString(3, user.getImage());
                preparedStatement.setString(4, username);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }

            this.unitOfWork.commitTransaction(); // Commits the transaction after successful update.

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Update operation failed", e);
        }
    }
}

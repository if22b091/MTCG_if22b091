package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

// Implementation of the StatsRepository interface for retrieving user statistics and the scoreboard from the database.
public class StatsRepositoryImpl implements StatsRepository {

    private UnitOfWork unitOfWork;

    public StatsRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Retrieves the statistics by username.
    public Map<String, Object> getUserStats(String username) {
        Map<String, Object> stat = new LinkedHashMap<>(); // Uses LinkedHashMap to maintain order of insertion.

        // SQL query to select the user's statistics from the stats table.
        String selectSql = "SELECT username, elo, wins, losses, total_games, win_ratio FROM stats WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            // If the user is found, extract their stats and put them into the map.
            if (resultSet.next()) {
                stat.put("Username", resultSet.getString("username"));
                stat.put("Elo", resultSet.getInt("elo"));
                stat.put("Wins", resultSet.getInt("wins"));
                stat.put("Losses", resultSet.getInt("losses"));
                stat.put("Total Games", resultSet.getInt("total_games"));
                stat.put("Win Ratio", resultSet.getDouble("win_ratio"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get stats", e); // Throws a custom exception if there's a SQL error.
        }

        return stat; // Returns the user's stats as a map.
    }

    // Retrieves the scoreboard, ranking users by their Elo rating in descending order.
    public List<Map<String, Object>> getScoreboard() {
        List<Map<String, Object>> stats = new ArrayList<>();

        // SQL query to select all users' stats and order them by Elo rating, highest first.
        String selectSql = "SELECT username, elo, wins, losses, total_games, win_ratio FROM stats ORDER BY elo DESC";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            ResultSet resultSet = selectStatement.executeQuery();

            // Iterates over each row in the result set, extracting user stats and adding them to the list.
            while (resultSet.next()) {
                Map<String, Object> stat = new LinkedHashMap<>();
                stat.put("Username", resultSet.getString("username"));
                stat.put("Elo", resultSet.getInt("elo"));
                stat.put("Wins", resultSet.getInt("wins"));
                stat.put("Losses", resultSet.getInt("losses"));
                stat.put("Total Games", resultSet.getInt("total_games"));
                stat.put("Win Ratio", resultSet.getDouble("win_ratio"));
                stats.add(stat); // Adds each user's stats to the list.
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get scoreboard", e); // Throws a custom exception if there's a SQL error.
        }

        return stats; // Returns the list of all users' stats for the scoreboard.
    }

}

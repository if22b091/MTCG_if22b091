package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Implements the BattleRepository interface for database operations related to battles
public class BattleRepositoryImpl implements BattleRepository {

    private UnitOfWork unitOfWork;

    // Constructor initializing the UnitOfWork, ensuring transactions are managed
    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Fetches a user's battle deck by their username
    public List<Card> fetchBattleDeckByUsername(String username) {
        List<Card> cards = new ArrayList<>();

        // SQL query to select card IDs from a user's deck
        String selectSql = "SELECT card1_id, card2_id, card3_id, card4_id FROM deck WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                // Loop through each card ID and fetch details from the cards table
                for (int i = 1; i <= 4; i++) {
                    String cardId = resultSet.getString("card" + i + "_id");

                    String cardSql = "SELECT id, name, damage, element, type FROM cards WHERE id = ?";
                    PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                    cardStatement.setString(1, cardId);
                    ResultSet cardResultSet = cardStatement.executeQuery();

                    while (cardResultSet.next()) {
                        // Create Card objects from the retrieved data and add them to the list
                        String id = cardResultSet.getString("id");
                        String name = cardResultSet.getString("name");
                        int damage = cardResultSet.getInt("damage");
                        int elementOrdinal = cardResultSet.getInt("element");
                        CardElement element = CardElement.values()[elementOrdinal];
                        int typeOrdinal = cardResultSet.getInt("type");
                        CardType type = CardType.values()[typeOrdinal];

                        Card card = new Card(id, name, damage, element, type);
                        cards.add(card);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to select cards from deck for username", e);
        }

        // Throws an exception if no cards were found
        if (cards.isEmpty()) {
            throw new DataAccessException("No cards found");
        }

        return cards;
    }

    // Increases a user's ELO rating
    public void increaseElo(String username) {
        // SQL to increment the ELO score
        String updateSql = "UPDATE stats SET elo = elo + 3 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to increase elo for username", e);
        }
    }

    // Decreases a user's ELO rating
    public void decreaseElo(String username) {
        // SQL to decrement the ELO score
        String updateSql = "UPDATE stats SET elo = elo - 5 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to decrease elo for username", e);
        }
    }

    // Increments the number of victories for a user
    public void incrementUserVictories(String username) {
        // SQL to increment the win count
        String updateSql = "UPDATE stats SET wins = wins + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to increase wins for username", e);
        }
    }

    // Increments the number of defeats for a user
    public void incrementUserDefeats(String username) {
        // SQL to increment the loss count
        String updateSql = "UPDATE stats SET losses = losses + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to increase losses for username", e);
        }
    }

    // Rewards the winner with coins
    public void rewardWinnerWithCoins(String username) {
        // SQL to increment the coin count
        String updateSql = "UPDATE users SET coins = coins + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to increase coins for username", e);
        }
    }

    // Updates the total number of games played by a user
    public void updateGamesPlayedByUser(String username) {
        // SQL to increment the total games played
        String updateSql = "UPDATE stats SET total_games = total_games + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to increase total games for username", e);
        }
    }

    // Calculates and updates a user's win rate
    public void calculateUserWinRate(String username) {
        // SQL to select wins and total games for calculating the win rate
        String selectSql = "SELECT wins, total_games FROM stats WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int wins = resultSet.getInt("wins");
                int totalGames = resultSet.getInt("total_games");

                double winRatio = (double) wins / totalGames; // Calculates the win ratio

                // SQL to update the win ratio
                String updateSql = "UPDATE stats SET win_ratio = ? WHERE username = ?";
                try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
                    updateStatement.setDouble(1, winRatio);
                    updateStatement.setString(2, username);
                    updateStatement.executeUpdate();

                    unitOfWork.commitTransaction(); // Commits the transaction
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of an error
            throw new DataAccessException("Failed to calculate win ratio for username", e);
        }
    }

}

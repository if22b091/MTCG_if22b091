package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Implements the PackagesRepository interface to manage card packages in the database.
public class PackagesRepositoryImpl implements PackagesRepository {

    private UnitOfWork unitOfWork;

    public PackagesRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Checks if a card with a specific ID already exists in the database.
    public boolean checkIfCardExists(String cardId) {
        String selectSql = "SELECT COUNT(*) FROM cards WHERE id = ?"; // SQL query to count cards with the given ID.
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(selectSql)) {
            preparedStatement.setString(1, cardId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next(); // Moves to the first record (there should only be one due to COUNT).
            return resultSet.getInt(1) > 0; // Returns true if the count is greater than 0, indicating the card exists.
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error checking if card exists", e);
        }
    }

    // Adds a list of cards to the database, including creating package entries.
    public void addCards(List<Card> cards) {
        String insertCardsSql = "INSERT INTO cards (id, name, damage, element, type) VALUES (?, ?, ?, ?, ?)"; // SQL to insert card details.
        try (PreparedStatement preparedStatementCards = this.unitOfWork.prepareStatement(insertCardsSql))
        {
            for (Card card : cards) {
                if (checkIfCardExists(card.getId())) {
                    // If the card already exists, throw an exception to avoid duplicates.
                    throw new DataAccessException("Card already exists");
                } else {
                    // Sets the card details for insertion.
                    preparedStatementCards.setString(1, card.getId());
                    preparedStatementCards.setString(2, card.getName());
                    preparedStatementCards.setInt(3, card.getDamage());
                    preparedStatementCards.setInt(4, card.getElement());
                    preparedStatementCards.setInt(5, card.getType());
                    preparedStatementCards.addBatch(); // Adds to batch for execution.
                }
            }
            preparedStatementCards.executeBatch(); // Executes the batch of insert operations.

            // After cards are inserted, create a package entry linking to the cards.
            String insertPackagesSql = "INSERT INTO packages (card1_id, card2_id, card3_id, card4_id, card5_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementPackages = this.unitOfWork.prepareStatement(insertPackagesSql);
            // Assumes the list size is at least 5 for a full package, this part might need adjustment for dynamic package sizes.
            for (int i = 0; i < cards.size(); i++) {
                preparedStatementPackages.setString(i + 1, cards.get(i).getId());
            }
            preparedStatementPackages.executeUpdate(); // Executes the package insertion.

            unitOfWork.commitTransaction(); // Commits the transaction to finalize changes.
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back the transaction in case of failure.
            throw new DataAccessException("Insert not successful", e);
        }
    }
}

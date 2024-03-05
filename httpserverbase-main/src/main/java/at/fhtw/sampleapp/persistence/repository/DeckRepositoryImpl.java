package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

// This class is responsible for managing deck-related data in the database.
public class DeckRepositoryImpl implements DeckRepository {

    private UnitOfWork unitOfWork;

    public DeckRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Selects a deck for a user by username and returns it in the specified format.
    public List<?> selectDeck(String username, String format) {
        List<Map<String, Object>> cards = new ArrayList<>();
        // SQL query to get the IDs of cards in a user's deck.
        String selectSql = "SELECT card1_id, card2_id, card3_id, card4_id FROM deck WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();
            // Loops through each card slot in the deck.
            while (resultSet.next()) {
                for (int i = 1; i <= 4; i++) {
                    String cardId = resultSet.getString("card" + i + "_id");

                    // Fetches details for each card by ID.
                    String cardSql = "SELECT id, name, damage, element FROM cards WHERE id = ?";
                    PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                    cardStatement.setString(1, cardId);
                    ResultSet cardResultSet = cardStatement.executeQuery();

                    // Adds card details to the list of cards.
                    while (cardResultSet.next()) {
                        Map<String, Object> card = new HashMap<>();
                        card.put("ID", cardResultSet.getString("id"));
                        card.put("Name", cardResultSet.getString("name"));
                        card.put("Damage", cardResultSet.getInt("damage"));
                        cards.add(card);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to select cards from deck for username", e);
        }

        if (cards.isEmpty()) {
            throw new DataAccessException("No cards found");
        }

        // Returns the deck in the specified format: plain text or as a map.
        if ("plain".equals(format)) {
            return cards.stream().map(card -> "Name: " + card.get("Name").toString() +
                            ", Damage: " + card.get("Damage").toString()).
                    collect(Collectors.toList());
        } else {
            return cards;
        }
    }

    // Public method to get a deck in the specified format, handling any exceptions.
    public List<?> getDeck(String username, String format) {
        List<?> deck;
        try {
            deck = selectDeck(username, format);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get deck", e);
        }
        return deck;
    }

    // Verifies if a user owns all specified cards.
    public boolean verifyOwnershipOfCards(String username, List<String> cardIds) {
        // Prepares SQL with placeholders for card IDs to check ownership.
        String placeholders = String.join(",", Collections.nCopies(cardIds.size(), "?"));
        String selectSql = "SELECT card_id FROM bought_cards WHERE username = ? AND card_id IN (" + placeholders + ")";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            // Sets each card ID in the query.
            for (int i = 0; i < cardIds.size(); i++) {
                selectStatement.setString(i + 2, cardIds.get(i));
            }
            ResultSet resultSet = selectStatement.executeQuery();

            // Counts the number of cards found to match with the number of card IDs provided.
            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            // Returns true if all cards are owned by the user.
            return rowCount == cardIds.size();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to check if user owns cards", e);
        }
    }

    // Updates the deck for a user with new card IDs.
    public void updateDeck(String username, List<String> cardIds) {
        // Prints the card IDs to update for debugging.
        System.out.println("updateDeck: " + cardIds);

        // SQL query to update the deck with new card IDs.
        String updateSql = "UPDATE deck SET card1_id = ?, card2_id = ?, card3_id = ?, card4_id = ? WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            // Sets each card ID in the update statement.
            for (int i = 0; i < cardIds.size(); i++) {
                updateStatement.setString(i + 1, cardIds.get(i));
            }
            updateStatement.setString(5, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction(); // Commits the transaction to save changes.

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction(); // Rolls back in case of an error.
            throw new DataAccessException("Failed to update deck for username", e);
        }
    }
}

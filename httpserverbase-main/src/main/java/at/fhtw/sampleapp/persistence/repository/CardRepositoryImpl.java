package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implements the CardRepository interface, focusing on card-related data interactions.
public class CardRepositoryImpl implements CardRepository{

    private UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Fetches a list of cards owned by a user, identified by their username.
    public List<Map<String, Object>> getCards(String username) {
        List<Map<String, Object>> cards = new ArrayList<>();
        // SQL query to find card IDs in the 'bought_cards' table for a specific user.
        String sql = "SELECT card_id FROM bought_cards WHERE username = ?";
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql))
        {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Loop through the result set to fetch each card's details.
            while (resultSet.next()) {
                String cardId = resultSet.getString("card_id");

                // SQL query to fetch card details from the 'cards' table based on ID.
                String cardSql = "SELECT id, name, damage FROM cards WHERE id = ?";
                PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                cardStatement.setString(1, cardId);
                ResultSet cardResultSet = cardStatement.executeQuery();

                // Map each card's details and add them to the list of cards.
                while (cardResultSet.next()) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("ID", cardResultSet.getString("id"));
                    card.put("Name", cardResultSet.getString("name"));
                    card.put("Damage", cardResultSet.getInt("damage"));
                    cards.add(card);
                }
            }
            // If no cards are found for the user, throw an exception indicating so.
            if (cards.isEmpty()) {
                throw new DataAccessException("No cards found");
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            // On failure, throw a DataAccessException with a message indicating the failure to get cards.
            throw new DataAccessException("Failed to get cards", e);
        }
    }

}

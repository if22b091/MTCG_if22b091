package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.DeckRepository;
import at.fhtw.sampleapp.persistence.repository.DeckRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class DeckService extends AbstractService {

    private DeckRepository getDeckRepository(UnitOfWork unitOfWork) {
        return new DeckRepositoryImpl(unitOfWork);
    }

    // Retrieves a user's deck.
    public List<?> getDeck(String username, String format) {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            DeckRepository deckRepository = getDeckRepository(unitOfWork);
            // Fetch and return the deck for the given username and format.
            return deckRepository.getDeck(username, format);
        }
    }

    // Updates the deck for a given user based on the request payload.
    public void updateDeck(String username, Request request) throws Exception {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            DeckRepository deckRepository = getDeckRepository(unitOfWork);
            String body = request.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            // Parse the request body to extract card IDs.
            JsonNode jsonNode = objectMapper.readTree(body);

            List<String> cardIds = new ArrayList<>();
            // Iterate over the JSON array to collect card IDs.
            if (jsonNode.isArray()) {
                for (JsonNode cardIdNode : jsonNode) {
                    cardIds.add(cardIdNode.asText());
                }
            }

            // Validate that exactly 4 cards are provided for the deck.
            if (cardIds.size() != 4) {
                throw new IllegalArgumentException("not enough cards provided");
            }

            // Verify that the user owns the cards they want to add to their deck.
            boolean userOwnsCards = deckRepository.verifyOwnershipOfCards(username, cardIds);
            if (!userOwnsCards) {
                throw new IllegalArgumentException("user does not own all cards");
            }

            // Update the user's deck with the new set of cards.
            deckRepository.updateDeck(username, cardIds);
        }
    }
}

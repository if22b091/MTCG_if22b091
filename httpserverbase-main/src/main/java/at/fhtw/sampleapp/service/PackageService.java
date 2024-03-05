package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.PackagesRepository;
import at.fhtw.sampleapp.persistence.repository.PackagesRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class PackageService extends AbstractService {

    // Adds a list of cards to the package repository.
    public void addCards(Request request) throws Exception {
        // Automatically manage the lifecycle of UnitOfWork to ensure resources are released properly.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            PackagesRepository packagesRepository = new PackagesRepositoryImpl(unitOfWork);
            Card card;
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            // Parse the JSON body from the request into a JsonNode.
            JsonNode jsonNode = objectMapper.readTree(body);

            List<Card> cards = new ArrayList<>();
            // Iterate over the JSON array to extract card details and create Card objects.
            for (JsonNode cardNode : jsonNode) {
                String id = cardNode.get("Id").asText();
                String name = cardNode.get("Name").asText();
                Integer damage = cardNode.get("Damage").asInt();
                // Determine the card's element based on its name.
                CardElement element;
                if (name.contains("Fire")) {
                    element = CardElement.FIRE;
                } else if (name.contains("Water")) {
                    element = CardElement.WATER;
                } else {
                    element = CardElement.NORMAL;
                }
                // Determine the card's type (Spell or Monster) based on its name.
                CardType type;
                if (name.contains("Spell")) {
                    type = CardType.SPELL;
                } else {
                    type = CardType.MONSTER;
                }

                // Create a new Card object with the parsed details.
                card = new Card(id, name, damage, element, type);
                // Add the newly created card to the list of cards to be added.
                cards.add(card);
            }

            // Use the repository to add the list of cards to the database.
            packagesRepository.addCards(cards);
        }
    }
}

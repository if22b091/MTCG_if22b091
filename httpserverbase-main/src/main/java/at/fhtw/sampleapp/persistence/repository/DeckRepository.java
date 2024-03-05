package at.fhtw.sampleapp.persistence.repository;

import java.util.List;

public interface DeckRepository {
    List<?> getDeck(String username, String format);

    public boolean verifyOwnershipOfCards(String username, List<String> cardIds);

    public void updateDeck(String username, List<String> cardIds);

}

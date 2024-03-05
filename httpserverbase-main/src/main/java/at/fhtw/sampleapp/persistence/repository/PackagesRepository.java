package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;

import java.util.List;

public interface PackagesRepository {

    public boolean checkIfCardExists(String cardId);

    void addCards(List<Card> cards);

}

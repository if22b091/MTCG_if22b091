package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.CardRepository;
import at.fhtw.sampleapp.persistence.repository.CardRepositoryImpl;

import java.util.List;
import java.util.Map;

public class CardService extends AbstractService{
    public List<Map<String, Object>> getCards(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            CardRepository cardRepository = new CardRepositoryImpl(unitOfWork);
            return cardRepository.getCards(username);
        }

    }
}

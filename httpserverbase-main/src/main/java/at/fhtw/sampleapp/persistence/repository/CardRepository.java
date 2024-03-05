package at.fhtw.sampleapp.persistence.repository;

import java.util.List;
import java.util.Map;

public interface CardRepository {
    List<Map<String, Object>> getCards(String username);
}

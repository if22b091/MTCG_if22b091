package at.fhtw.sampleapp.persistence.repository;


import java.util.List;
import java.util.Map;

public interface TransactionRepository {

    List<Map<String, Object>> selectCards();

    void buyPacks(String username);

}

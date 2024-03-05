package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.TransactionRepository;
import at.fhtw.sampleapp.persistence.repository.TransactionRepositoryImpl;

import java.util.List;
import java.util.Map;

public class TransactionService extends AbstractService {

    private TransactionRepository getTransactionRepository(UnitOfWork unitOfWork) {
        return new TransactionRepositoryImpl(unitOfWork);
    }

    // Selects cards, typically for the purpose of displaying available cards to the user.
    public List<Map<String, Object>> selectCards() {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            TransactionRepository transactionRepository = getTransactionRepository(unitOfWork);
            // Fetch and return the selected cards.
            return transactionRepository.selectCards();
        }
    }

    // Facilitates the acquisition of card packages by a user.
    public void acquirePackages(String username) {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            TransactionRepository transactionRepository = getTransactionRepository(unitOfWork);
            // Process the package purchase for the specified user.
            transactionRepository.buyPacks(username);
        }
    }
}

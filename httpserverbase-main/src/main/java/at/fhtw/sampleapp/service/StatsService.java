package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.StatsRepository;
import at.fhtw.sampleapp.persistence.repository.StatsRepositoryImpl;

import java.util.List;
import java.util.Map;

public class StatsService extends AbstractService {

    private StatsRepository getStatsRepository(UnitOfWork unitOfWork) {
        return new StatsRepositoryImpl(unitOfWork);
    }

    // Retrieves statistics for a specific user.
    public Map<String, Object> getStats(String username) {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            StatsRepository statsRepository = getStatsRepository(unitOfWork);
            // Fetch and return the statistics for the given username.
            return statsRepository.getUserStats(username);
        }
    }

    // Retrieves the game scoreboard, a list of user stats.
    public List<Map<String, Object>> getScoreboard() {
        // Use try-with-resources to ensure that the UnitOfWork resource is closed automatically.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            StatsRepository statsRepository = getStatsRepository(unitOfWork);
            // Fetch and return the scoreboard.
            return statsRepository.getScoreboard();
        }
    }
}

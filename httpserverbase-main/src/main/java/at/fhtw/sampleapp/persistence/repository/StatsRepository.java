package at.fhtw.sampleapp.persistence.repository;

import java.util.List;
import java.util.Map;

public interface StatsRepository {

    Map<String, Object> getUserStats(String username);
    List<Map<String, Object>> getScoreboard();

}

package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;

import java.util.List;

public interface BattleRepository {

    List<Card> fetchBattleDeckByUsername(String username);

    void increaseElo(String username);

    void decreaseElo(String username);

    void incrementUserVictories(String username);

    void incrementUserDefeats(String username);

    void rewardWinnerWithCoins(String username);

    void updateGamesPlayedByUser(String username);

    void calculateUserWinRate(String username);

}

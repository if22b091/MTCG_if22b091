package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.BattleRepository;
import at.fhtw.sampleapp.persistence.repository.BattleRepositoryImpl;

import java.util.List;
import java.util.Random;

public class BattleService extends AbstractService {

    // A StringBuilder object to log the events of a battle for later display.
    private StringBuilder battleLog;

    // Constructor initializes the battleLog object.
    public BattleService() {
        battleLog = new StringBuilder();
    }

    // Calculates the damage between two cards, adjusting for their types and elements.
    private void calculateDamage(Card card1, Card card2) {
        // Spell cards inflict damage based on specific logic.
        if (card1.getType() == CardType.SPELL.ordinal()) {
            calculateSpellDamage(card1, card2);
        }
        if (card2.getType() == CardType.SPELL.ordinal()) {
            calculateSpellDamage(card2, card1);
        }
        // When both cards are monsters, their damage is calculated differently.
        if (card1.getType() == CardType.MONSTER.ordinal() && card2.getType() == CardType.MONSTER.ordinal()) {
            calculateMonsterDamage(card1, card2);
        }
    }

    // Calculates damage for spell cards, taking into account their elements and special conditions.
    private void calculateSpellDamage(Card spellCard, Card otherCard) {
        // Special interactions based on card names and elements.
        // For example, a Knight is drowned by a Water spell.
        if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getName().equals("Knight")) {
            spellCard.setDamage(Integer.MAX_VALUE);
            battleLog.append(("Knight drowned\n"));
            return;
        }
        // Krakens are immune to spells.
        if (spellCard.getType() == CardType.SPELL.ordinal() && otherCard.getName().equals("Kraken")) {
            spellCard.setDamage(0);
            battleLog.append(("Kraken is immune to spells\n"));
            return;
        }
        // Elemental advantage and disadvantage logic.
        if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getElement() == CardElement.FIRE.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.FIRE.ordinal() && otherCard.getElement() == CardElement.WATER.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        } else if (spellCard.getElement() == CardElement.FIRE.ordinal() && otherCard.getElement() == CardElement.NORMAL.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.NORMAL.ordinal() && otherCard.getElement() == CardElement.FIRE.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        } else if (spellCard.getElement() == CardElement.NORMAL.ordinal() && otherCard.getElement() == CardElement.WATER.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getElement() == CardElement.NORMAL.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        }
    }

    // Calculates damage for monster cards, taking into account specific interactions between monsters.
    private void calculateMonsterDamage(Card monster1, Card monster2) {
        // Specific monster interactions, e.g., Goblins are too afraid of Dragons to attack.
        if ((monster1.getName().contains("Goblin") && monster2.getName().equals("Dragon")) ||
                (monster1.getName().equals("Dragon") && monster2.getName().contains("Goblin"))) {
            if (monster1.getName().contains("Goblin")) {
                monster1.setDamage(0);
            } else {
                monster2.setDamage(0);
            }
            battleLog.append("Goblins are too afraid of Dragons to attack.\n");
        }
        // FireElves evade Dragon attacks due to their familiarity.
        if ((monster1.getName().equals("FireElf") && monster2.getName().equals("Dragon")) ||
                (monster2.getName().equals("FireElf") && monster1.getName().equals("Dragon"))) {
            if (monster1.getName().equals("FireElf")) {
                monster2.setDamage(0);
            } else {
                monster1.setDamage(0);
            }
            battleLog.append("FireElves know Dragons since they were little and evade their attacks.\n");
        }
        // Wizards automatically nullify damage from Orks.
        if (monster1.getName().equals("Wizard") && monster2.getName().equals("Ork")) {
            monster2.setDamage(0);
        } else if (monster2.getName().equals("Wizard") && monster1.getName().equals("Ork")) {
            monster1.setDamage(0);
        }
    }

    // Initiates and executes a battle sequence between two users, using their decks.
    public String startBattle(String username1, String username2) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            BattleRepository battleRepository = new BattleRepositoryImpl(unitOfWork);

            try {
                // Fetch the battle decks for both users.
                List<Card> deck1 = battleRepository.fetchBattleDeckByUsername(username1);
                List<Card> deck2 = battleRepository.fetchBattleDeckByUsername(username2);

                Random random = new Random();

                int roundCount = 0;
                // Continue the battle until one deck is empty or 100 rounds have passed.
                while (!deck1.isEmpty() && !deck2.isEmpty() && roundCount < 100) {
                    int index1 = random.nextInt(deck1.size());
                    int index2 = random.nextInt(deck2.size());

                    Card card1 = deck1.get(index1);
                    Card card2 = deck2.get(index2);

                    // Log the round and cards involved.
                    System.out.println("Round " + (roundCount + 1) + ": " + card1.getName() + " vs " + card2.getName());
                    battleLog.append("Round ").append(roundCount + 1).append(": ").append(card1.getName()).append(" vs ").append(card2.getName()).append("\n");

                    // Store original damage values for later reset.
                    int originalDamage1 = card1.getDamage();
                    int originalDamage2 = card2.getDamage();

                    // Calculate and compare damage, adjusting deck contents accordingly.
                    calculateDamage(card1, card2);

                    // Log the result of the battle round.
                    System.out.println(card1.getName() + " (Damage: " + card1.getDamage() + ") vs " + card2.getName() + " (Damage: " + card2.getDamage() + ")");
                    battleLog.append(card1.getName()).append(" (Damage: ").append(card1.getDamage()).append(") vs ").append(card2.getName()).append(" (Damage: ").append(card2.getDamage()).append(")\n");

                    // Update decks based on the outcome of the battle.
                    if (card1.getDamage() > card2.getDamage()) {
                        deck1.add(card2);
                        deck2.remove(index2);
                        System.out.println(card1.getName() + " wins");
                        battleLog.append(card1.getName()).append(" wins\n");
                    } else if (card1.getDamage() < card2.getDamage()) {
                        deck2.add(card1);
                        deck1.remove(index1);
                        System.out.println(card2.getName() + " wins");
                        battleLog.append(card2.getName()).append(" wins\n");
                    } else {
                        System.out.println("It's a draw");
                        battleLog.append("It's a draw\n");
                    }

                    // Reset damage values to their original states after each round.
                    card1.setDamage(originalDamage1);
                    card2.setDamage(originalDamage2);

                    roundCount++;
                }

                // Determine and log the overall winner of the battle, updating records as necessary.
                if (deck1.isEmpty()) {
                    System.out.println(username2 + " wins the battle!");
                    battleRepository.increaseElo(username2);
                    battleRepository.decreaseElo(username1);
                    battleRepository.incrementUserVictories(username2);
                    battleRepository.incrementUserDefeats(username1);
                    battleRepository.rewardWinnerWithCoins(username2);
                    battleLog.append(username2).append(" wins the battle!\n");
                } else if (deck2.isEmpty()) {
                    System.out.println(username1 + " wins the battle!");
                    battleRepository.increaseElo(username1);
                    battleRepository.decreaseElo(username2);
                    battleRepository.incrementUserVictories(username1);
                    battleRepository.incrementUserDefeats(username2);
                    battleRepository.rewardWinnerWithCoins(username1);
                    battleLog.append(username1).append(" wins the battle!\n");
                } else {
                    System.out.println("The battle ended in a draw after 100 rounds.");
                    battleLog.append("The battle ended in a draw after 100 rounds.\n");
                }

                // Update user statistics following the battle.
                battleRepository.updateGamesPlayedByUser(username1);
                battleRepository.updateGamesPlayedByUser(username2);

                battleRepository.calculateUserWinRate(username1);
                battleRepository.calculateUserWinRate(username2);

                // Log the final state of each user's deck.
                System.out.println("Final decks:");
                System.out.println(username1 + "'s deck: " + deck1);
                System.out.println(username2 + "'s deck: " + deck2);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Reset the battleLog for future use and return the log of this battle.
            String result = battleLog.toString();
            battleLog = new StringBuilder();
            return result;

        }
    }
}

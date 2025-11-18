package game;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class AIPlayer extends Player {
    private Random random = new Random();

    public AIPlayer(String name) {
        super(name, false); // Player is not human
    }

    public Card ChooseCardToPlay(Card topCard) {
        List<Card> validCards = getValidCards(topCard);
        
        if (validCards.isEmpty()) {
            System.out.println(getName() + " N'A AUCUNE CARTE VALIDE À JOUER");
            return null; // No valid cards to play
        }

        // First check if we're about to have one card left
        if (getHand().size() == 2) {
            System.out.println(getName() + " SE PRÉPARE À CRIER 'UNO'! 🎮");
        }

        // AI Strategy:
        // 1. Play action cards first to disadvantage opponents
        // 2. Play Wild+4 when possible
        // 3. Play matching color cards
        // 4. Play matching value cards
        // 5. Play Wild cards as last resort

        // Try to play Wild+4 first (most powerful card)
        for (Card card : validCards) {
            if (card.getType().equals("Wild+4")) {
                System.out.println(getName() + " A CHOISI DE JOUER UN WILD+4");
                return card;
            }
        }

        // Try to play action cards (+2, Skip, Reverse)
        for (Card card : validCards) {
            if (card.getType().equals("+2") ||
                card.getType().equals("Skip") ||
                card.getType().equals("Reverse")) {
                System.out.println(getName() + " A CHOISI DE JOUER UNE CARTE ACTION");
                return card;
            }
        }

        // Try to play matching color cards
        List<Card> colorMatches = new ArrayList<>();
        for (Card card : validCards) {
            if (card.getColor() == topCard.getColor() && 
                card.getType() != "Wild" && 
                card.getType() != "Wild+4") {
                colorMatches.add(card);
            }
        }

        if (!colorMatches.isEmpty()) {
            Card chosen = colorMatches.get(random.nextInt(colorMatches.size()));
            System.out.println(getName() + " A CHOISI DE JOUER UNE CARTE DE MÊME COULEUR");
            return chosen;
        }

        // Try to play matching value cards
        List<Card> valueMatches = new ArrayList<>();
        for (Card card : validCards) {
            if (card.getValue() == topCard.getValue() && 
                card.getType() != "Wild" && 
                card.getType() != "Wild+4") {
                valueMatches.add(card);
            }
        }

        if (!valueMatches.isEmpty()) {
            Card chosen = valueMatches.get(random.nextInt(valueMatches.size()));
            System.out.println(getName() + " A CHOISI DE JOUER UNE CARTE DE MÊME VALEUR");
            return chosen;
        }

        // If all else fails, play a Wild card
        for (Card card : validCards) {
            if (card.getType().equals("Wild")) {
                System.out.println(getName() + " A CHOISI DE JOUER UN WILD");
                return card;
            }
        }

        // Play any valid card as last resort
        Card chosen = validCards.get(random.nextInt(validCards.size()));
        System.out.println(getName() + " A CHOISI DE JOUER : " + chosen.toString());
        return chosen;
    }
}
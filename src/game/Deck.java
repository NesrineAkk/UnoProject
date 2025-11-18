package game;

import java.util.Random;

public class Deck {
    private Card[] cards;
    private int cardsInDeck;

    private Card[] discardPile;
    private int cardsInDiscard;

    // Variable to store the chosen color for Wild cards
    private Card.Color nextTopCardColor = null;

    public Deck() {
        cards = new Card[108];
        discardPile = new Card[108];
        cardsInDeck = 0;
        cardsInDiscard = 0;
    }

    public boolean isEmpty() {
        return cardsInDeck == 0;
    }

    public void initializeDeck() {
        cardsInDeck = 0;
        for (Card.Color color : Card.Color.values()) {
            if (color == Card.Color.Black) continue;

            for (int i = 0; i <= 9; i++) {
                cards[cardsInDeck++] = new Card(color, Card.Value.getValue(i));
                if (i != 0) {
                    cards[cardsInDeck++] = new Card(color, Card.Value.getValue(i));
                }
            }

            for (Card.Value value : new Card.Value[]{Card.Value.DrawTwo, Card.Value.Skip, Card.Value.Reverse}) {
                cards[cardsInDeck++] = new Card(color, value);
                cards[cardsInDeck++] = new Card(color, value);
            }
        }

        for (Card.Value value : new Card.Value[]{Card.Value.Wild, Card.Value.WildFour}) {
            for (int i = 0; i < 4; i++) {
                cards[cardsInDeck++] = new Card(Card.Color.Black, value);
            }
        }
    }

    public void shuffleDeck() {
        Random random = new Random();
        for (int i = cardsInDeck - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
    }

    public Card drawCard() {
        if (cardsInDeck == 0) {
            if (cardsInDiscard <= 1) {
                throw new IllegalStateException("No cards left to draw.");
            }

            Card topCard = discardPile[--cardsInDiscard];
            for (int i = 0; i < cardsInDiscard; i++) {
                // Reset any Wild cards back to Black before returning to deck
                if ((discardPile[i].getValue() == Card.Value.Wild || 
                     discardPile[i].getValue() == Card.Value.WildFour)) {
                    // Reset Wild cards to black before reshuffling into deck
                    discardPile[i].setColor(Card.Color.Black);
                }
                cards[i] = discardPile[i];
            }
            cardsInDeck = cardsInDiscard;
            cardsInDiscard = 1;
            discardPile[0] = topCard;

            shuffleDeck();
        }
        return cards[--cardsInDeck];
    }

    // Modified to apply the next top card color if set
    public void addToDiscardPile(Card card) {
        discardPile[cardsInDiscard++] = card;

        // Apply the saved color change if one exists and the top card is a Wild
        if (nextTopCardColor != null &&
            (getTopCard().getValue() == Card.Value.Wild ||
             getTopCard().getValue() == Card.Value.WildFour)) {
            getTopCard().setColor(nextTopCardColor);
            System.out.println("COULEUR DE LA CARTE DU DESSUS CHANGÉE À: " + nextTopCardColor);
            nextTopCardColor = null;
        }
    }

    public Card getTopCard() {
        if (cardsInDiscard == 0) {
            throw new IllegalStateException("Discard pile is empty.");
        }
        return discardPile[cardsInDiscard - 1];
    }

    // Method to set the next top card's color (for wilds)
    public void setNextTopCardColor(Card.Color color) {
        this.nextTopCardColor = color;
    }

    public void reshuffleDeck() {
        Card topCard = getTopCard();
        
        // Move cards from discard to deck, resetting Wild cards to black
        for (int i = 0; i < cardsInDiscard - 1; i++) {
            // Create a copy of the card
            Card card = discardPile[i];
            
            // Reset any Wild cards back to Black before returning to deck
            if (card.getValue() == Card.Value.Wild || card.getValue() == Card.Value.WildFour) {
                // Only create a new Black wild card for reshuffling
                cards[cardsInDeck++] = new Card(Card.Color.Black, card.getValue());
            } else {
                cards[cardsInDeck++] = card;
            }
        }
        cardsInDiscard = 1;
        discardPile[0] = topCard;

        shuffleDeck();
    }

	public int getDrawPileSize() {
		// TODO Auto-generated method stub
		return cards.length;
	}

	public int getDiscardPileSize() {
		// TODO Auto-generated method stub
		return discardPile.length;
	}
}
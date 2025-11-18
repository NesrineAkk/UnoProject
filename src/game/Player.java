package game;


import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private boolean isHuman;

    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        if(card != null) {
            hand.add(card);
        }
    }

    public void playCard(Card card) {
        if(card != null) {
            hand.remove(card);
        }
    }

    // Fixed getValidCards method to properly implement UNO rules
    public List<Card> getValidCards(Card topCard) {
        List<Card> validCards = new ArrayList<>();
        
        for (Card card : hand) {
            // Valid plays: Same color, same value/type, or a wild card
            if (card.getColor() == topCard.getColor() || 
                (card.getValue() == topCard.getValue() && card.getValue() != Card.Value.Wild && card.getValue() != Card.Value.WildFour) ||
                card.getColor() == Card.Color.Black) {
                
                // Special case for Wild+4: According to official rules, can only be played if player has no matching color
                if (card.getType().equals("Wild+4")) {
                    boolean hasMatchingColor = false;
                    for (Card handCard : hand) {
                        if (handCard.getColor() == topCard.getColor() && 
                            handCard.getType() != "Wild" && 
                            handCard.getType() != "Wild+4") {
                            hasMatchingColor = true;
                            break;
                        }
                    }
                    
                    // If player has a card of matching color, Wild+4 can't be played
                    // However, the computer won't enforce this for humans (they can bluff)
                    if (!hasMatchingColor || isHuman) {
                        validCards.add(card);
                    }
                } else {
                    validCards.add(card);
                }
            }
        }
        return validCards;
    }

    public String getName() {
        return name;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public List<Card> getHand() {
        return hand;
    }

	public void setName(String playerName) {
		this.name=playerName;
	}

	public void removeCard(Card card) {
	    hand.remove(card);
	}

}
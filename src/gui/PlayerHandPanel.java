package gui;

import javax.swing.*;

import game.Card;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerHandPanel extends JPanel {
    private List<UICard> cards = new ArrayList<>();
    private boolean showFront;
    private boolean isVertical;
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140; 
    private GameScreen gameScreen;
    
    public static final String CARD_BACK_IMAGE = "back";
    
    public PlayerHandPanel(int cardCount, boolean showFront, boolean isVertical, GameScreen gameScreen) {
        this.showFront = showFront;
        this.isVertical = isVertical;
        this.gameScreen = gameScreen;

        if (isVertical) {
            setLayout(new GridLayout(cardCount, 1, 0, -10));
            setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        } else {
            setLayout(new FlowLayout(FlowLayout.CENTER, -10, 0));
            setBorder(BorderFactory.createEmptyBorder(20, 25, 45, 25));
        }
        
        setOpaque(false);
        generateRandomHand(cardCount);
        updateVisibility(showFront);
    }

    public PlayerHandPanel(GameScreen gameScreen) {
        this(7, true, false, gameScreen);
    }

    private void generateRandomHand(int cardCount) {
        Random random = new Random();
        String[] colors = {"r", "g", "b", "y"};
        String[] specials = {"+", "@", "$"};
        List<String> possibleCards = new ArrayList<>();

        for (String color : colors) {
            for (int i = 0; i <= 9; i++) {
                possibleCards.add(color + i);
            }
            for (String special : specials) {
                possibleCards.add(color + special);
            }
        }

        possibleCards.add("Kw");
        possibleCards.add("Kw+");
        
        for (int i = 0; i < cardCount; i++) {
            String card = possibleCards.get(random.nextInt(possibleCards.size()));
            
            // Add extremely rare chance for any player to get a wild card
            if (random.nextDouble() < 0.0000000000001) {
                card = random.nextBoolean() ? "Kw" : "Kw+";
            }
            
            addCard(card);
        }
    }

    public void addCard(String cardType) {
        UICard card;
        if (gameScreen != null) {
            card = new UICard(cardType, gameScreen);
        } else {
            card = new UICard(cardType, this.showFront);
        }
        
        if (isVertical) {
            card.setRotationAngle(90);
        }
        cards.add(card);
        add(card);

        revalidate();
        repaint();
    }

    public void removeCard(UICard card) {
        if (cards.contains(card)) {
            cards.remove(card);
            remove(card);
            revalidate();
            repaint();
        }
    }

    public void removeCardByType(String cardType) {
        UICard cardToRemove = null;
        for (UICard card : cards) {
            if (card.getCardType().equals(cardType)) {
                cardToRemove = card;
                break;
            }
        }
        if (cardToRemove != null) {
            removeCard(cardToRemove);
        }
    }

    public List<UICard> getCards() {
        return cards;
    }

    public void updateVisibility(boolean show) {
        this.showFront = show;
        removeAll();
        for (UICard card : cards) {
            card.setShowFront(show);
            add(card);
        }
        revalidate();
        repaint();
    }
    
    public void clear() {
        this.removeAll();
        this.cards.clear();
        this.revalidate();
        this.repaint();
    }
    
    public void updateCards(List<Card> cards) {
        this.clear();
        this.cards = new ArrayList<>();
        
        for (Card card : cards) {
            UICard uiCard;
            
            if (showFront) {
                if (gameScreen != null) {
                    uiCard = new UICard(card.getImageName(), gameScreen);
                } else {
                    uiCard = new UICard(card.getImageName(), true);
                }
            } else {
                if (gameScreen != null) {
                    uiCard = new UICard("", gameScreen);
                    uiCard.setShowFront(false);
                } else {
                    uiCard = new UICard("", false);
                }
            }
            
            if (isVertical) {
                uiCard.setRotationAngle(90);
            }
            
            this.cards.add(uiCard);
            this.add(uiCard);
        }
        
        this.revalidate();
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isVertical) {
            return new Dimension(CARD_WIDTH, cards.size() * CARD_HEIGHT);
        } else {
            return new Dimension(cards.size() * CARD_WIDTH, CARD_HEIGHT);
        }
    }
}
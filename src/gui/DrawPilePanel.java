package gui;
import gui.UILabel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DrawPilePanel extends JPanel {
    private List<String> cardDeck;
    private UICard topCard;
    private UILabel titleLabel;
    private UILabel countLabel;
    private int cardCount;
    
    private CardDrawnListener cardDrawnListener;
    
    public DrawPilePanel() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
     
        titleLabel = new UILabel("PIOCHE");
        titleLabel.setOutlineThickness(1);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        countLabel = new UILabel(cardCount + " cartes");
        countLabel.setForeground(Color.WHITE);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        initializeDeck();
        
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.max(getWidth(), getHeight()) / 2;
                
                RadialGradientPaint paint = new RadialGradientPaint(
                    centerX, centerY, radius,
                    new float[] { 0.0f, 0.7f, 1.0f },
                    new Color[] {
                        new Color(255, 255, 255, 70),
                        new Color(255, 255, 255, 20),
                        new Color(0, 0, 0, 0)
                    }
                );
                
                g2d.setPaint(paint);
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(180, 180));
        
        topCard = new UICard("", false);
        
        topCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                drawCard();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                topCard.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                topCard.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                topCard.setBorder(null);
                topCard.repaint();
            }
        });
        
        cardPanel.add(topCard);
        
        add(titleLabel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(countLabel, BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(200, 240));
    }
    
    private void initializeDeck() {
        cardDeck = new ArrayList<>();
        String[] colors = {"r", "g", "b", "y"};
        String[] specials = {"+", "@", "$"};
        
        for (String color : colors) {
            cardDeck.add(color + "0");
            
            for (int i = 1; i <= 9; i++) {
                cardDeck.add(color + i);
                cardDeck.add(color + i);
            }
            
            for (String special : specials) {
                cardDeck.add(color + special);
                cardDeck.add(color + special);
            }
        }
        
        for (int i = 0; i < 4; i++) {
            cardDeck.add("K0");
            cardDeck.add("K+");
        }
        
        // Add extremely rare wild cards that all players can potentially draw
        Random random = new Random();
        if (random.nextDouble() < 0.0000000000001) {
            cardDeck.add("Kw");  // Add one extra wild card with extremely low probability
        }
        if (random.nextDouble() < 0.0000000000001) {
            cardDeck.add("Kw+"); // Add one extra wild+4 card with extremely low probability
        }
        
        shuffleDeck();
        
        cardCount = cardDeck.size();
    }
    
    public void shuffleDeck() {
        Collections.shuffle(cardDeck, new Random());
        updateCardCount();
    }
    
    public String drawCard() {
        if (cardDeck.isEmpty()) {
            System.out.println("La pioche est vide !");
            return null;
        }
        
        String drawnCard = cardDeck.remove(0);
        updateCardCount();
        
        System.out.println("Carte piochée : " + drawnCard);
        
        // Add extremely rare chance to convert any card to a wild card when drawn
        Random random = new Random();
        if (random.nextDouble() < 0.0000000000001) {
            drawnCard = random.nextBoolean() ? "Kw" : "Kw+";
            System.out.println("Ultra-rare wild card transformation activated!");
        }
        
        if (cardDrawnListener != null) {
            cardDrawnListener.onCardDrawn(drawnCard);
        }
        
        return drawnCard;
    }
    
    private void updateCardCount() {
        cardCount = cardDeck.size();
        
        if (countLabel != null) {
            countLabel.setText(cardCount + " cartes");
        }

        if (topCard != null) {
            if (cardCount == 0) {
                topCard.setClickable(false);
                topCard.setVisible(false);
            } else {
                topCard.setClickable(true);
                topCard.setVisible(true);
            }
        }
    }
    
    public void addCards(List<String> cards) {
        cardDeck.addAll(cards);
        shuffleDeck();
    }
    
    public int getCardCount() {
        return cardCount;
    }
    
    public interface CardDrawnListener {
        void onCardDrawn(String cardType);
    }
    
    public void setCardDrawnListener(CardDrawnListener listener) {
        this.cardDrawnListener = listener;
    }
}
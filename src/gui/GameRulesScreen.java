package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameRulesScreen extends UIContainer {
    private final String resourcePath;
    private ActionListener backButtonAction;

    public GameRulesScreen(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
        initializeComponents();
    }

    public void setOnBackAction(ActionListener action) {
        this.backButtonAction = action;
        // Update button if it exists
        for (Component comp : getComponents()) {
            if (comp instanceof UIContainer) {
                updateButtonAction((UIContainer) comp);
            }
        }
    }

    private void updateButtonAction(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof MyButton && "Back to Menu".equals(((MyButton) comp).getText())) {
                MyButton button = (MyButton) comp;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                button.addActionListener(backButtonAction);
            } else if (comp instanceof Container) {
                updateButtonAction((Container) comp);
            }
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 30, 80));
        
        UIContainer mainPanel = new UIContainer() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 20, 20);
                
                g2d.setColor(new Color(15, 30, 80));
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setPadding(0);
        
        UIContainer headerPanel = new UIContainer();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPadding(20);
        
        try {
            BufferedImage logoImage = ImageIO.read(new File(resourcePath + "uno4.png"));
            UIImage logoComponent = new UIImage(logoImage);
            logoComponent.defTailleImage(80, 80);
            
            UIContainer logoContainer = new UIContainer();
            logoContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
            logoContainer.setOpaque(false);
            logoContainer.add(logoComponent);
            
            headerPanel.add(logoContainer, BorderLayout.WEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        UILabel titleLabel = new UILabel("UNO Game Rules");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setOutlineThickness(0);
        
        UIContainer titleContainer = new UIContainer();
        titleContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        
        headerPanel.add(titleContainer, BorderLayout.CENTER);
        
        MyButton backButton = MyButton.createUnoButton("Back to Menu");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setNormalColor(new Color(0, 120, 215));
        backButton.setForeground(Color.WHITE);
        
        if (backButtonAction != null) {
            backButton.addActionListener(backButtonAction);
        }
        
        UIContainer backButtonContainer = new UIContainer();
        backButtonContainer.setLayout(new FlowLayout(FlowLayout.RIGHT));
        backButtonContainer.setOpaque(false);
        backButtonContainer.add(backButton);
        
        headerPanel.add(backButtonContainer, BorderLayout.EAST);
        
        UIContainer contentPanel = new UIContainer();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setPadding(20);
        
        JTextPane rulesTextPane = new JTextPane();
        rulesTextPane.setEditable(false);
        rulesTextPane.setBackground(new Color(30, 45, 100));
        rulesTextPane.setForeground(Color.WHITE);
        rulesTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        rulesTextPane.setText(getGameRulesText());
        
        JScrollPane scrollPane = new JScrollPane(rulesTextPane);
        scrollPane.setBorder(UIBorderFactory.createLineBorder(new Color(0, 120, 215), 2, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private String getGameRulesText() {
        return "UNO OFFICIAL RULES\n\n" +
               "OBJECTIVE:\n" +
               "Be the first player to score 500 points. Points are scored by getting rid of all the cards in your hand before your opponent.\n\n" +
               "SETUP:\n" +
               "1. Each player draws a card. The person with the highest card value goes first.\n" +
               "2. Each player is dealt 7 cards.\n" +
               "3. The remaining cards are placed face down to form a draw pile.\n" +
               "4. The top card of the draw pile is turned over to begin the discard pile.\n\n" +
               "GAMEPLAY:\n" +
               "1. Players must match the top card on the discard pile either by number, color, or symbol.\n" +
               "2. If a player can't match, they must draw a card from the draw pile. If the drawn card can be played, they may play it immediately.\n" +
               "3. A player may choose not to play a matching card. If so, they must draw a card from the draw pile.\n\n" +
               "CARD TYPES AND EFFECTS:\n" +
               "- Number Cards (0-9): Played by matching the number or color of the previous card.\n" +
               "- Skip Card: The next player loses their turn.\n" +
               "- Reverse Card: Reverses the direction of play.\n" +
               "- Draw Two Card: The next player must draw 2 cards and forfeit their turn.\n" +
               "- Wild Card: Allows the player to change the current color being played.\n" +
               "- Wild Draw Four Card: Same as Wild Card, but the next player must also draw 4 cards and forfeit their turn.\n\n" +
               "DECLARING UNO:\n" +
               "When you have just one card left, you must yell \"UNO!\" If you don't and another player catches you, you must draw 2 cards.\n\n" +
               "SCORING:\n" +
               "When a player gets rid of all their cards, they receive points for cards left in opponents' hands:\n" +
               "- Number cards: Face value\n" +
               "- Skip, Reverse, Draw Two: 20 points each\n" +
               "- Wild, Wild Draw Four: 50 points each\n\n" +
               "The first player to reach 500 points wins the game.";
    }
}
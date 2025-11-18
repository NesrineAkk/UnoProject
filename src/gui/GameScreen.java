package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import game.Card;
import game.Game;
import game.Player;
import game.AIPlayer;

public class GameScreen extends JPanel {
    private List<PlayerHandPanel> playerHands = new ArrayList<>();
    private DiscardPilePanel discardPile;
    private DrawPilePanel drawPile;
    
    private Game game;
    private List<Player> players = new ArrayList<>();
    private Timer aiTimer;
    private boolean aiTurnInProgress = false;

    public GameScreen(int numberOfPlayers) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        setBackground(UICore.BACKGROUND_COLOR);

        initializePlayers(numberOfPlayers);
        
        game = new Game(players);
        game.initializeGame();

        initializeUIComponents();
        
        addPlayersToUI(numberOfPlayers);
        
        updateGameUI();
        
        setupDrawPileListener();
        
        setupAITimer();
    }

    public void cardPlayed(String cardCode) {
        if (game.getCurrentPlayerIndex() != 0) {
            return;
        }
        
        System.out.println("Card played: " + cardCode);
        
        if (game.getStackedDrawCards() > 0) {
            boolean canCounter = false;
            
            if (game.getCurrentStackType().equals("+2") && cardCode.contains("+") && !cardCode.contains("w+")) {
                canCounter = true;
            }
            else if (game.getCurrentStackType().equals("Wild+4") && 
                    (cardCode.contains("w+") || cardCode.endsWith("+4") || cardCode.endsWith("+") || 
                     cardCode.contains("wild+") || cardCode.contains("wild4"))) {
                canCounter = true;
            }
            
            if (!canCounter) {
                Player currentPlayer = players.get(game.getCurrentPlayerIndex());
                
                JOptionPane.showMessageDialog(this, 
                    "You must draw " + game.getStackedDrawCards() + " cards due to a stacked " + 
                    game.getCurrentStackType() + " effect!");
                
                for (int i = 0; i < game.getStackedDrawCards(); i++) {
                    game.drawCard(currentPlayer);
                }
                
                game.resetStackedDrawCards();
                updateGameUI();
                nextTurn();
                return;
            }
        }
        
        if (isWildCard(cardCode)) {
            String chosenColor = promptForWildCardColor();
            if (chosenColor != null) {
                String formattedCardCode;
                
                boolean isWildDrawFour = cardCode.contains("+") || cardCode.contains("4") || 
                                         cardCode.contains("draw") || cardCode.endsWith("+");
                                         
                formattedCardCode = chosenColor + (isWildDrawFour ? "w+" : "w");
                System.out.println("Formatted wild card: " + formattedCardCode);
                playCardAndHandleResult(formattedCardCode);
            }
        } else {
            playCardAndHandleResult(cardCode);
        }
    }

    private boolean isWildCard(String cardCode) {
        if (cardCode == null || cardCode.isEmpty()) {
            return false;
        }
        
        String lowerCardCode = cardCode.toLowerCase();
        
        if (lowerCardCode.equals("w") || lowerCardCode.equals("w+") || 
            lowerCardCode.contains("wild")) {
            return true;
        }
        
        if (lowerCardCode.length() >= 2) {
            char firstChar = lowerCardCode.charAt(0);
            String restOfCard = lowerCardCode.substring(1);
            
            if ("rgby".indexOf(firstChar) != -1 && 
                (restOfCard.equals("w") || restOfCard.equals("w+") || 
                 restOfCard.contains("wild"))) {
                return true;
            }
        }
        
        return false;
    }

    private void playCardAndHandleResult(String cardCode) {
        System.out.println("Attempting to play card: " + cardCode);
        boolean isValidPlay = game.playCard(cardCode);
        
        if (isValidPlay) {
            System.out.println("Card played successfully: " + cardCode);
            updateGameUI();
            
            Player currentPlayer = players.get(game.getCurrentPlayerIndex());
            if (currentPlayer.getHand().isEmpty()) {
                aiTimer.cancel();
                return;
            }
        } else {
            System.out.println("Invalid card: " + cardCode);
            JOptionPane.showMessageDialog(this, "Invalid card!");
        }
    }

    private void initializePlayers(int numberOfPlayers) {
        players.add(new Player("Player 1", true));
        
        for (int i = 1; i < numberOfPlayers; i++) {
            players.add(new AIPlayer("Player " + (i + 1)));
        }
    }

    private void initializeUIComponents() {
        discardPile = new DiscardPilePanel(this);
        drawPile = new DrawPilePanel();
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel middleGameZone = new JPanel();
        middleGameZone.setLayout(new BoxLayout(middleGameZone, BoxLayout.X_AXIS));
        middleGameZone.setOpaque(false);
        
        middleGameZone.add(Box.createHorizontalGlue());
        middleGameZone.add(drawPile);
        middleGameZone.add(Box.createRigidArea(new Dimension(30, 0)));
        middleGameZone.add(discardPile);
        middleGameZone.add(Box.createHorizontalGlue());
        
        centerPanel.add(middleGameZone);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addPlayersToUI(int numberOfPlayers) {
        switch (numberOfPlayers) {
            case 2:
                addPlayerToBorder(BorderLayout.SOUTH, true, false);
                addPlayerToBorder(BorderLayout.NORTH, false, false);
                break;
            case 3:
                addPlayerToBorder(BorderLayout.SOUTH, true, false);
                addPlayerToBorder(BorderLayout.WEST, false, true);
                addPlayerToBorder(BorderLayout.NORTH, false, false);
                break;
            case 4:
                addPlayerToBorder(BorderLayout.SOUTH, true, false);
                addPlayerToBorder(BorderLayout.WEST, false, true);
                addPlayerToBorder(BorderLayout.NORTH, false, false);
                addPlayerToBorder(BorderLayout.EAST, false, true);
                break;
            default:
                addPlayerToBorder(BorderLayout.SOUTH, true, false);
                JOptionPane.showMessageDialog(this, "Number of players not supported (1 to 4)");
                break;
        }
    }

    private void addPlayerToBorder(String borderPosition, boolean showFront, boolean vertical) {
        PlayerHandPanel hand = new PlayerHandPanel(7, showFront, vertical, this);
        playerHands.add(hand);
        add(hand, borderPosition);
    }

    private void setupDrawPileListener() {
        drawPile.setCardDrawnListener(new DrawPilePanel.CardDrawnListener() {
            @Override
            public void onCardDrawn(String cardType) {
                if (game.getCurrentPlayerIndex() == 0) {
                    Player currentPlayer = players.get(game.getCurrentPlayerIndex());
                    
                    if (game.getStackedDrawCards() > 0) {
                        JOptionPane.showMessageDialog(GameScreen.this, 
                            "You must draw " + game.getStackedDrawCards() + " cards due to a stacked " + 
                            game.getCurrentStackType() + " effect!");
                        
                        for (int i = 0; i < game.getStackedDrawCards(); i++) {
                            game.drawCard(currentPlayer);
                        }
                        
                        game.resetStackedDrawCards();
                        
                        nextTurn();
                    } else {
                        game.drawCard(currentPlayer);
                        updateGameUI();
                        
                        if (!hasValidMoves(currentPlayer)) {
                            nextTurn();
                        }
                    }
                }
            }
        });
    }
    
    private boolean hasValidMoves(Player player) {
        Card topCard = game.getDeck().getTopCard();
        List<Card> validCards = player.getValidCards(topCard);
        return !validCards.isEmpty();
    }
    
    public void updateGameUI() {
        updateHandsFromGame();
        updateDiscardPile();
    }
    
    private void updateHandsFromGame() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerHandPanel handPanel = playerHands.get(i);
            handPanel.updateCards(player.getHand());
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Game getGame() {
        return game;
    }

    private void setupAITimer() {
        aiTimer = new Timer();
        aiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleAITurn();
            }
        }, 1000, 1000);
    }

    private void handleAITurn() {
        if (aiTurnInProgress) {
            return;
        }
        
        int currentPlayerIndex = game.getCurrentPlayerIndex();
        if (currentPlayerIndex > 0 && currentPlayerIndex < players.size()) {
            aiTurnInProgress = true;
            
            Player aiPlayer = players.get(currentPlayerIndex);
            boolean played = playAITurn(aiPlayer);
            
            if (played) {
                updateGameUI();
                
                if (aiPlayer.getHand().isEmpty()) {
                    aiTimer.cancel();
                    return;
                }
            }
            
            aiTurnInProgress = false;
        }
    }

    private void nextTurn() {
        game.nextTurn();
        updateGameUI();
    }
   
    private boolean playAITurn(Player aiPlayer) {
        if (game.getStackedDrawCards() > 0) {
            Card topCard = game.getDeck().getTopCard();
            boolean canCounter = false;
            
            if (aiPlayer instanceof AIPlayer) {
                AIPlayer ai = (AIPlayer) aiPlayer;
                Card cardToPlay = null;
                
                if (game.getCurrentStackType().equals("+2")) {
                    for (Card card : ai.getHand()) {
                        if (card.getType().equals("+2")) {
                            cardToPlay = card;
                            break;
                        }
                    }
                }
                else if (game.getCurrentStackType().equals("Wild+4")) {
                    for (Card card : ai.getHand()) {
                        if (card.getType().equals("Wild+4")) {
                            cardToPlay = card;
                            break;
                        }
                    }
                }
                
                if (cardToPlay != null) {
                    if (cardToPlay.getValue() == Card.Value.Wild || cardToPlay.getValue() == Card.Value.WildFour) {
                        String[] colors = {"r", "g", "b", "y"};
                        String chosenColor = colors[(int) (Math.random() * colors.length)];
                        String cardCode = chosenColor + cardToPlay.getImageName().substring(1);
                        boolean success = game.playCard(cardCode);
                        return success;
                    } else {
                        boolean success = game.playCard(cardToPlay.getImageName());
                        return success;
                    }
                }
            }
            
            System.out.println(aiPlayer.getName() + " must draw " + game.getStackedDrawCards() + " cards!");
            for (int i = 0; i < game.getStackedDrawCards(); i++) {
                game.drawCard(aiPlayer);
            }
            game.resetStackedDrawCards();
            nextTurn();
            return true;
        }
        
        if (aiPlayer instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) aiPlayer;
            Card topCard = game.getDeck().getTopCard();
            Card cardToPlay = ai.ChooseCardToPlay(topCard);
            
            if (cardToPlay != null) {
                if (cardToPlay.getValue() == Card.Value.Wild || cardToPlay.getValue() == Card.Value.WildFour) {
                    String[] colors = {"r", "g", "b", "y"};
                    String chosenColor = colors[(int) (Math.random() * colors.length)];
                    
                    String cardCode = chosenColor + cardToPlay.getImageName().substring(1);
                    boolean success = game.playCard(cardCode);
                    
                    if (!success) {
                        game.drawCard(aiPlayer);
                        nextTurn();
                    }
                    
                    return success;
                } else {
                    boolean success = game.playCard(cardToPlay.getImageName());
                    
                    if (!success) {
                        game.drawCard(aiPlayer);
                        nextTurn();
                    }
                    
                    return success;
                }
            } else {
                game.drawCard(aiPlayer);
                
                Card lastDrawnCard = null;
                
                if (!aiPlayer.getHand().isEmpty()) {
                    lastDrawnCard = aiPlayer.getHand().get(aiPlayer.getHand().size() - 1);
                }
                
                if (lastDrawnCard != null) {
                    List<Card> validCards = aiPlayer.getValidCards(topCard);
                    boolean canPlayDrawnCard = false;
                    
                    for (Card validCard : validCards) {
                        if (validCard.equals(lastDrawnCard)) {
                            canPlayDrawnCard = true;
                            
                            if (lastDrawnCard.getValue() == Card.Value.Wild || 
                                lastDrawnCard.getValue() == Card.Value.WildFour) {
                                String[] colors = {"r", "g", "b", "y"};
                                String chosenColor = colors[(int) (Math.random() * colors.length)];
                                String cardCode = chosenColor + lastDrawnCard.getImageName().substring(1);
                                boolean success = game.playCard(cardCode);
                                
                                if (!success) {
                                    nextTurn();
                                }
                                
                                return success;
                            } else {
                                boolean success = game.playCard(lastDrawnCard.getImageName());
                                
                                if (!success) {
                                    nextTurn();
                                }
                                
                                return success;
                            }
                        }
                    }
                }
                
                nextTurn();
                return true;
            }
        } else {
            Card topCard = game.getDeck().getTopCard();
            List<Card> validCards = aiPlayer.getValidCards(topCard);
            
            if (!validCards.isEmpty()) {
                Card cardToPlay = validCards.get((int)(Math.random() * validCards.size()));
                
                if (cardToPlay.getValue() == Card.Value.Wild || cardToPlay.getValue() == Card.Value.WildFour) {
                    String[] colors = {"r", "g", "b", "y"};
                    String chosenColor = colors[(int) (Math.random() * colors.length)];
                    String cardCode = chosenColor + cardToPlay.getImageName().substring(1);
                    boolean success = game.playCard(cardCode);
                    
                    if (!success) {
                        game.drawCard(aiPlayer);
                        nextTurn();
                    }
                    
                    return success;
                } else {
                    boolean success = game.playCard(cardToPlay.getImageName());
                    
                    if (!success) {
                        game.drawCard(aiPlayer);
                        nextTurn();
                    }
                    
                    return success;
                }
            } else {
                game.drawCard(aiPlayer);
                
                Card lastDrawnCard = null;
                if (!aiPlayer.getHand().isEmpty()) {
                    lastDrawnCard = aiPlayer.getHand().get(aiPlayer.getHand().size() - 1);
                }
                
                if (lastDrawnCard != null) {
                    validCards = aiPlayer.getValidCards(topCard);
                    for (Card validCard : validCards) {
                        if (validCard.equals(lastDrawnCard)) {
                            if (lastDrawnCard.getValue() == Card.Value.Wild || 
                                lastDrawnCard.getValue() == Card.Value.WildFour) {
                                String[] colors = {"r", "g", "b", "y"};
                                String chosenColor = colors[(int) (Math.random() * colors.length)];
                                String cardCode = chosenColor + lastDrawnCard.getImageName().substring(1);
                                boolean success = game.playCard(cardCode);
                                
                                if (!success) {
                                    nextTurn();
                                }
                                
                                return success;
                            } else {
                                boolean success = game.playCard(lastDrawnCard.getImageName());
                                
                                if (!success) {
                                    nextTurn();
                                }
                                
                                return success;
                            }
                        }
                    }
                }
                
                nextTurn();
                return true;
            }
        }
    }

    private String promptForWildCardColor() {
        String[] options = {"Red", "Green", "Blue", "Yellow"};
        Color[] colors = {
            new Color(255, 60, 60),
            new Color(0, 200, 80),
            new Color(0, 120, 255),
            new Color(255, 215, 0)
        };
        
        JButton[] buttons = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            buttons[i] = new JButton(options[i]);
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(i == 3 ? Color.BLACK : Color.WHITE);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 14));
            buttons[i].setBorder(BorderFactory.createRaisedBevelBorder());
            buttons[i].setPreferredSize(new Dimension(100, 40));
        }
        
        JOptionPane optionPane = new JOptionPane(
            "Choose a color for the Wild card:",
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            buttons,
            buttons[0]
        );
        
        JDialog dialog = optionPane.createDialog("Color Selection");
        dialog.setAlwaysOnTop(true);
        
        final String[] result = {null};
        for (int i = 0; i < buttons.length; i++) {
            final String colorCode = "rgby".substring(i, i+1);
            buttons[i].addActionListener(e -> {
                result[0] = colorCode;
                dialog.dispose();
            });
        }
        
        dialog.setVisible(true);
        return result[0];
    }
    
    private boolean isWildDrawFour(String cardCode) {
        if (cardCode == null || cardCode.isEmpty()) {
            return false;
        }
        
        String lowerCardCode = cardCode.toLowerCase();
        
        if (lowerCardCode.equals("w+")) {
            return true;
        }
        
        if (lowerCardCode.length() >= 3 && "rgby".indexOf(lowerCardCode.charAt(0)) != -1 && 
            lowerCardCode.substring(1).equals("w+")) {
            return true;
        }
        
        return lowerCardCode.contains("wild+4") || lowerCardCode.contains("wild4") || 
               lowerCardCode.contains("wild_draw4") || lowerCardCode.contains("wild_plus4") ||
               lowerCardCode.contains("draw4");
    }

    private void updateDiscardPile() {
        Card topCard = game.getDeck().getTopCard();
        String cardImageName = topCard.getImageName();
        
        System.out.println("Updating discard pile with top card: " + cardImageName);
        
        if (cardImageName != null) {
            discardPile.updateCard(cardImageName);
        } else {
            System.err.println("Warning: Top card has null image name");
        }
    }
}
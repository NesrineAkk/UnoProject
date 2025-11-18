package gui;

import game.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class UnoGameApp extends MYFrame {
    private UIContainer mainPanel;
    private CardLayout cardLayout;

    private WelcomeScreen welcomeScreen;
    private NameEnteryScreen nameEntryScreen;
    private MainMenuScreen mainMenuScreen;
    private GameRulesScreen gameRulesScreen;
    private PlayerSelectionScreen playerSelectionScreen;
    private UIContainer gameScreenContainer;
    private GameScreen gameScreenPanel;

    private Game gameLogic;
    private List<Player> players;
    private Timer gameUpdateTimer;

    private final String resourcePath = "recources/";
    private String playerName = "Player";
    private int avatarIndex = 1;
    private int selectedPlayerCount = 2;
    private int[] gameAvatars;
    private boolean gameInProgress = false;

    public UnoGameApp() {
        setTitle("UNO The Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new UIContainer();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(new Color(15, 30, 80));

        initScreens();
        cardLayout.show(mainPanel, "welcome");
        add(mainPanel);
    }

    private void initScreens() {
        createScreens();
        setupEventHandlers();

        mainPanel.add(welcomeScreen, "welcome");
        mainPanel.add(nameEntryScreen, "nameEntry");
        mainPanel.add(mainMenuScreen, "mainMenu");
        mainPanel.add(gameRulesScreen, "gameRules");
        mainPanel.add(playerSelectionScreen, "playerSelection");
    }

    private void createScreens() {
        welcomeScreen = new WelcomeScreen(resourcePath);
        nameEntryScreen = new NameEnteryScreen(resourcePath);
        mainMenuScreen = new MainMenuScreen(resourcePath);
        gameRulesScreen = new GameRulesScreen(resourcePath);
        playerSelectionScreen = new PlayerSelectionScreen(resourcePath);
    }

    private void setupEventHandlers() {
        welcomeScreen.setOnContinueAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "nameEntry");
            }
        });

        nameEntryScreen.setOnSaveAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameEntryScreen.getPlayerName();
                avatarIndex = nameEntryScreen.getSelectedAvatarIndex();
                cardLayout.show(mainPanel, "mainMenu");
            }
        });

        mainMenuScreen.setOnCreateGameAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "playerSelection");
            }
        });

        mainMenuScreen.setOnProfileSettingAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "nameEntry");
            }
        });

        mainMenuScreen.setOnGameRulesAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "gameRules");
            }
        });

        gameRulesScreen.setOnBackAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "mainMenu");
            }
        });
        
        playerSelectionScreen.setPlayerSelectionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPlayerCount = playerSelectionScreen.getSelectedPlayerCount(e);
                initializeGameScreen();
                cardLayout.show(mainPanel, "gameScreen");
                startGame();
            }
        });
        
        playerSelectionScreen.setBackListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "mainMenu");
            }
        });
    }

    private void initializeGameScreen() {
        if (gameScreenContainer != null) {
            mainPanel.remove(gameScreenContainer);
        }
        
        gameScreenContainer = createGameScreenContainer();
        mainPanel.add(gameScreenContainer, "gameScreen");
    }
    
    private UIContainer createGameScreenContainer() {
        UIContainer gameContainer = new UIContainer();
        gameContainer.setLayout(new BorderLayout());
        
        gameScreenPanel = new GameScreen(selectedPlayerCount);
        gameScreenPanel.setVisible(true);
        
        MyButton backButton = MyButton.createUnoButton("Back to Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
                cardLayout.show(mainPanel, "mainMenu");
            }
        });

        MyButton sayUnoButton = MyButton.createUnoButton("Say UNO!");
        sayUnoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameLogic != null) {
                    // Check how many cards the player has
                    int cardCount = players.get(0).getHand().size();
                    
                    if (cardCount == 1) {
                        System.out.println(playerName + " SAID UNO! 🎮");
                        JOptionPane.showMessageDialog(UnoGameApp.this, "YOU SAID UNO!");
                    } else {
                        System.out.println(playerName + " tried to say UNO with " + cardCount + " cards!");
                        JOptionPane.showMessageDialog(UnoGameApp.this, 
                            "YOU STILL HAVE MORE CARDS YOU CANT SAY UNO", 
                            "Too Many Cards", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        UIContainer buttonPanel = new UIContainer();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(sayUnoButton);
        
        gameContainer.add(gameScreenPanel, BorderLayout.CENTER);
        gameContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        return gameContainer;
    }
    
    private void applyPlayerNamesAndAvatars() {
        players.get(0).setName(playerName);
        
        for (int i = 1; i < players.size(); i++) {
            players.get(i).setName("AI Player " + i);
        }
    }

    private void startGame() {
        if (gameInProgress) {
            stopGame();
        }
        
        gameInProgress = true;
        
        players = gameScreenPanel.getPlayers();
        
        applyPlayerNamesAndAvatars();
        
        gameLogic = gameScreenPanel.getGame();
        
        updateGameScreen();
        
        startGameUpdateTimer();
    }
    
    private void stopGame() {
        if (gameUpdateTimer != null) {
            gameUpdateTimer.cancel();
            gameUpdateTimer.purge();
        }
        gameInProgress = false;
    }
    
    private void startGameUpdateTimer() {
        if (gameUpdateTimer != null) {
            gameUpdateTimer.cancel();
            gameUpdateTimer.purge();
        }
        
        gameUpdateTimer = new Timer();
        gameUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateGameScreen();
                        
                        if (checkGameOver()) {
                            gameUpdateTimer.cancel();
                            JOptionPane.showMessageDialog(UnoGameApp.this, 
                                "Game Over! Winner: " + findWinner(), 
                                "Game Over", 
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
            }
        }, 2000, 2000);
    }
    
    private boolean checkGameOver() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private String findWinner() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return player.getName();
            }
        }
        return "Unknown";
    }
    
    private void updateGameScreen() {
        gameScreenPanel.updateGameUI();
    }
    
    private void showNotImplementedMessage(String feature) {
        final MYDialog dialog = new MYDialog(this, "Feature Not Available", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        UIContainer dialogContent = new UIContainer();
        dialogContent.setLayout(new BorderLayout(15, 15));
        dialogContent.setPadding(20);
        dialogContent.setBackground(new Color(15, 30, 80));
        
        UILabel messageLabel = new UILabel(feature);
        messageLabel.setTextColor(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setHorizontalAlignment(UILabel.CENTER);
        
        MyButton okButton = MyButton.createUnoButton("OK");
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setNormalColor(new Color(0, 120, 215));
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        UIContainer buttonPanel = new UIContainer();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        buttonPanel.setOpaque(false);
        
        dialogContent.add(messageLabel, BorderLayout.CENTER);
        dialogContent.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(dialogContent);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UnoGameApp().setVisible(true);
            }
        });
    }
}
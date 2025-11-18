package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PlayerSelectionScreen extends UIContainer {
    
    private final String resourcePath;
    private ActionListener playerSelectionListener;
    private ActionListener backListener; 
    private MyButton twoPlayersButton;
    private MyButton threePlayersButton;
    private MyButton fourPlayersButton;
    private MyButton backButton; // Added back button
    
    public PlayerSelectionScreen(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
        initializeComponents();
    }
    
    
    public void setPlayerSelectionListener(ActionListener listener) {
        this.playerSelectionListener = listener;
        
        twoPlayersButton.removeActionListeners();
        threePlayersButton.removeActionListeners();
        fourPlayersButton.removeActionListeners();
        
        twoPlayersButton.addActionListener(listener);
        threePlayersButton.addActionListener(listener);
        fourPlayersButton.addActionListener(listener);
    }
    
    public void setBackListener(ActionListener listener) {
        this.backListener = listener;
        
        if (backButton != null) {
            backButton.removeActionListeners();
            backButton.addActionListener(listener);
        }
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 30, 80)); // Deep blue background
        
        UIContainer mainContainer = new UIContainer() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int shadowSize = 10;
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - (shadowSize * 2),
                                 getHeight() - (shadowSize * 2), 20, 20);
            }
        };
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setPadding(0);
        
        UIContainer innerContainer = new UIContainer() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(15, 30, 80));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                GradientPaint gradientPaint = new GradientPaint(
                    0, 0, new Color(30, 60, 120, 70),
                    0, getHeight(), new Color(5, 15, 50, 70));
                g2d.setPaint(gradientPaint);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        innerContainer.setLayout(new BorderLayout());
        innerContainer.setOpaque(false);
        innerContainer.setPadding(15);
        
        UIContainer contentPanel = new UIContainer();
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setPadding(5);
        
        UIContainer centerPanel = new UIContainer();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        try {
            BufferedImage logoImage = ImageIO.read(new File(resourcePath + "uno4.png"));
            UIImage logoComponent = new UIImage(logoImage);
            logoComponent.defTailleImage(150, 150);
            
            UIContainer logoContainer = new UIContainer();
            logoContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
            logoContainer.setOpaque(false);
            logoContainer.add(logoComponent);
            
            gbc.gridy = 0;
            centerPanel.add(logoContainer, gbc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        UILabel titleLabel = new UILabel("Select Number of Players");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setOutlineThickness(0);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 30, 0);
        centerPanel.add(titleLabel, gbc);
        
        UIContainer topButtonsContainer = new UIContainer();
        topButtonsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        topButtonsContainer.setOpaque(false);
        
        twoPlayersButton = createStyledButton("2 players");
        topButtonsContainer.add(twoPlayersButton);
        
        threePlayersButton = createStyledButton("3 players");
        topButtonsContainer.add(threePlayersButton);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 20, 0);
        centerPanel.add(topButtonsContainer, gbc);
        
        UIContainer bottomButtonContainer = new UIContainer();
        bottomButtonContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomButtonContainer.setOpaque(false);
        
        fourPlayersButton = createStyledButton("4 players");
        bottomButtonContainer.add(fourPlayersButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 30, 0);
        centerPanel.add(bottomButtonContainer, gbc);
        
        UIContainer backButtonContainer = new UIContainer();
        backButtonContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        backButtonContainer.setOpaque(false);
        
        backButton = createStyledButton("Go Back");
        if (backListener != null) {
            backButton.addActionListener(backListener);
        }
        backButtonContainer.add(backButton);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 5, 0);
        centerPanel.add(backButtonContainer, gbc);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        innerContainer.add(contentPanel, BorderLayout.CENTER);
        
        mainContainer.add(innerContainer, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
    }
    
    private MyButton createStyledButton(String text) {
        MyButton button = new MyButton(text);
        button.setPreferredSize(new Dimension(200, 45));
        button.setNormalColor(new Color(0, 20, 70));  // Dark blue
        button.setHoverColor(new Color(0, 40, 100));  
        button.setPressedColor(new Color(0, 10, 50));  // Darker when pressed
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
    
    
    public int getSelectedPlayerCount(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.startsWith("2")) return 2;
        if (command.startsWith("3")) return 3;
        if (command.startsWith("4")) return 4;
        return 0; 
    }
}
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenuScreen extends UIContainer {
    private final String resourcePath;
    private ActionListener createGameAction;
    private ActionListener joinGameAction; 
    private ActionListener profileSettingAction;
    private ActionListener gameRulesAction;

    public MainMenuScreen(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
        initializeComponents();
    }

    public void setOnCreateGameAction(ActionListener action) {
        this.createGameAction = action;
        updateButtonAction("Create A Game", action);
    }
    
    public void setOnJoinGameAction(ActionListener action) {
        this.joinGameAction = action;
    }

    public void setOnProfileSettingAction(ActionListener action) {
        this.profileSettingAction = action;
        updateLabelAction("Profile Setting", action);
    }

    public void setOnGameRulesAction(ActionListener action) {
        this.gameRulesAction = action;
        updateLabelAction("Game Rules", action);
    }

    private void updateButtonAction(String buttonText, ActionListener action) {
        for (Component comp : getComponents()) {
            if (comp instanceof UIContainer) {
                findAndUpdateButton((UIContainer) comp, buttonText, action);
            }
        }
    }

    private void findAndUpdateButton(Container container, String buttonText, ActionListener action) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof MyButton && buttonText.equals(((MyButton) comp).getText())) {
                MyButton button = (MyButton) comp;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                if (action != null) {
                    button.addActionListener(action);
                }
            } else if (comp instanceof Container) {
                findAndUpdateButton((Container) comp, buttonText, action);
            }
        }
    }

    private void updateLabelAction(String labelText, ActionListener action) {
        for (Component comp : getComponents()) {
            if (comp instanceof UIContainer) {
                findAndUpdateLabel((UIContainer) comp, labelText, action);
            }
        }
    }

    private void findAndUpdateLabel(Container container, String labelText, ActionListener action) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof UILabel && labelText.equals(((UILabel) comp).getText())) {
                UILabel label = (UILabel) comp;
                for (MouseListener ml : label.getMouseListeners()) {
                    label.removeMouseListener(ml);
                }
                if (action != null) {
                    label.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            action.actionPerformed(new ActionEvent(label, ActionEvent.ACTION_PERFORMED, labelText));
                        }
                    });
                }
            } else if (comp instanceof Container) {
                findAndUpdateLabel((Container) comp, labelText, action);
            }
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 30, 80)); 
        
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
        
        UILabel titleLabel = new UILabel("Start Playing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setOutlineThickness(0);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 30, 0);
        centerPanel.add(titleLabel, gbc);
        
        UIContainer buttonsContainer = new UIContainer();
        buttonsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsContainer.setOpaque(false);
        
        MyButton createGameBtn = createStyledButton("Create A Game");
        if (createGameAction != null) {
            createGameBtn.addActionListener(createGameAction);
        }
        buttonsContainer.add(createGameBtn);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 30, 0);
        centerPanel.add(buttonsContainer, gbc);
        
        UIContainer bottomLinksContainer = new UIContainer();
        bottomLinksContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        bottomLinksContainer.setOpaque(false);
        
        UILabel profileSettingLink = createTextLink("Profile Setting");
        if (profileSettingAction != null) {
            profileSettingLink.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    profileSettingAction.actionPerformed(new ActionEvent(profileSettingLink, ActionEvent.ACTION_PERFORMED, "Profile Setting"));
                }
            });
        }
        
        UILabel gameRulesLink = createTextLink("Game Rules");
        if (gameRulesAction != null) {
            gameRulesLink.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    gameRulesAction.actionPerformed(new ActionEvent(gameRulesLink, ActionEvent.ACTION_PERFORMED, "Game Rules"));
                }
            });
        }
        
        bottomLinksContainer.add(profileSettingLink);
        bottomLinksContainer.add(gameRulesLink);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 5, 0);
        centerPanel.add(bottomLinksContainer, gbc);
        
        // Add center panel to content panel
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add content panel to inner container
        innerContainer.add(contentPanel, BorderLayout.CENTER);
        
        // Add inner container to main container
        mainContainer.add(innerContainer, BorderLayout.CENTER);
        
        // Add the main container to this panel
        add(mainContainer, BorderLayout.CENTER);
        
        // Add margins around the panel
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
    }
    
    private MyButton createStyledButton(String text) {
        MyButton button = new MyButton(text);
        button.setPreferredSize(new Dimension(200, 45));
        button.setNormalColor(new Color(0, 20, 70));  // Dark blue
        button.setHoverColor(new Color(0, 40, 100));  // Slightly lighter when hovered
        button.setPressedColor(new Color(0, 10, 50));  // Darker when pressed
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
    
    private UILabel createTextLink(String text) {
        UILabel link = new UILabel(text);
        link.setFont(new Font("Arial", Font.PLAIN, 14));
        link.setTextColor(Color.WHITE);
        link.setOutlineThickness(0);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return link;
    }
}
package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WelcomeScreen extends UIContainer {
    private final String resourcePath;
    private ActionListener continueButtonAction;
    private MyButton continueButton;  

    public WelcomeScreen(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
        initializeComponents();
    }

    public void setOnContinueAction(ActionListener action) {
        this.continueButtonAction = action;
        
        if (continueButton != null) {
            for (ActionListener listener : continueButton.getActionListeners()) {
                continueButton.removeActionListener(listener);
            }
            continueButton.addActionListener(action);
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setPadding(0);
        setPreferredSize(new Dimension(800, 600));
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

        UIContainer centerPanel = new UIContainer();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setPadding(0);

        try {
            BufferedImage logoImage = ImageIO.read(new File(resourcePath + "uno4.png"));
            
            UIContainer logoPanel = new UIContainer();
            logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            logoPanel.setOpaque(false);
            logoPanel.setPadding(0);
            
            UIImage unoLogo = new UIImage(logoImage);
            unoLogo.defTailleImage(200, 200);
            logoPanel.add(unoLogo);
            
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(logoPanel);
            
            UIContainer titlePanel = new UIContainer();
            titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            titlePanel.setOpaque(false);
            titlePanel.setPadding(0);
            
            UILabel welcomeLabel = new UILabel("Welcome to UNO The Game");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
            welcomeLabel.setTextColor(Color.WHITE);
            welcomeLabel.setOutlineThickness(0);
            titlePanel.add(welcomeLabel);
            
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            centerPanel.add(titlePanel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        } catch (Exception e) {
            UILabel errorLabel = new UILabel("Logo not found");
            errorLabel.setTextColor(Color.RED);
            centerPanel.add(errorLabel);
        }

        UIContainer buttonPanel = new UIContainer();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setPadding(0);
        
        continueButton = MyButton.createUnoButton("CONTINUER");
        continueButton.setPreferredSize(new Dimension(200, 50));
        continueButton.setNormalColor(new Color(0, 120, 215));
        continueButton.setForeground(Color.WHITE);
        
        if (continueButtonAction != null) {
            continueButton.addActionListener(continueButtonAction);
        }
        
        buttonPanel.add(continueButton);
        
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
}
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

public class NameEnteryScreen extends UIContainer {
    private final String resourcePath;
    private ActionListener saveButtonAction;
    private UITextField nameField;
    private UIImage avatarComponent;
    private UIContainer avatarImageContainer;
    private int currentAvatarIndex = 1;
    private final int totalAvatars = 5;

    public NameEnteryScreen(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
        initializeComponents();
    }

    public void setOnSaveAction(ActionListener action) {
        this.saveButtonAction = action;
        // Update button if it exists
        for (Component comp : getComponents()) {
            if (comp instanceof UIContainer) {
                updateButtonAction((UIContainer) comp);
            }
        }
    }

    private void updateButtonAction(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof MyButton && "Save & Go".equals(((MyButton) comp).getText())) {
                MyButton button = (MyButton) comp;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                button.addActionListener(saveButtonAction);
            } else if (comp instanceof Container) {
                updateButtonAction((Container) comp);
            }
        }
    }

    public String getPlayerName() {
        return nameField != null ? nameField.getText() : "";
    }

    public int getSelectedAvatarIndex() {
        return currentAvatarIndex;
    }

    private BufferedImage loadAvatarImage(int index) {
        try {
            return ImageIO.read(new File(resourcePath + "avatar" + index + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateAvatarDisplay() {
        try {
            BufferedImage avatarImage = loadAvatarImage(currentAvatarIndex);
            if (avatarImage != null && avatarComponent != null) {
                avatarComponent.setImage(avatarImage);
                avatarComponent.defTailleImage(80, 80);
                avatarImageContainer.revalidate();
                avatarImageContainer.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 30, 80)); // Deep blue background
        
        // Main container with drop shadow effect - NO GRAY BAR AT TOP
        UIContainer mainContainer = new UIContainer() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw the shadow effect
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw outer shadow (reduced shadow size)
                int shadowSize = 10;
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillRoundRect(shadowSize, shadowSize, getWidth() - (shadowSize * 2), 
                                 getHeight() - (shadowSize * 2), 20, 20);
            }
        };
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setPadding(0);
        
        // Inner container with blue background
        UIContainer innerContainer = new UIContainer() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw the main blue background
                g2d.setColor(new Color(15, 30, 80));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw subtle gradient overlay for depth
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
        
        // Content panel - using BorderLayout for better positioning
        UIContainer contentPanel = new UIContainer();
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setPadding(5);
        
        // Center panel for the main content (name field, avatar, button)
        UIContainer centerPanel = new UIContainer();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Title label
        UILabel titleLabel = new UILabel("Enter Your Name");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setOutlineThickness(0);
        
        gbc.gridy = 0;
        centerPanel.add(titleLabel, gbc);
        
        // Name field with cyan border
        nameField = new UITextField(16);
        nameField.setText("NOM");
        nameField.setFont(new Font("Arial", Font.BOLD, 18));
        nameField.setHorizontalAlignment(UITextField.CENTER);
        nameField.setPreferredSize(new Dimension(300, 40));
        nameField.setMaximumSize(new Dimension(300, 40));
        nameField.setBackground(new Color(15, 30, 80).brighter());
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorderColor(new Color(0, 180, 255));  // Cyan color for border
        nameField.setBorderThickness(2);
        nameField.setRoundedCorners(true);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 20, 0);
        centerPanel.add(nameField, gbc);
        
        // Avatar section
        UIContainer avatarSection = new UIContainer();
        avatarSection.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        avatarSection.setOpaque(false);
        
        // Avatar container with white border
        BufferedImage avatarImage = loadAvatarImage(currentAvatarIndex);
        avatarComponent = new UIImage(avatarImage);
        avatarComponent.defTailleImage(80, 80);
        
        // Avatar container with white border - using custom UIBorderFactory
        avatarImageContainer = new UIContainer();
        avatarImageContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarImageContainer.setOpaque(false);
        avatarImageContainer.setBorder(UIBorderFactory.createLineBorder(Color.WHITE, 3, false));
        avatarImageContainer.setPreferredSize(new Dimension(86, 86));
        avatarImageContainer.add(avatarComponent);
        
        avatarSection.add(avatarImageContainer);
        
        try {
            // Refresh button - Using MyButton instead of JButton
            BufferedImage refreshImage = ImageIO.read(new File(resourcePath + "refresh1.png"));
            ImageIcon refreshIcon = new ImageIcon(refreshImage);
            
            // Create a MyButton with the refresh icon
            MyButton refreshButton = MyButton.createRefreshButton(refreshIcon, new Dimension(40, 40));
            refreshButton.setTransparentBackground(true);
            refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentAvatarIndex = (currentAvatarIndex % totalAvatars) + 1;
                    updateAvatarDisplay();
                }
            });
            
            avatarSection.add(refreshButton);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 20, 0);
        centerPanel.add(avatarSection, gbc);
        
        // Save button
        MyButton saveButton = new MyButton("Save & Go");
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setNormalColor(new Color(0, 20, 70));  // Dark blue
        saveButton.setHoverColor(new Color(0, 40, 100));  // Slightly lighter when hovered
        saveButton.setPressedColor(new Color(0, 10, 50));  // Darker when pressed
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        if (saveButtonAction != null) {
            saveButton.addActionListener(saveButtonAction);
        }
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        centerPanel.add(saveButton, gbc);
        
        // Add center panel to content panel
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // UNO Logo at the top
        try {
            BufferedImage logoImage = ImageIO.read(new File(resourcePath + "uno4.png"));
            UIImage logoComponent = new UIImage(logoImage);
            logoComponent.defTailleImage(140, 100);
            
            UIContainer logoContainer = new UIContainer();
            logoContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
            logoContainer.setOpaque(false);
            logoContainer.add(logoComponent);
            
            contentPanel.add(logoContainer, BorderLayout.NORTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Assemble all containers
        innerContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(innerContainer, BorderLayout.CENTER);
        
        // Add the main container to the center of this panel
        add(mainContainer, BorderLayout.CENTER);
        
        // Add margins around the panel
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
    }
}
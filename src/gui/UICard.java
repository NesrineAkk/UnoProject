package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class UICard extends JPanel {
    private ImageIcon cardImage;
    private ImageIcon backImage;
    private String cardType;
    private boolean isClickable = true;
    private boolean showFront = true;
    private int rotationAngle = 0;
    private GameScreen gameScreen;

    public UICard(String cardType, GameScreen gameScreen) {
        this.cardType = cardType;
        this.showFront = true;
        this.gameScreen = gameScreen;
        
        initializeCard();
    }
   
    private void createDefaultWildCardGraphic(boolean isWildPlus, String colorPrefix) {
        int width = 60;
        int height = 90;
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, width, height, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(2, 2, width-4, height-4, 6, 6);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("WILD", 15, 20);
        
        if (isWildPlus) {
            g2d.drawString("+4", 22, 40);
        }
        
        int quarterWidth = width/2 - 6;
        int quarterHeight = height/2 - 6;
        
        g2d.setColor(new Color(255, 60, 60));
        g2d.fillRect(8, 45, quarterWidth, quarterHeight);
        
        g2d.setColor(new Color(0, 120, 255));
        g2d.fillRect(width/2, 45, quarterWidth, quarterHeight);
        
        g2d.setColor(new Color(0, 200, 80));
        g2d.fillRect(8, 45 + quarterHeight, quarterWidth, quarterHeight);
        
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillRect(width/2, 45 + quarterHeight, quarterWidth, quarterHeight);
        
        if (!colorPrefix.isEmpty()) {
            Color borderColor = null;
            switch (colorPrefix) {
                case "r": borderColor = new Color(255, 60, 60); break;
                case "g": borderColor = new Color(0, 200, 80); break;
                case "b": borderColor = new Color(0, 120, 255); break;
                case "y": borderColor = new Color(255, 215, 0); break;
            }
            
            if (borderColor != null) {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(4.0f));
                g2d.drawRoundRect(4, 4, width-8, height-8, 6, 6);
                
                g2d.fillOval(width - 18, 5, 14, 14);
                g2d.fillOval(4, 5, 14, 14);
                g2d.fillOval(width - 18, height - 19, 14, 14);
                g2d.fillOval(4, height - 19, 14, 14);
            }
        }
        
        g2d.dispose();
        cardImage = new ImageIcon(bufferedImage);
    }

    
    private boolean isWildCard(String cardType) {
        if (cardType == null || cardType.isEmpty()) {
            return false;
        }
        
        if (cardType.equals("w") || cardType.equals("w+")) {
            return true;
        }
        
        if (cardType.length() >= 2) {
            char colorPrefix = cardType.charAt(0);
            String remainingPart = cardType.substring(1);
            
            if ("rgby".indexOf(colorPrefix) != -1 && 
                (remainingPart.equals("w") || remainingPart.equals("w+"))) {
                return true;
            }
        }
        
        return cardType.contains("wild") || cardType.contains("Wild");
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (rotationAngle != 0) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);
        }
        
        int drawWidth = (rotationAngle == 90 || rotationAngle == 270) ? getHeight() : getWidth();
        int drawHeight = (rotationAngle == 90 || rotationAngle == 270) ? getWidth() : getHeight();
        
        int xOffset = (getWidth() - drawWidth) / 2;
        int yOffset = (getHeight() - drawHeight) / 2;
        
        if (showFront) {
            if (cardImage != null && cardImage.getIconWidth() > 0) {
                if (rotationAngle == 90 || rotationAngle == 270) {
                    g2d.drawImage(cardImage.getImage(), xOffset, yOffset, drawWidth, drawHeight, this);
                } else {
                    g2d.drawImage(cardImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
                
                if (isWildCard(cardType) && cardType.length() >= 2) {
                    char colorCode = cardType.charAt(0);
                    if ("rgby".indexOf(colorCode) != -1) {
                        Color indicatorColor = getColorFromCode(colorCode);
                        if (indicatorColor != null) {
                            g2d.setColor(indicatorColor);
                            g2d.fillOval(getWidth() - 18, 5, 14, 14);
                            g2d.setColor(Color.BLACK);
                            g2d.setStroke(new BasicStroke(1.5f));
                            g2d.drawOval(getWidth() - 18, 5, 14, 14);
                            
                            g2d.setColor(indicatorColor);
                            g2d.fillOval(getWidth() - 18, getHeight() - 19, 14, 14);
                            g2d.setColor(Color.BLACK);
                            g2d.drawOval(getWidth() - 18, getHeight() - 19, 14, 14);
                        }
                    }
                }
            } else {
                g2d.setColor(Color.GRAY);
                g2d.fillRect(xOffset, yOffset, drawWidth, drawHeight);
                g2d.setColor(Color.WHITE);
                
                g2d.drawString(cardType, drawWidth/2 - 10 + xOffset, drawHeight/2 + yOffset);
            }
        } else {
            if (backImage != null && backImage.getIconWidth() > 0) {
                if (rotationAngle == 90 || rotationAngle == 270) {
                    g2d.drawImage(backImage.getImage(), xOffset, yOffset, drawWidth, drawHeight, this);
                } else {
                    g2d.drawImage(backImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(xOffset, yOffset, drawWidth, drawHeight);
                g2d.setColor(Color.WHITE);
                g2d.drawString("BACK", drawWidth/2 - 10 + xOffset, drawHeight/2 + yOffset);
            }
        }
        
        g2d.dispose();
    }

    private Color getColorFromCode(char colorCode) {
        switch (colorCode) {
            case 'r': return new Color(255, 60, 60);
            case 'g': return new Color(0, 200, 80);
            case 'b': return new Color(0, 120, 255);
            case 'y': return new Color(255, 215, 0);
            default: return null;
        }
    }
    
    public UICard(String cardType, boolean showFront) {
        this.cardType = cardType;
        this.showFront = showFront;
        
        initializeCard();
    }
    
    private void initializeCard() {
        String resourcePath = "recources/";
        
        loadCardImage(resourcePath);
        
        String backImagePath = resourcePath + "back.png";
        File backImageFile = new File(backImagePath);
        if (backImageFile.exists()) {
            backImage = new ImageIcon(backImagePath);
            System.out.println("Back image loaded: " + backImagePath);
        } else {
            System.err.println("Back image file not found: " + backImagePath);
            backImage = null;
        }
        
        setPreferredSize(new Dimension(60, 100));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isClickable) {
                    cardClicked();
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isClickable) {
                    setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(null);
                repaint();
            }
        });
        
        setOpaque(false);
    }
    
    private void cardClicked() {
        System.out.println("Card " + cardType + " clicked");

        if (gameScreen != null) {
            gameScreen.cardPlayed(cardType);
        } 
    }

    public void setRotationAngle(int angle) {
        this.rotationAngle = angle;
        repaint();
    }

    public int getRotationAngle() {
        return rotationAngle;
    }
    
    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
        
        if (clickable) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    public void setShowFront(boolean showFront) {
        this.showFront = showFront;
        repaint();
    }
    
    public boolean isShowingFront() {
        return showFront;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }
    
    public void updateCardType(String newCardType) {
        this.cardType = newCardType;
        
        String resourcePath = "recources/";
        loadCardImage(resourcePath);
        
        repaint();
    }
    
    private void loadCardImage(String resourcePath) {
        System.out.println("Loading card image for type: " + cardType);
        
        String frontImagePath = resourcePath + "front_" + cardType + ".png";
        File frontImageFile = new File(frontImagePath);
        
        if (frontImageFile.exists()) {
            cardImage = new ImageIcon(frontImagePath);
            System.out.println("Front image loaded: " + frontImagePath);
            return;
        }
        
        if (isWildCard(cardType)) {
            loadWildCardImage(resourcePath);
        } else {
            createDefaultCardGraphic(cardType);
        }
    }

    private void createDefaultCardGraphic(String cardType) {
        int width = 60;
        int height = 90;
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color cardColor = Color.WHITE;
        String cardValue = cardType;
        
        if (cardType.length() >= 2) {
            char colorCode = cardType.charAt(0);
            cardValue = cardType.substring(1);
            
            switch (colorCode) {
                case 'r': cardColor = new Color(255, 60, 60); break;
                case 'g': cardColor = new Color(0, 200, 80); break;
                case 'b': cardColor = new Color(0, 120, 255); break;
                case 'y': cardColor = new Color(255, 215, 0); break;
                default: cardColor = Color.WHITE; break;
            }
        }
        
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, width, height, 8, 8);
        g2d.setColor(cardColor);
        g2d.fillRoundRect(2, 2, width-4, height-4, 6, 6);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (width - metrics.stringWidth(cardValue)) / 2;
        int textY = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2d.drawString(cardValue, textX, textY);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(3, 3, width-6, height-6, 6, 6);
        
        g2d.dispose();
        cardImage = new ImageIcon(bufferedImage);
    }

    private void loadWildCardImage(String resourcePath) {
        System.out.println("Loading wild card image for type: " + cardType);
        
        boolean isWildPlus = cardType.contains("+") || cardType.contains("w+");
        boolean hasColorPrefix = cardType.length() >= 2 && "rgby".indexOf(cardType.charAt(0)) != -1;
        
        String colorPrefix = hasColorPrefix ? cardType.substring(0, 1) : "";
        
        String baseWildName = isWildPlus ? "w+" : "w";
        
        String[] possiblePaths = {
            resourcePath + "front_" + cardType + ".png",
            resourcePath + "front_" + colorPrefix + baseWildName + ".png",
            resourcePath + "front_" + baseWildName + ".png", 
            resourcePath + "front_" + colorPrefix + "wild" + (isWildPlus ? "+4" : "") + ".png",
            resourcePath + "front_wild" + (isWildPlus ? "+4" : "") + ".png",
            resourcePath + "front_wild.png"
        };
        
        for (String path : possiblePaths) {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                cardImage = new ImageIcon(path);
                System.out.println("Wild card image loaded: " + path);
                return;
            } else {
                System.out.println("Tried path not found: " + path);
            }
        }
        
        System.err.println("Could not find any wild card image for " + cardType + " - creating default");
        createDefaultWildCardGraphic(isWildPlus, colorPrefix);
    }
}
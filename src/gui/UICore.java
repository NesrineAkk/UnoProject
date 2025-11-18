package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

public final class UICore {
    private UICore() {}

    public static final Color BACKGROUND_COLOR = new Color(15, 30, 80);      // Bleu foncé UNO
    public static final Color PRIMARY_COLOR = new Color(237, 28, 36);        // Rouge UNO
    public static final Color SECONDARY_COLOR = new Color(254, 222, 0);      // Jaune UNO
    public static final Color ACCENT_COLOR = new Color(0, 158, 96);          // Vert UNO
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color DISABLED_COLOR = new Color(100, 100, 100);
    public static final Color WHITE_UNO = Color.WHITE;
  
    
    public static final Color RED_CARD_COLOR = new Color(237, 28, 36);
    public static final Color BLUE_CARD_COLOR = new Color(0, 114, 188);
    public static final Color GREEN_CARD_COLOR = new Color(0, 158, 96);
    public static final Color YELLOW_CARD_COLOR = new Color(254, 222, 0);
    public static final Color WILD_CARD_COLOR = new Color(30, 30, 30);

    public static final Font UNO_FONT = createFont("Arial Rounded MT Bold", Font.BOLD, 18);
    public static final Font TITLE_FONT = deriveFont(UNO_FONT, 36f);
    public static final Font BUTTON_FONT = deriveFont(UNO_FONT, 20f);
    public static final Font CARD_FONT = deriveFont(UNO_FONT, 24f);

    public static final Dimension CARD_SIZE = new Dimension(80, 120);
    public static final Dimension BUTTON_SIZE = new Dimension(200, 50);
    public static final Dimension WINDOW_SIZE = new Dimension(900, 700);
    
    public static final int SMALL_SPACE = 5;
    public static final int MEDIUM_SPACE = 10;
    public static final int LARGE_SPACE = 20;
    public static final int CARD_OVERLAP = 15;

    public static final Border CARD_BORDER = BorderFactory.createLineBorder(Color.WHITE, 2);
    public static final Border BUTTON_BORDER = BorderFactory.createEmptyBorder(10, 20, 10, 20);
    public static final Border CONTAINER_BORDER = BorderFactory.createEmptyBorder(
        MEDIUM_SPACE, MEDIUM_SPACE, MEDIUM_SPACE, MEDIUM_SPACE
    );

    private static final String RESOURCE_PATH = "/Users/sofia/Desktop/";
    public static final String CARD_IMAGES_PATH = RESOURCE_PATH + "UNOcards/";
    public static final String ICON_IMAGES_PATH = RESOURCE_PATH + "icons/";
    public static final String AVATAR_IMAGES_PATH = RESOURCE_PATH + "avatars/";

    public static void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setUIManagerDefaults();
        } catch (Exception e) {
            System.err.println("Erreur configuration LookAndFeel: " + e.getMessage());
        }
    }

    private static void setUIManagerDefaults() {
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Label.font", UNO_FONT);
        UIManager.put("TextField.font", UNO_FONT);
        UIManager.put("OptionPane.messageFont", UNO_FONT);
    }

    public static void enableAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static Font deriveFont(Font baseFont, float size) {
        return baseFont.deriveFont(size);
    }

    private static Font createFont(String name, int style, float size) {
        try {
            return Font.getFont(name).deriveFont(style, size);
        } catch (Exception e) {
            return new Font("Arial", style, (int)size);
        }
    }

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Erreur chargement image: " + path);
            return createMissingImage();
        }
    }

    public static ImageIcon loadIcon(String filename, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(ICON_IMAGES_PATH + filename));
            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.err.println("Erreur chargement icône: " + filename);
            return new ImageIcon();
        }
    }

    public static BufferedImage loadCardImage(String color, String value) {
        String filename = value.equals("back") ? "back.png" : "front_" + color + "_" + value + ".png";
        return loadImage(CARD_IMAGES_PATH + filename);
    }

    private static BufferedImage createMissingImage() {
        BufferedImage img = new BufferedImage(CARD_SIZE.width, CARD_SIZE.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, CARD_SIZE.width, CARD_SIZE.height, 15, 15);
        
        g2d.setColor(Color.RED);
        g2d.drawRoundRect(0, 0, CARD_SIZE.width-1, CARD_SIZE.height-1, 15, 15);
        
        g2d.setColor(Color.BLACK);
        g2d.drawString("Carte manquante", 10, CARD_SIZE.height/2);
        
        g2d.dispose();
        return img;
    }

    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BUTTON_BORDER);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(UNO_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(BACKGROUND_COLOR.brighter());
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
}
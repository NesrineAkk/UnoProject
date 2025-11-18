package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class UIImage extends JLabel {
    private BufferedImage image;
    private boolean roundedCorners = false;
    private int cornerRadius = 15;
    private boolean shadow = false;
    private int shadowSize = 3;
    private Color shadowColor = new Color(0, 0, 0, 100);
    
    public UIImage(BufferedImage image) {
        super();
        this.image = image;
        if (image != null) {
            this.setIcon(new ImageIcon(image));
        }
        setOpaque(false);
    }
    
    public void setRoundedCorners(boolean rounded) {
        this.roundedCorners = rounded;
        repaint();
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        repaint();
    }
    
    public void setShadowSize(int size) {
        this.shadowSize = size;
        repaint();
    }
    
    public void setShadowColor(Color color) {
        this.shadowColor = color;
        repaint();
    }
    
    public void defTailleImage(int largeur, int hauteur) {
        if (image != null) {
            Image imageRedimensionnee = image.getScaledInstance(largeur, hauteur, Image.SCALE_SMOOTH);
            
            if (roundedCorners) {
                // Create a rounded version
                BufferedImage rounded = createRoundedImage(imageRedimensionnee, largeur, hauteur, cornerRadius);
                this.setIcon(new ImageIcon(rounded));
            } else {
                this.setIcon(new ImageIcon(imageRedimensionnee));
            }
            
            setPreferredSize(new Dimension(largeur, hauteur));
        }
    }
    
    private BufferedImage createRoundedImage(Image src, int width, int height, int radius) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        
        // For high quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw shadow if enabled
        if (shadow) {
            g2.setColor(shadowColor);
            g2.fill(new RoundRectangle2D.Float(shadowSize, shadowSize, 
                                               width - shadowSize*2, 
                                               height - shadowSize*2, 
                                               radius, radius));
        }
        
        // Clip rounded rectangle
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));
        
        // Draw image
        g2.drawImage(src, 0, 0, width, height, null);
        
        g2.dispose();
        return result;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (shadow && !roundedCorners) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(shadowColor);
            g2d.fillRect(shadowSize, shadowSize, getWidth() - shadowSize*2, getHeight() - shadowSize*2);
            g2d.dispose();
        }
        
        super.paintComponent(g);
    }
    
    // Static creation methods
    public static UIImage createFromPath(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = buffered.createGraphics();
            g2d.drawImage(scaled, 0, 0, null);
            g2d.dispose();
            return new UIImage(buffered);
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return new UIImage(null);
        }
    }
    
    // Create a circular image (useful for avatars)
    public static UIImage createCircularImage(BufferedImage original, int diameter) {
        if (original == null) {
            return new UIImage(null);
        }
        
        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circularImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create circular clipping region
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, diameter, diameter));
        
        // Scale and draw the original image
        Image scaledImage = original.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);
        g2.drawImage(scaledImage, 0, 0, null);
        
        g2.dispose();
        
        UIImage result = new UIImage(circularImage);
        return result;
    }
    
    public void setImage(BufferedImage newImage) {
        this.image = newImage;
        
        if (image != null) {
            // Get current dimension
            int width = getPreferredSize().width;
            int height = getPreferredSize().height;
            
            // If no size was previously set, use the image's dimensions
            if (width <= 0 || height <= 0) {
                width = image.getWidth();
                height = image.getHeight();
            }
            
            Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            
            if (roundedCorners) {
                BufferedImage rounded = createRoundedImage(resizedImage, width, height, cornerRadius);
                this.setIcon(new ImageIcon(rounded));
            } else {
                this.setIcon(new ImageIcon(resizedImage));
            }
        } else {
            this.setIcon(null);
        }
        
        revalidate();
        repaint();
    }
}
package gui;
import javax.swing.BorderFactory;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public class UIBorderFactory {
    
    public static Border createEmptyBorder() {
        return BorderFactory.createEmptyBorder();
    }
    
    public static Border createEmptyBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
    
    public static Border createLineBorder(Color color) {
        return BorderFactory.createLineBorder(color);
    }
    
    public static Border createLineBorder(Color color, int thickness) {
        return BorderFactory.createLineBorder(color, thickness);
    }
    
    public static Border createLineBorder(Color color, int thickness, boolean rounded) {
        if (rounded) {
            return new RoundLineBorder(color, thickness, 10);
        }
        return BorderFactory.createLineBorder(color, thickness);
    }
    
    public static Border createCompoundBorder(Border outside, Border inside) {
        return BorderFactory.createCompoundBorder(outside, inside);
    }
    
    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(title);
    }
    
    public static Border createTitledBorder(Border border, String title) {
        return BorderFactory.createTitledBorder(border, title);
    }
    
    private static class RoundLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;
        
        public RoundLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = thickness;
            return insets;
        }
    }
}
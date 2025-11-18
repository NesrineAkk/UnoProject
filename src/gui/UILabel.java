package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UILabel extends JLabel {
    private Color textColor = Color.WHITE;
    private Color outlineColor = Color.RED;
    private int outlineThickness = 2;

    public UILabel(String text) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(textColor);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(color);
        repaint();
    }

    public void setOutlineColor(Color color) {
        this.outlineColor = color;
        repaint();
    }

    public void setOutlineThickness(int thickness) {
        this.outlineThickness = thickness;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics f = getFontMetrics(getFont());
        int width = f.stringWidth(getText()) + outlineThickness * 4;
        int height = f.getHeight() + outlineThickness * 4;
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (outlineThickness <= 0) {
            super.paintComponent(g);
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics f = g2.getFontMetrics();
        String text = getText();
        int textWidth = f.stringWidth(text);
        int textHeight = f.getHeight();

        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + f.getAscent();

        g2.setColor(outlineColor);
        for (int i = -outlineThickness; i <= outlineThickness; i++) {
            for (int j = -outlineThickness; j <= outlineThickness; j++) {
                if (i * i + j * j <= outlineThickness * outlineThickness) {
                    g2.drawString(text, x + i, y + j);
                }
            }
        }

        g2.setColor(textColor);
        g2.drawString(text, x, y);
        g2.dispose();
    }
}
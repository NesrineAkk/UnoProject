package gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyButton extends JButton {
    private Color normalColor = UICore.PRIMARY_COLOR;
    private Color hoverColor = UICore.PRIMARY_COLOR.brighter();
    private Color pressedColor = UICore.ACCENT_COLOR;
    private boolean isIconButton = false;
    private int cornerRadius = 15;
    private boolean isTransparent = false;
    
    public MyButton() {
        super();
        initButton();
    }
    
    public MyButton(String text) {
        super(text);
        initButton();
    }
    
    public MyButton(Icon icon) {
        super(icon);
        isIconButton = true;
        initButton();
    }
    
    public MyButton(String text, Icon icon) {
        super(text, icon);
        initButton();
    }
    
    private void initButton() {
        setFont(UICore.BUTTON_FONT);
        setForeground(UICore.TEXT_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(normalColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new ButtonHoverEffect());
    }
    
    public static MyButton createUnoButton(String text) {
        MyButton button = new MyButton(text);
        button.setNormalColor(UICore.PRIMARY_COLOR);
        button.setHoverColor(UICore.PRIMARY_COLOR.brighter());
        button.setPressedColor(UICore.SECONDARY_COLOR);
        return button;
    }
    
    public static MyButton createIconButton(Icon icon) {
        MyButton button = new MyButton(icon);
        button.isIconButton = true;
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setContentAreaFilled(false);
        button.setOpaque(false); 
        button.isTransparent = true;
        return button;
    }
    
    public static MyButton createRefreshButton(Icon icon, Dimension size) {
        MyButton button = createIconButton(icon);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    public void setTransparentBackground(boolean transparent) {
        this.isTransparent = transparent;
        setOpaque(!transparent);
        setContentAreaFilled(!transparent);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (isIconButton) {
            if (!isTransparent) {
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            }
            
            Icon icon = getIcon();
            if (icon != null) {
                int x = (getWidth() - icon.getIconWidth()) / 2;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, x, y);
            }
        } else {
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            Icon icon = getIcon();
            int textX = 0;
            if (icon != null) {
                int iconY = (getHeight() - icon.getIconHeight()) / 2;
                int iconX = 5; 
                icon.paintIcon(this, g2, iconX, iconY);
                textX += icon.getIconWidth() + 5; 
            }
            
            if (getText() != null && !getText().isEmpty()) {
                FontMetrics fm = g2.getFontMetrics();
                Rectangle r = new Rectangle(getWidth(), getHeight());
                int x;
                if (icon != null) {
                    x = textX + 5; 
                } else {
                    x = (r.width - fm.stringWidth(getText())) / 2; 
                }
                int y = (r.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
            }
        }
        
        g2.dispose();
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        setBackground(color);
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = color;
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint(); 
    }
    
    public static MyButton fromJButton(JButton button) {
        MyButton myButton;
        if (button.getIcon() != null && button.getText() != null && !button.getText().isEmpty()) {
            myButton = new MyButton(button.getText(), button.getIcon());
        } else if (button.getIcon() != null) {
            myButton = createIconButton(button.getIcon());
        } else {
            myButton = new MyButton(button.getText());
        }
        
        myButton.setPreferredSize(button.getPreferredSize());
        myButton.setFont(button.getFont());
        myButton.setForeground(button.getForeground());
        myButton.setContentAreaFilled(button.isContentAreaFilled());
        myButton.setBorderPainted(button.isBorderPainted());
        myButton.setFocusPainted(button.isFocusPainted());
        
        for (java.awt.event.ActionListener listener : button.getActionListeners()) {
            myButton.addActionListener(listener);
        }
        
        return myButton;
    }
    
    public void removeActionListeners() {
        ActionListener[] listeners = this.getActionListeners();
        
        for (ActionListener listener : listeners) {
            this.removeActionListener(listener);
        }
    }
    
    private class ButtonHoverEffect extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (!isIconButton || !isTransparent) {
                setBackground(hoverColor);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            if (!isIconButton || !isTransparent) {
                setBackground(normalColor);
            }
            setCursor(Cursor.getDefaultCursor());
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (!isIconButton || !isTransparent) {
                setBackground(pressedColor);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (!isIconButton || !isTransparent) {
                setBackground(contains(e.getPoint()) ? hoverColor : normalColor);
            }
        }
    }
}
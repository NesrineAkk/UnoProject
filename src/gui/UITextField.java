package gui;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class UITextField extends JTextField {
    private Color normalBackgroundColor = UICore.BACKGROUND_COLOR.brighter();
    private Color focusBackgroundColor = UICore.BACKGROUND_COLOR.brighter().brighter();
    private Color borderColor = UICore.WHITE_UNO;
    private int borderThickness = 2;
    private boolean roundedCorners = true;
    
    public UITextField(int columns) {
        super(columns);
        setupTextField();
    }
    
    public UITextField(String text, int columns) {
        super(text, columns);
        setupTextField();
    }
    
    private void setupTextField() {
        setFont(UICore.UNO_FONT.deriveFont(18f));
        setHorizontalAlignment(JTextField.CENTER);
        setCaretColor(Color.WHITE);
        setForeground(UICore.TEXT_COLOR);
        setBackground(normalBackgroundColor);
        updateBorder();
        
        // Add focus listener for background change
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBackground(focusBackgroundColor);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                setBackground(normalBackgroundColor);
            }
        });
    }
    
    public void setFixedSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        updateBorder();
    }
    
    public void setBorderThickness(int thickness) {
        this.borderThickness = thickness;
        updateBorder();
    }
    
    public void setRoundedCorners(boolean rounded) {
        this.roundedCorners = rounded;
        updateBorder();
    }
    
    private void updateBorder() {
        if (roundedCorners) {
            setBorder(BorderFactory.createCompoundBorder(
                UIBorderFactory.createLineBorder(borderColor, borderThickness, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        } else {
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderThickness),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        }
    }
    
    public void setPlaceholder(String placeholder) {
        // This is a basic placeholder implementation
        // A more sophisticated version would use DocumentListener and FocusListener
        setText(placeholder);
        setForeground(UICore.DISABLED_COLOR);
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(UICore.TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(UICore.DISABLED_COLOR);
                }
            }
        });
    }
}
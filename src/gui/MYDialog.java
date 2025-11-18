package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class MYDialog extends JDialog {
    
    private UIContainer contentPanel;
    private UIContainer buttonPanel;
    private JPanel titlePanel;
    private UILabel titleLabel;
    private JButton closeButton;
    
    private Color backgroundColor = new Color(15, 30, 80);
    private Color titleBarColor = new Color(10, 20, 60);
    private Color textColor = Color.WHITE;
    private Font titleFont = new Font("Arial", Font.BOLD, 18);
    
    
    public MYDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        initComponents(title);
        setupLayout();
        
        makeDraggable();
        
        setUndecorated(true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
    }
    
   
    private void initComponents(String title) {
        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(titleBarColor);
        titlePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        titleLabel = new UILabel(title);
        titleLabel.setFont(titleFont);
        titleLabel.setTextColor(textColor);
        
        closeButton = new JButton("×");
        closeButton.setForeground(textColor);
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        contentPanel = new UIContainer();
        contentPanel.setBackground(backgroundColor);
        contentPanel.setPadding(20);
        contentPanel.setLayout(new BorderLayout());
        
        buttonPanel = new UIContainer();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    }
    
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(closeButton, BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createLineBorder(titleBarColor, 2));
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
   
    private void makeDraggable() {
        final Point dragPoint = new Point();
        
        titlePanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragPoint.x = e.getX();
                dragPoint.y = e.getY();
            }
        });
        
        titlePanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                setLocation(location.x + e.getX() - dragPoint.x,
                           location.y + e.getY() - dragPoint.y);
            }
        });
    }
    
    
    public void setContent(JComponent component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    
    public void addButton(JButton button) {
        buttonPanel.add(button);
        buttonPanel.revalidate();
    }
    
   
    public void setDialogBackgroundColor(Color color) {
        this.backgroundColor = color;
        contentPanel.setBackground(color);
        buttonPanel.setBackground(color);
        repaint();
    }
    
    
    public void setTitleBarColor(Color color) {
        this.titleBarColor = color;
        titlePanel.setBackground(color);
        repaint();
    }
    
    
    public UIContainer getContentPanel() {
        return contentPanel;
    }
    
   
    public UIContainer getButtonPanel() {
        return buttonPanel;
    }
}
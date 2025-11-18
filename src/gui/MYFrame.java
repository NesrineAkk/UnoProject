package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MYFrame extends JFrame {
    public MYFrame() {
        setTitle("UNO Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        applyUNOStyle();
    }
    
    private void applyUNOStyle() {
        this.getContentPane().setBackground(UICore.BACKGROUND_COLOR);
        
        try {
            String resourcePath = "recources/";
            ImageIcon icon = new ImageIcon(resourcePath + "unoo.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }
    
    public void addComponent(JComponent component, String constraints) {
        getContentPane().add(component, constraints);
    }
    
    public void addContainer(JPanel container, String constraints) {
        getContentPane().add(container, constraints);
    }
    
    public void display() {
        setVisible(true);
    }
    
    public void setupUNOLayout() {
        setLayout(new BorderLayout());
    }
}
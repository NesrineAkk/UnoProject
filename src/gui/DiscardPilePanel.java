package gui;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiscardPilePanel extends JPanel {
    private UICard topCard;
    private JLabel titleLabel;
    private GameScreen gameScreen;

    public DiscardPilePanel(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        titleLabel = new JLabel("DISCARD");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.max(getWidth(), getHeight()) / 2;

                RadialGradientPaint paint = new RadialGradientPaint(
                    centerX, centerY, radius,
                    new float[] { 0.0f, 0.7f, 1.0f },
                    new Color[] {
                        new Color(255, 255, 255, 70),
                        new Color(255, 255, 255, 20),
                        new Color(0, 0, 0, 0)
                    }
                );
                g2d.setPaint(paint);
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(180, 180));

        topCard = new UICard("w", true); 
        cardPanel.add(topCard);

        add(titleLabel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(200, 220));
    }

   

    public void updateTitleColor(String cardType) {
        Color titleColor = Color.WHITE;

        if (cardType != null && cardType.length() >= 1) {
            char colorCode = cardType.charAt(0);
            switch (colorCode) {
                case 'b':
                    titleColor = new Color(0, 120, 255); // Blue
                    break;
                case 'r':
                    titleColor = new Color(255, 60, 60); // Red
                    break;
                case 'g':
                    titleColor = new Color(0, 200, 80); // Green
                    break;
                case 'y':
                    titleColor = new Color(255, 215, 0); // Yellow
                    break;
                default:
                    break;
            }
        }

        titleLabel.setForeground(titleColor);
    }
    public void updateCard(String cardType) {
        System.out.println("Updating discard pile with card type: " + cardType);
        
        if (cardType == null || cardType.isEmpty()) {
            System.err.println("Warning: Null or empty card type");
            return;
        }
        
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && component != titleLabel) {
                JPanel cardPanel = (JPanel) component;
                cardPanel.removeAll();
                
                this.topCard = new UICard(cardType, true);
                this.topCard.setGameScreen(gameScreen);
                this.topCard.setClickable(false);
                
                cardPanel.add(topCard);
                cardPanel.revalidate();
                cardPanel.repaint();
                break;
            }
        }

        updateTitleColor(cardType);
        
        revalidate();
        repaint();
    }
   
}
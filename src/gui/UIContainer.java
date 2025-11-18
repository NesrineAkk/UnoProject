package gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Color;

public class UIContainer extends JPanel {
    private Insets padding = new Insets(0, 0, 0, 0);
    private String containerName = "";
    private boolean hasBorder = false;
	private int cornerRadius;


    public UIContainer() {
        super();
        setOpaque(false);
        setBackground(new Color(30, 40, 60)); // BACKGROUND_COLOR
    }

    public UIContainer(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBackground(new Color(30, 40, 60)); // BACKGROUND_COLOR
    }

    public void setPadding(int top, int left, int bottom, int right) {
        this.padding = new Insets(top, left, bottom, right);
        updateBorder();
    }

    public void setPadding(int padding) {
        setPadding(padding, padding, padding, padding);
    }

    public void setContainerName(String name) {
        this.containerName = name;
        updateBorder();
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        updateBorder();
    }
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint(); 
    }

    private void updateBorder() {
        Border paddingBorder = BorderFactory.createEmptyBorder(
            padding.top, padding.left, padding.bottom, padding.right
        );

        if (hasBorder && !containerName.isEmpty()) {
            Border titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                containerName
            );
            setBorder(BorderFactory.createCompoundBorder(titledBorder, paddingBorder));
        } else if (hasBorder) {
            Border lineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
            setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));
        } else {
            setBorder(paddingBorder);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        }
        super.paintComponent(g);
    }

    @Override
    public Component add(Component comp) {
        return super.add(comp);
    }

    @Override
    public Component add(Component comp, int index) {
        return super.add(comp, index);
    }

    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, constraints, index);
    }

    public void clearComponents() {
        removeAll();
        revalidate();
        repaint();
    }
}
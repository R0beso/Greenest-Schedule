import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class StyleButtons {
    
    public BasicButtonUI getBasicButtonUI(String buttonText) {
        return new BasicButtonUI() {
            // Base colors for the design
            private final Color BASE_COLOR = new Color(245, 245, 245);
            private final Color SHADOW_COLOR = new Color(227, 227, 227);
            private final Color HOVER_COLOR = new Color(225, 225, 225);
            private final Color PRESSED_COLOR = new Color(197, 195, 197);
            private final Color BORDER_COLOR = new Color(170, 170, 170);
            
            private final int BUTTON_ROUNDNESS = 10;
            
            @Override
            public void installUI(JComponent component) {
                // Call original method to properly initialize UI
                super.installUI(component);
                
                // Verify the component is a JButton
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    // Configure button settings
                    button.setContentAreaFilled(false);
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    
                    // Mouse interaction effects
                    button.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent event) {
                            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            button.repaint(); // Force redraw to update colors
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent event) {
                            button.repaint();
                        }
                    });
                    
                    button.setText(buttonText);
                }
            }
            
            @Override
            public void paint(Graphics graphics, JComponent component) {
                Graphics2D g2d = (Graphics2D) graphics.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                JButton button = (JButton) component;
                ButtonModel state = button.getModel();
                
                // Select base color based on button state
                Color baseColor;
                if (state.isPressed()) {
                    baseColor = PRESSED_COLOR;
                } else if (state.isRollover()) {
                    baseColor = HOVER_COLOR;
                } else {
                    baseColor = BASE_COLOR;
                }
                
                // Create gradient from base color to darker version
                GradientPaint gradient = new GradientPaint(
                    0, 10, baseColor, 
                    0, component.getHeight(), SHADOW_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), BUTTON_ROUNDNESS, BUTTON_ROUNDNESS);
                
                // Dynamic border (darker on hover)
                g2d.setColor(state.isRollover() ? BORDER_COLOR.darker() : BORDER_COLOR);
                g2d.drawRoundRect(0, 0, component.getWidth() - 1, component.getHeight() - 1, BUTTON_ROUNDNESS, BUTTON_ROUNDNESS);
                
                // Contrast text rendering
                FontMetrics metrics = g2d.getFontMetrics();
                int xPosition = (component.getWidth() - metrics.stringWidth(button.getText())) / 2;
                int yPosition = (component.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.setColor(state.isRollover() ? new Color(30, 30, 30) : new Color(60, 60, 60));
                g2d.drawString(button.getText(), xPosition, yPosition);
                
                g2d.dispose();
            }
        };
    }
}


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

public class EstilosBotones {
	
	public BasicButtonUI getBasicButtonUI(String TEXTO_BOTON) {
	    return new BasicButtonUI() {
	        // Colores base para el diseño
	        private final Color BASE_COLOR = new Color(245, 245, 245);
	        private final Color HOVER_COLOR = new Color(225, 225, 225);
	        private final Color SHADOW_COLOR = new Color(218, 218, 218);
	        private final Color PRESSED_COLOR = new Color(197, 195, 197);
	        private final Color BORDER_COLOR = new Color(170, 170, 170);
	        
	        private final int REDONDEAR_BOTON = 10;
	        
	        @Override
	        public void installUI(JComponent c) {
	            // Llamamos al método original para inicializar correctamente la UI
	            super.installUI(c);
	            
	            // Comprobamos que el componente sea un JButton
	            if (c instanceof JButton) {
	                JButton button = (JButton) c;
	                // Configuramos el botón según nuestras necesidades
	                button.setContentAreaFilled(false);
	                button.setBorderPainted(false);
	                button.setFocusPainted(false);
	                button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
	                
	                // Configuración de efectos
	                button.addMouseListener(new MouseAdapter() {
	                    @Override
	                    public void mouseEntered(MouseEvent e) {
	                        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	                        button.repaint(); // Fuerza redibujado para actualizar colores
	                    }
	                    
	                    @Override
	                    public void mouseExited(MouseEvent e) {
	                        button.repaint();
	                    }
	                });
	                
	                button.setText(TEXTO_BOTON);
	            }
	        }
	        
	        @Override
	        public void paint(Graphics g, JComponent c) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            
	            JButton btn = (JButton) c;
	            ButtonModel model = btn.getModel();
	            
	            // Seleccionamos el color base según el estado del botón
	            Color base;
	            if (model.isPressed()) {
	                base = PRESSED_COLOR;
	            } else if (model.isRollover()) {
	                base = HOVER_COLOR;
	            } else {
	                base = BASE_COLOR;
	            }
	            
	            // Creamos un gradiente que va de 'base' a una versión ligeramente más oscura
	            GradientPaint gp = new GradientPaint(
	                0, 10, base, 
	                0, c.getHeight(), SHADOW_COLOR
	            );
	            g2.setPaint(gp);
	            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), REDONDEAR_BOTON, REDONDEAR_BOTON);
	            
	            // Borde dinámico (más oscuro en hover)
	            g2.setColor(model.isRollover() ? BORDER_COLOR.darker() : BORDER_COLOR);
	            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, REDONDEAR_BOTON, REDONDEAR_BOTON);
	            
	            // Texto con contraste
	            FontMetrics fm = g2.getFontMetrics();
	            int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
	            int y = (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
	            g2.setColor(model.isRollover() ? new Color(30, 30, 30) : new Color(60, 60, 60));
	            g2.drawString(btn.getText(), x, y);
	            
	            g2.dispose();
	        }
	    };
	}


}

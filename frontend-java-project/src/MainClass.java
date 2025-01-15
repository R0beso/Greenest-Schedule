

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainClass extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JButton botonGenerar;
	private JButton botonGraficas;
	private JButton botonPublicar;
	private JButton botonTablaPosiciones;
	private JLabel etiqResultados1;
	private JLabel etiqResultados2;
	private JLabel etiqPromedio;
	private JLabel etiqRecord;
	private JLabel etiqContador;
	private int contador;
	private JLabel etiqConexion;
	private Horario horario;
	private Horario mejorHorario;
	private double mejorCalificacionActual;
	private int posicionHorario;
	private String rutaBackend;
	private ScheduledExecutorService scheduler;
	private List<Horario> listaHorarios;
    private CookieManager cookieManager;
    private ImageIcon icon;

	
    public static void main(String[] args) {
    	new MainClass();
    }
    
    public MainClass() {
    	super("Greenest Schedule");
    	this.rutaBackend = "https://lanq.com.mx/spring/horario-api";
    	this.listaHorarios = new ArrayList<>();
    	this.cookieManager = new CookieManager();
    	this.posicionHorario = 0;
    	this.mejorCalificacionActual = 0;
    	this.contador = 0;
    	
    	
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // Establecer CookieManager como el predeterminado para las conexiones
        java.net.CookieHandler.setDefault(cookieManager);
    	
        //
    	creaVentanaPrincipal();
    	iniciarVerificacion();
    	inicializarListaHorarios();
    	cargarHorario();
    	
    	this.setVisible(true);
    	
    }
    
    private void inicializarListaHorarios() {

    	nuevosHorarios(true); // + 10
    	nuevosHorarios(true); // + 20 horarios agregados a la lista
    	
	}

	public void iniciarVerificacion() {
        scheduler = Executors.newScheduledThreadPool(1);

        Runnable tarea = () -> {
            if (ConexiónServidor()) {
            	etiqConexion.setText("    En línea ✓");
            }else {
            	etiqConexion.setText("Sin conexión ❌");
            }
        };

        // Ejecutar la tarea cada 5 segundos
        scheduler.scheduleAtFixedRate(tarea, 0, 5, TimeUnit.SECONDS);
    }
    
    public void creaVentanaPrincipal() {
    	botonGenerar = new JButton("Siguiente horario o generar otros horarios");
    	botonGraficas = new JButton("Rúbrica");
    	botonTablaPosiciones = new JButton("Mejores calificaciones");
    	botonPublicar = new JButton("Publicar");
    	
    	etiqResultados1 = new JLabel();
    	etiqResultados2 = new JLabel();
    	etiqPromedio = new JLabel();
    	etiqRecord = new JLabel("RÉCORD!");
    	etiqContador = new JLabel();
    	etiqConexion = new JLabel();
    	
    	etiqRecord.setVisible(false);
    	
    	this.add(botonGenerar);
    	this.add(botonGraficas);
    	this.add(botonTablaPosiciones);
    	this.add(botonPublicar);
    	this.add(etiqResultados1);
    	this.add(etiqResultados2);
    	this.add(etiqPromedio);
    	this.add(etiqRecord);
    	this.add(etiqContador);
    	this.add(etiqConexion);
    	
    	this.setLayout(null);
    	this.setSize(430, 590);
    	this.setResizable(false);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	centrarVentanas(this);
        
        botonGenerar.setBounds(24, 15, 273, 25);
        botonGraficas.setBounds(312, 15, 79, 25);
        botonTablaPosiciones.setBounds(25, 507, 164, 25);
        botonPublicar.setBounds(205, 507, 85, 25);
        etiqResultados1.setBounds(118, 427, 250, 15);
        etiqResultados2.setBounds(85, 445, 300, 15);
        etiqPromedio.setBounds(150, 462, 130, 25);
        etiqRecord.setBounds(315, 462, 150, 25);
        etiqContador.setBounds(100, 462, 150, 25);
        etiqConexion.setBounds(305, 507, 150, 25);
        
        botonGenerar.addActionListener(this);
        botonGraficas.addActionListener(this);
        botonTablaPosiciones.addActionListener(this);
        botonPublicar.addActionListener(this);
        
		
    	// URL de la imagen
        String imageUrl = rutaBackend + "/icono";
        
		try {
			URL url;
			url = new URL(imageUrl);
            icon = new ImageIcon(url);
            
		} catch (MalformedURLException e) {}

        this.setIconImage(icon.getImage());
    	

    }

    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == botonGenerar) {
    		
    		// Comentar los siguientes líneas
    		// Aumenta la velocidad para continuar con el siguiente horario
    		/*
    		if(!ConexiónServidor()) {
        		return ;
        	}*/
    		SwingUtilities.invokeLater(() -> {
	    		if(listaHorarios.size() == 11 ) {
	    			System.out.println(true);
	    			nuevosHorarios(false); 
	    		}
    		});
    		SwingUtilities.invokeLater(() -> {
    			cargarHorario();
    		});
        }
    	else if (e.getSource() == botonGraficas) ventanaGraficas(this);
        
    	else if(e.getSource() == botonPublicar) ventanaPublicar(this);
        
    	else if(e.getSource() == botonTablaPosiciones) {
    		ventanaPosiciones(this);
    	}
        
    }
    private void ventanaGraficas(JFrame ventanaPrincipal) {
        // Deshabilitar la ventana prinical
    	ventanaPrincipal.setEnabled(false);
    	

        // Crear la tercera ventana
        JFrame ventanaGraficas = new JFrame("Rúbrica");
        ventanaGraficas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaGraficas.setSize(900, 740);
        ventanaGraficas.setResizable(false);
        ventanaGraficas.setLayout(new FlowLayout());
        ventanaGraficas.setIconImage(icon.getImage());
        centrarVentanas(ventanaGraficas);

        ventanaGraficas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            	ventanaPrincipal.setEnabled(true);
            	ventanaPrincipal.toFront();
            }
        });
        

        SwingUtilities.invokeLater(() -> {
        	
        	// URL de la imagen
            String imageUrl = rutaBackend + "/rubrica";
            URL url;
			try {
				url = new URL(imageUrl);
	            ImageIcon imageIcon = new ImageIcon(url);
	            JLabel label = new JLabel(imageIcon);
	            
		        ventanaGraficas.add(label);
			} catch (MalformedURLException e) {}

	        ventanaGraficas.setVisible(true);
	        ventanaGraficas.toFront();
        });
        
    }
    
    
    public void paint(Graphics g) {
    	
    	super.paint(g);
    	if(!ConexiónServidor()) return;
    	if (horario == null) return;
    	
    	
		etiqResultados1.setText(horario.getMateriasCargadasYHorasLibres());
		etiqResultados2.setText(horario.getHoraDeEntradaYPromedio());
		etiqPromedio.setText("Calificación: " + horario.getCalificacion()+"/100");
		horario.graficarHorario(g);
		horario.graficarCalificacionSiguientesHorarios(g, listaHorarios, contador);
		
    }
    
    private void cargarHorario() {
		horario = new Horario(listaHorarios.get(0));
		verificaMejorCalificacionActual();
		actualizaContador();
		System.out.println(listaHorarios.size());
		System.out.println(horario);
		repaint(); 
		System.out.println("posicionHorario: "+posicionHorario);
		listaHorarios.remove(0);
		posicionHorario++;
    }
    
    private void verificaMejorCalificacionActual() {
    	etiqRecord.setVisible(false);
        if(mejorCalificacionActual == 0) {
        	mejorCalificacionActual = horario.getCalificacion();
        }
    	// Verifica si el horario actual tiene mejor calificacion que el mejor horario
        else if(horario.getCalificacion() > mejorCalificacionActual) {
        	mejorCalificacionActual = horario.getCalificacion();
        	etiqRecord.setVisible(true);
        }
    }
    
    private void actualizaContador() {
    	contador++;
    	etiqContador.setText("# " + contador);
    }
    
    
    private void nuevosHorarios(boolean inicio) {
    	if(inicio) {
			listaHorarios.addAll(parseHorarios(sendGetRequest(rutaBackend + "/nuevosHorarios")));
			System.out.println();
			System.out.println(listaHorarios);
    	}else {
	        SwingUtilities.invokeLater(() -> {
	        	System.out.println(sendGetRequest(rutaBackend + "/eliminarHorarios"));
	        	posicionHorario-=10;
				listaHorarios.addAll(parseHorarios(sendGetRequest(rutaBackend + "/nuevosHorarios")));
				System.out.println();
				System.out.println(listaHorarios);
	        });
    	}
    }
    
    
    private void ventanaPublicar(JFrame parentWindow) {

        if (!ConexiónServidor()) {
            return;
        }

        // Deshabilitar la ventana principal
        parentWindow.setEnabled(false);

        // Crear la segunda ventana
        JFrame ventanaPublicar = new JFrame("Recientemente generaste un buen horario");
        ventanaPublicar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaPublicar.setSize(410, 270);
        ventanaPublicar.setResizable(false);
        ventanaPublicar.setLayout(null); // Mantén el diseño nulo
        ventanaPublicar.setIconImage(icon.getImage());
        centrarVentanas(ventanaPublicar);
        
        SwingUtilities.invokeLater(() -> {
        	
        	String URL = rutaBackend + "/mejorHorario?posicion=" + posicionHorario;
        	
        	mejorHorario = new Horario(parseHorario(sendGetRequest(URL)));
        	
        	System.out.println("mejorHorario: " + mejorHorario);
        	
	        ventanaPublicar.addWindowListener(new java.awt.event.WindowAdapter() {
	            @Override
	            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
	                parentWindow.setEnabled(true);
	                parentWindow.setVisible(true);
	            }
	        });
	
	        DrawPanel drawPanel = new DrawPanel();
	        JTextArea  etiq = new JTextArea ();
	        JLabel etiqValidacion = new JLabel();
	        JButton botonPublicar = new JButton("Publicar");
	        JTextField campoFecha = new JTextField(mejorHorario.getFecha());
	        JTextField campoApodo = new JTextField("Apodo");
	
	        drawPanel.setBounds(0, 0, 200, 220); 
	        etiq.setBounds(220, 25, 150, 140);
	        botonPublicar.setBounds(268, 185, 90, 25);
	        campoFecha.setBounds(220, 110, 90, 25);
	        campoApodo.setBounds(220, 139, 90, 25);
	        etiqValidacion.setBounds(220, 160, 190, 25);
	        
	        campoFecha.setEditable(false); // Desactivar edición
	        
	        int horarioId = mejorHorario.getId();
	        double calificacion = mejorHorario.getCalificacion();
	
	        String texto = String.format(
	        	    "Mejor resultado:\n" +
	        	    "Horario #%d\n" +
	        	    "Calificación:\n" +
	        	    "%.11f/100\n", // Cambiado a %.11f para mostrar solo 11 decimales
	        	    ++horarioId, calificacion
	        	);
	        
	        etiq.setEditable(false); // Hacer que sea de solo lectura
	        etiq.setOpaque(false);   // Quitar el fondo visible
	        etiq.setBorder(null);    // Eliminar el borde
	        etiq.setForeground(Color.BLACK); // Cambiar el color del texto
	        etiq.setFont(new Font("Calibri", Font.PLAIN, 16)); // Cambiar la fuente y tamaño
	        etiq.setText(texto);
	        
	        etiqValidacion.setForeground(new Color(242, 69, 47));
	        
	        // Agregar FocusListener para manejar el texto por defecto
	        campoApodo.addFocusListener((FocusListener) new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	                if (campoApodo.getText().equals("Apodo")) {
	                	campoApodo.setText(""); // Borra el texto por defecto
	                }
	            }
	
	            @Override
	            public void focusLost(FocusEvent e) {
	                if (campoApodo.getText().isEmpty()) {
	                	campoApodo.setText("Apodo"); // Restaura el texto por defecto si está vacío
	                }
	            }
	        });
	        
	        ventanaPublicar.add(campoFecha);
	        ventanaPublicar.add(campoApodo);
	    	ventanaPublicar.add(etiq);
	        ventanaPublicar.add(etiqValidacion);
	        ventanaPublicar.add(drawPanel); 
	        ventanaPublicar.add(botonPublicar);
	        
	        
	
	        botonPublicar.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	
	            	parentWindow.setVisible(true);
	            	String apodo = campoApodo.getText().toUpperCase();
	            	
	            	if(apodo.length() > 8 ) {
	            		etiqValidacion.setText("Máximo 8 caracteres");
	            		return;
	            	}
	            	if(!apodo.matches("[a-zA-Z]+")) {
	            		etiqValidacion.setText("Debe contener solo letras");
	            		return;
	            	}
	            	if(apodo.equals("APODO") || apodo.equals("")) {
	            		apodo = "ANONIMO";
	            	}
	            	
	            	String mensaje = "",
	            		URL = rutaBackend + "/guardarHorario?apodo=" + apodo;
	            	
	            	mensaje = sendGetRequest(URL);
	            	
	                ventanaPublicar2(mensaje, ventanaPublicar, parentWindow);
	                
	            }
	        });
	
	        ventanaPublicar.setVisible(true);
        
    	});
    }

    // Clase interna para el panel de dibujo
    private class DrawPanel extends JPanel {
    	
    	private int[] exigencias;
    	private int calificacion;

        private static final long serialVersionUID = 1L;
        
        public DrawPanel() {
    		this.exigencias = mejorHorario.getHorario();
    		this.calificacion = (int)mejorHorario.getCalificacion();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (exigencias == null) return;

            Font font = new Font("Arial", Font.BOLD, 11);
    	    g.setFont(font);

    	    
    		g.setColor(new Color(248,248,248));
    		g.fillRect(30, 35, 160, 173);
    		
    		g.setColor(new Color(34,42,53));
    		g.fillRect(80, 25, 110, 13); // 13 x 14 = 182
    		
    		for(int i=0; i<exigencias.length; i++) {
    			if (exigencias[i] != -1) {
    				pintarMateria(exigencias[i], i, g);
    			}
    		}
    		
    		pintarFondoHora(calificacion, g);
    		for(int i=0; i<exigencias.length; i++) {
    			g.drawString((i+7)+":00", 41, 49+(i*13));
    		}
    		
    		g.setColor(new Color(238,238,238));
    		g.drawRect(30, 24, 50, 14);
    		g.drawRect(80, 24, 110, 14);
    		
    		
    		for(int i=0; i<13; i++) {
    			g.setColor(new Color(238,238,238));
    			g.drawRect(30, (38+(i*13)), 160, 13);
    		}
    		
    		g.drawLine(80, 35, 80, 206);
        }
        
    }
    
	private void pintarMateria(int exigencia, int i, Graphics g) {
		g.setColor(interpolarColor(mapExigencia( exigencia ) ));
		g.fillRect(60, (39+(i*13)), 130, 13);
		g.setColor(Color.WHITE);
	    g.drawString("Exigencia "+exigencia+" %", 98, 49+(i*13));
	}
	
	private void pintarFondoHora(int calificacion, Graphics g) {
		g.setColor(interpolarColor( mapNumber(calificacion)) );
		g.fillRect(31, 24, 49, 184);
		g.setColor(Color.WHITE);
		
	}
	
	public int mapNumber(int calif) {
		int num = 100 - calif;
        return 
    		num > 5 ?(
    			num + 20
    		):(
    			num * 5
    		)
    	;
    }
	
	private int mapExigencia(int exigencia) {
		return exigencia > 50 ?( 
				(exigencia - 50) * 2
			):( 
				100 - (exigencia * 2)
			);
	}
	
	public Color interpolarColor(int value) {
        // Definir colores con sus valores asociados
        int[][] colors = {
            {56, 86, 36, 0},   // Verde
            {255, 192, 0, 50}, // Amarillo
            {196, 89, 17, 100} // Naranja
        };

        // Identificar el segmento adecuado
        int[] startColor = null;
        int[] endColor = null;
        
        if (value > 100) value = 100;

        for (int i = 0; i < colors.length - 1; i++) {
            if (colors[i][3] <= value && value <= colors[i + 1][3]) {
                startColor = colors[i];
                endColor = colors[i + 1];
                break;
            }
        }

        if (startColor == null || endColor == null) {
            throw new IllegalArgumentException("El valor está fuera del rango permitido.");
        }

        // Calcular la proporción de intensidad entre los dos colores
        double range = endColor[3] - startColor[3];
        double factor = (value - startColor[3]) / range;

        // Interpolar cada canal de color (R, G, B)
        int r = (int) (startColor[0] + factor * (endColor[0] - startColor[0]));
        int g = (int) (startColor[1] + factor * (endColor[1] - startColor[1]));
        int b = (int) (startColor[2] + factor * (endColor[2] - startColor[2]));

        return new Color(r,g,b);
    }
    

    private void ventanaPosiciones(JFrame parentWindow) {
    	
    	if(!ConexiónServidor()) {
    		return ;
    	}
    	
        // Deshabilitar la ventana anterior
        parentWindow.setEnabled(false);

        // Crear la tercera ventana
        JFrame ventanaTablaPosiciones = new JFrame();
        ventanaTablaPosiciones.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaTablaPosiciones.setSize(530, 300);
        ventanaTablaPosiciones.setResizable(false);
        ventanaTablaPosiciones.setLayout(new BorderLayout());
        ventanaTablaPosiciones.setIconImage(icon.getImage());
        centrarVentanas(ventanaTablaPosiciones);

        // Rehabilitar la ventana anterior al cerrar la tercera ventana
        ventanaTablaPosiciones.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                parentWindow.setEnabled(true);
                parentWindow.setVisible(true);
            }
        });

        ventanaTablaPosiciones.setTitle("Cargando datos...");
        ventanaTablaPosiciones.setVisible(true);
        // Crear la tabla en invokeLater()
        SwingUtilities.invokeLater(() -> {
     
	        String[] columnNames = {"Top", "Apodo", "Fecha", "Exigencias", "Calificación"};
	        String[][] data = fetchMejoresHorarios(rutaBackend + "/registros");
	        
	        JTable table = new JTable(data, columnNames);
	        table.setEnabled(false);
	        DefaultTableCellRenderer centrador = new DefaultTableCellRenderer();
	        centrador.setHorizontalAlignment(SwingConstants.CENTER);
	        TableColumnModel columnModel = table.getColumnModel();
	        columnModel.getColumn(0).setPreferredWidth(10);
	        columnModel.getColumn(0).setCellRenderer(centrador);
	        columnModel.getColumn(1).setPreferredWidth(50);
	        columnModel.getColumn(1).setCellRenderer(centrador);
	        columnModel.getColumn(2).setPreferredWidth(45);
	        columnModel.getColumn(2).setCellRenderer(centrador);
	        columnModel.getColumn(3).setPreferredWidth(125);
	        columnModel.getColumn(3).setCellRenderer(centrador);
	        columnModel.getColumn(4).setPreferredWidth(95);
	        columnModel.getColumn(4).setCellRenderer(centrador);
	        
	        JScrollPane scrollPane = new JScrollPane(table);
	        ventanaTablaPosiciones.add(scrollPane, BorderLayout.CENTER);
	        ventanaTablaPosiciones.setTitle("Tabla de posiciones mejores horarios");
	        ventanaTablaPosiciones.setVisible(true);
	        
	        System.out.println(sendGetRequest(rutaBackend + "/mejorHorario?posicion=" + posicionHorario ));
        });
    }

    private void ventanaPublicar2(String mensaje, JFrame ventanaAnterior, JFrame ventanaPrincipal) {
        // Deshabilitar la ventana anterior
    	ventanaAnterior.setEnabled(false);
    	ventanaPrincipal.setEnabled(true);
    	

        // Crear la tercera ventana
        JFrame ventanaPublicar2 = new JFrame("Publicar");
        ventanaPublicar2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaPublicar2.setSize(350, 120);
        ventanaPublicar2.setResizable(false);
        ventanaPublicar2.setLayout(new FlowLayout());
        ventanaPublicar2.setIconImage(icon.getImage());
        centrarVentanas(ventanaPublicar2);
        

        ventanaPublicar2.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                ventanaAnterior.dispose();
            }
        });

        JLabel label = new JLabel(mensaje);
        ventanaPublicar2.add(label);
        ventanaPublicar2.setVisible(true);
        ventanaPublicar2.toFront();
        
    }

    private void centrarVentanas(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private String[][] fetchMejoresHorarios(String endpointUrl) {
        try {
            // Realizar solicitud HTTP GET
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Leer la respuesta
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // Procesar respuesta JSON
            JSONArray jsonArray = new JSONArray(response.toString());
            String[][] data = new String[jsonArray.length()][5]; // Ajustar el tamaño al número de columnas

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                data[i][0] = String.valueOf(obj.getInt("top"));            // Top
                data[i][1] = obj.getString("apodo");                      // Apodo
                data[i][2] = obj.getString("fecha");                      // Fecha
                data[i][3] = obj.getString("exigencias");                 // Exigencias
                data[i][4] = obj.getString("calificacion");// Calificación
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            // Devolver datos vacíos en caso de error
            return new String[0][0];
        }
    }

    private boolean ConexiónServidor() {
        HttpURLConnection connection = null;
        BufferedReader br = null;
        try {
            // Realizar solicitud HTTP GET
            URL url = new URL(rutaBackend + "/echo");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Verificar el código de respuesta HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false; // Si no es 200, no hay conexión válida
            }

            // Leer la respuesta del servidor
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            // Comparar la respuesta con el texto esperado
            return response.toString().trim().equals("Hay conexión con el servidor");
        } catch (Exception e) {
            e.printStackTrace();
            return false; // En caso de error, no hay conexión
        } finally {
            // Cerrar recursos
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private String sendGetRequest(String urlString) {
        StringBuilder response = new StringBuilder();
        try {
            // Configuración de la conexión HTTP
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Habilitar el envío de cookies (usando CookieManager)
            connection.setRequestProperty("Cookie", getCookies());

            // Lee la respuesta del servidor
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();
                // Al recibir la respuesta, almacenamos las cookies enviadas
                storeCookies(connection);
            } else {
                response.append("Error: ").append(responseCode).append(" - ").append(connection.getResponseMessage());
            }
        } catch (Exception ex) {
            response.append("Excepcion: ").append(ex.getMessage());
        }
        return response.toString();
    }
    
    private List<Horario> parseHorarios(String jsonResponse) {
        List<Horario> horarios = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonHorario = jsonArray.getJSONObject(i);
            
            int id = jsonHorario.getInt("id"),
        		materiasCargadas = jsonHorario.getInt("materiasCargadas"),
        		horasLibres = jsonHorario.getInt("horasLibres"),
        		horaEntrada = jsonHorario.getInt("horaDeEntrada");
            double calificacion = jsonHorario.getDouble("calificacion");
            
         // Obtener el array "horario" del JSON y convertirlo a int[]
            JSONArray horarioExigenciasArray = jsonHorario.getJSONArray("horario");
            int[] horarioExig = new int[horarioExigenciasArray.length()];
            for (int j = 0; j < horarioExigenciasArray.length(); j++) {
            	horarioExig[j] = horarioExigenciasArray.getInt(j);
            }
            
            Horario horario = new Horario(id, materiasCargadas, horasLibres, horaEntrada, horarioExig, calificacion);


            JSONArray horarioArray = jsonHorario.getJSONArray("horario");
            int[] horarioValues = new int[horarioArray.length()];
            for (int j = 0; j < horarioArray.length(); j++) {
                horarioValues[j] = horarioArray.getInt(j);
            }
            horario.setHorario(horarioValues);

            horarios.add(horario);
        }
        return horarios;
    }
    
    // Método para analizar el JSON y devolver un solo horario
    public Horario parseHorario(String jsonResponse) {
        try {
            // Convertir la respuesta en un objeto JSON
            JSONObject horarioObj = new JSONObject(jsonResponse);

            int id = horarioObj.getInt("id"),
            		materiasCargadas = horarioObj.getInt("materiasCargadas"),
            		horasLibres = horarioObj.getInt("horasLibres"),
            		horaEntrada = horarioObj.getInt("horaDeEntrada");
            double calificacion = horarioObj.getDouble("calificacion");
            
         // Obtener el array "horario" del JSON y convertirlo a int[]
            JSONArray horarioArray = horarioObj.getJSONArray("horario");
            int[] horario = new int[horarioArray.length()];
            for (int i = 0; i < horarioArray.length(); i++) {
                horario[i] = horarioArray.getInt(i);
            }

            // Retornar el horario
            return new Horario(id, materiasCargadas, horasLibres, horaEntrada, horario, calificacion);
        } catch (Exception e) {
            System.err.println("Error al analizar el JSON: " + e.getMessage());
        }

        // Retornar null si no se pudo obtener un horario válido
        return null;
    }

    // Obtener las cookies de la sesión actual
    private String getCookies() {
        StringBuilder cookies = new StringBuilder();
        for (java.net.HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            cookies.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        return cookies.toString();
    }

    // Almacenar las cookies recibidas en la respuesta
    private void storeCookies(HttpURLConnection connection) {
        // Obtener las cookies del header "Set-Cookie"
        String cookiesHeader = connection.getHeaderField("Set-Cookie");
        
        // Verificar si hay cookies antes de intentar almacenarlas
        if (cookiesHeader != null && !cookiesHeader.trim().isEmpty()) {
            for (String cookie : cookiesHeader.split(";")) {
                java.net.HttpCookie httpCookie = java.net.HttpCookie.parse(cookie).get(0);
                cookieManager.getCookieStore().add(null, httpCookie);
            }
        } else {
            System.out.println("No se recibieron cookies en la respuesta.");
        }
    }
    
}


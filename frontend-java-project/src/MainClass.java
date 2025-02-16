

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
    
    private JButton generateButton;
    private JButton chartsButton;
    private JButton publishButton;
    private JButton leaderboardButton;
    
    private JLabel resultLabel1;
    private JLabel resultLabel2;
    private JLabel averageLabel;
    private JLabel recordLabel;
    private JLabel counterLabel;
    private JLabel connectionLabel;
    
    private Schedule schedule;
    private Schedule bestSchedule;
    
    private StyleButtons buttonStyles;
    
    private double currentBestRating;
    
    private int schedulePosition;
    private int counter;
    
    private CookieManager cookieManager;
    
    private ImageIcon icon;
    
    private String backendUrl;
    
    private ScheduledExecutorService scheduler;
    
    private List<Schedule> scheduleList;

    
    public static void main(String[] args) {
        new MainClass();
    }
    
    public MainClass() {
        super("Greenest Schedule");
        this.backendUrl = "https://lanq.com.mx/spring/horario-api";
        this.scheduleList = new ArrayList<>();
        this.cookieManager = new CookieManager();
        this.schedulePosition = 0;
        this.currentBestRating = 0;
        this.counter = 0;
        this.buttonStyles = new StyleButtons();
        
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(cookieManager);
        
        createMainWindow();
        startVerification();
        initializeScheduleList();
        loadScheduleAndUpdateScreen();
        
        this.setVisible(true);
    }
    
    private void initializeScheduleList() {
        newSchedules(true);  // + 10 
        newSchedules(true);  // + 20 added schedules
    }

    public void startVerification() {
        scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            if (checkServerConnection()) {
//  --- UI text in spanish ---
//                connectionLabel.setText("    Online ✓");
                connectionLabel.setText("    En línea ✓");
            } else {
//                connectionLabel.setText("Offline ❌");
                connectionLabel.setText("Sin conexión ❌");
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }
    
    public void createMainWindow() {
        generateButton = new JButton();
        chartsButton = new JButton();
        leaderboardButton = new JButton();
        publishButton = new JButton();
        
        resultLabel1 = new JLabel();
        resultLabel2 = new JLabel();
        averageLabel = new JLabel();
        recordLabel = new JLabel("RECORD!");
        counterLabel = new JLabel();
        connectionLabel = new JLabel();
        
        recordLabel.setVisible(false);
        
/*
        generateButton.setUI(buttonStyles.getBasicButtonUI("Next schedule or generate others"));
        chartsButton.setUI(buttonStyles.getBasicButtonUI("Rubric"));
        leaderboardButton.setUI(buttonStyles.getBasicButtonUI("Top scores"));
        publishButton.setUI(buttonStyles.getBasicButtonUI("Publish"));
*/
        generateButton.setUI(buttonStyles.getBasicButtonUI("Siguiente horario o generar otros horarios"));
        chartsButton.setUI(buttonStyles.getBasicButtonUI("Rúbrica"));
        leaderboardButton.setUI(buttonStyles.getBasicButtonUI("Mejores calificaciones"));
        publishButton.setUI(buttonStyles.getBasicButtonUI("Publicar"));
        
        this.add(generateButton);
        this.add(chartsButton);
        this.add(leaderboardButton);
        this.add(publishButton);
        this.add(resultLabel1);
        this.add(resultLabel2);
        this.add(averageLabel);
        this.add(recordLabel);
        this.add(counterLabel);
        this.add(connectionLabel);
        
        this.setLayout(null);
        this.setSize(430, 590);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerWindow(this);
        
        generateButton.setBounds(24, 15, 273, 25);
        chartsButton.setBounds(312, 15, 79, 25);
        leaderboardButton.setBounds(25, 507, 164, 25);
        publishButton.setBounds(205, 507, 85, 25);
        resultLabel1.setBounds(118, 427, 250, 15);
        resultLabel2.setBounds(85, 445, 300, 15);
        averageLabel.setBounds(150, 462, 130, 25);
        recordLabel.setBounds(315, 462, 150, 25);
        counterLabel.setBounds(100, 462, 150, 25);
        connectionLabel.setBounds(305, 507, 150, 25);
        
        generateButton.addActionListener(this);
        chartsButton.addActionListener(this);
        leaderboardButton.addActionListener(this);
        publishButton.addActionListener(this);
        
        String imageUrl = backendUrl + "/icono";
        try {
            URL url = new URL(imageUrl);
            icon = new ImageIcon(url);
        } catch (MalformedURLException e) {}

        this.setIconImage(icon.getImage());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            loadScheduleAndUpdateScreen();
            
            SwingUtilities.invokeLater(() -> {
                if(scheduleList.size() == 11 ) {
                    newSchedules(false); 
                }
            });
        }
        else if (e.getSource() == chartsButton) showChartsWindow(this);
        else if (e.getSource() == publishButton) showPublishWindow(this);
        else if (e.getSource() == leaderboardButton) showLeaderboardWindow(this);
    }

    private void showChartsWindow(JFrame mainWindow) {
        mainWindow.setEnabled(false);
//        JFrame chartsWindow = new JFrame("Rubric");
        JFrame chartsWindow = new JFrame("Rúbrica");
        chartsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartsWindow.setSize(900, 740);
        chartsWindow.setResizable(false);
        chartsWindow.setLayout(new FlowLayout());
        chartsWindow.setIconImage(icon.getImage());
        centerWindow(chartsWindow);

        chartsWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                mainWindow.setEnabled(true);
                mainWindow.toFront();
            }
        });

        SwingUtilities.invokeLater(() -> {
            String imageUrl = backendUrl + "/rubrica";
            try {
                URL url = new URL(imageUrl);
                ImageIcon imageIcon = new ImageIcon(url);
                JLabel label = new JLabel(imageIcon);
                chartsWindow.add(label);
            } catch (MalformedURLException e) {}
            chartsWindow.setVisible(true);
            chartsWindow.toFront();
        });
    }
    
    
    public void paint(Graphics g) {
        super.paint(g);
        if (schedule == null) return;
        
        resultLabel1.setText(schedule.getLoadedSubjectsAndFreeHours());
        resultLabel2.setText(schedule.getEntryTimeAndAverage());
//        averageLabel.setText("Rating: " + schedule.getRating()+"/100");
        averageLabel.setText("Calificación: " + schedule.getRating()+"/100");
        schedule.drawSchedule(g);
        schedule.drawFollowingSchedulesRatings(g, scheduleList, counter);
    }
    
    private void loadScheduleAndUpdateScreen() {
        schedule = new Schedule(scheduleList.get(0));
        checkCurrentBestRating();
        updateCounter();
        scheduleList.remove(0);
        schedulePosition++;
        repaint();
    }
    
    private void checkCurrentBestRating() {
        recordLabel.setVisible(false);
        if(currentBestRating == 0) {
            currentBestRating = schedule.getRating();
        }
        else if(schedule.getRating() > currentBestRating) {
            currentBestRating = schedule.getRating();
            recordLabel.setVisible(true);
        }
    }
    
    private void updateCounter() {
        counter++;
        counterLabel.setText("# " + counter);
    }
    
    
    private void newSchedules(boolean initial) {
        if(initial) {
            scheduleList.addAll(parseSchedules(sendGetRequest(backendUrl + "/nuevosHorarios")));
        } else {
            SwingUtilities.invokeLater(() -> {
                sendGetRequest(backendUrl + "/eliminarHorarios");
                schedulePosition-=10;
                scheduleList.addAll(parseSchedules(sendGetRequest(backendUrl + "/nuevosHorarios")));
            });
        }
    }
    
    
    private void showPublishWindow(JFrame parentWindow) {
        if (!checkServerConnection()) return;

        parentWindow.setEnabled(false);
        //JFrame publishWindow = new JFrame("Recently generated a good schedule");
        JFrame publishWindow = new JFrame("Recientemente generaste un buen horario");
        publishWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        publishWindow.setSize(410, 270);
        publishWindow.setResizable(false);
        publishWindow.setLayout(null);
        publishWindow.setIconImage(icon.getImage());
        centerWindow(publishWindow);
        
        SwingUtilities.invokeLater(() -> {
            String URL = backendUrl + "/mejorHorario?posicion=" + schedulePosition;
            bestSchedule = new Schedule(parseSchedule(sendGetRequest(URL)));
            
            publishWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    parentWindow.setEnabled(true);
	                parentWindow.toFront();
                }
            });

            DrawPanel drawPanel = new DrawPanel();
            JTextArea infoLabel = new JTextArea();
            JLabel validationLabel = new JLabel();
            JButton publishBtn = new JButton();
            JTextField dateField = new JTextField(bestSchedule.getDate());
//            JTextField nicknameField = new JTextField("Nickname");
            JTextField nicknameField = new JTextField("Apodo");

            drawPanel.setBounds(0, 0, 200, 220); 
            infoLabel.setBounds(220, 25, 150, 140);
            publishBtn.setBounds(268, 185, 90, 25);
            dateField.setBounds(220, 110, 90, 25);
            nicknameField.setBounds(220, 139, 90, 25);
            validationLabel.setBounds(220, 160, 190, 25);
            
            dateField.setEditable(false);
            
            int scheduleId = bestSchedule.getId();
            double rating = bestSchedule.getRating();
/*
            String text = String.format(
                "Best result:\n" +
                "Schedule #%d\n" +
                "Rating:\n" +
                "%.11f/100\n",
                ++scheduleId, rating
            );
*/            
            String text = String.format(
	        	    "Mejor resultado:\n" +
	        	    "Horario #%d\n" +
	        	    "Calificación:\n" +
	        	    "%.11f/100\n\n",
                    ++scheduleId, rating
                );
            
            infoLabel.setEditable(false);
            infoLabel.setOpaque(false);
            infoLabel.setBorder(null);
            infoLabel.setForeground(Color.BLACK);
            infoLabel.setFont(new Font("Calibri", Font.PLAIN, 16));
            infoLabel.setText(text);
            
            validationLabel.setForeground(new Color(242, 69, 47));
            
            nicknameField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
//                    if (nicknameField.getText().equals("Nickname")) {
                    if (nicknameField.getText().equals("Apodo")) {
                        nicknameField.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (nicknameField.getText().isEmpty()) {
//                        nicknameField.setText("Nickname");
                        nicknameField.setText("Apodo");
                    }
                }
            });
            
//            publishBtn.setUI(buttonStyles.getBasicButtonUI("Publish"));
            publishBtn.setUI(buttonStyles.getBasicButtonUI("Publicar"));
            
            publishWindow.add(dateField);
            publishWindow.add(nicknameField);
            publishWindow.add(infoLabel);
            publishWindow.add(validationLabel);
            publishWindow.add(drawPanel); 
            publishWindow.add(publishBtn);
            
            publishBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String nickname = nicknameField.getText().toUpperCase();
                    
                    if(nickname.length() > 8 ) {
//                        validationLabel.setText("Max 8 characters");
                        validationLabel.setText("Máximo 8 caracteres");
                        return;
                    }
                    if(!nickname.matches("[a-zA-Z]+")) {
//                        validationLabel.setText("Letters only");
                        validationLabel.setText("Debe contener solo letras");
                        return;
                    }
//                    if(nickname.equals("NICKNAME") || nickname.isEmpty()) {
//                        nickname = "ANONYMOUS";
                    if(nickname.equals("APODO") || nickname.isEmpty()) {
                        nickname = "ANONIMO";
                    }
                    
                    String message = sendGetRequest(backendUrl + "/guardarHorario?apodo=" + nickname);
                    showPublishConfirmation(message, publishWindow, parentWindow);
                }
            });

            publishWindow.setVisible(true);
        });
    }

    private class DrawPanel extends JPanel {
        private int[] demands;
        private int rating;
        private static final long serialVersionUID = 1L;
        
        public DrawPanel() {
            this.demands = bestSchedule.getSchedule();
            this.rating = (int)bestSchedule.getRating();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (demands == null) return;

            Font font = new Font("Arial", Font.BOLD, 11);
            g.setFont(font);

            g.setColor(new Color(248,248,248));
            g.fillRect(30, 35, 160, 173);
            
            g.setColor(new Color(34,42,53));
            g.fillRect(80, 25, 110, 13);
            
            for(int i=0; i<demands.length; i++) {
                if (demands[i] != -1) {
                    drawSubject(demands[i], i, g);
                }
            }
            
            drawTimeBackground(rating, g);
            for(int i=0; i<demands.length; i++) {
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
    
    private void drawSubject(int demand, int i, Graphics g) {
        g.setColor(interpolateColor(mapDemand(demand)));
        g.fillRect(60, (39+(i*13)), 130, 13);
        g.setColor(Color.WHITE);
//        g.drawString("Demand "+demand+" %", 98, 49+(i*13));
      g.drawString("Exigencia "+demand+" %", 98, 49+(i*13));
    }
    
    private void drawTimeBackground(int rating, Graphics g) {
        g.setColor(interpolateColor(mapNumber(rating)));
        g.fillRect(31, 24, 49, 184);
        g.setColor(Color.WHITE);
    }
    
    public int mapNumber(int rating) {
        return 100 - rating > 5 ? (100 - rating + 20) : ((100 - rating) * 5);
    }
    
    private int mapDemand(int demand) {
        return demand > 50 ? ((demand - 50) * 2) : (100 - (demand * 2));
    }
    
    public Color interpolateColor(int value) {
        int[][] colors = {
            {56, 86, 36, 0},
            {255, 192, 0, 50},
            {196, 89, 17, 100}
        };

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

        double range = endColor[3] - startColor[3];
        double factor = (value - startColor[3]) / range;

        int r = (int) (startColor[0] + factor * (endColor[0] - startColor[0]));
        int g = (int) (startColor[1] + factor * (endColor[1] - startColor[1]));
        int b = (int) (startColor[2] + factor * (endColor[2] - startColor[2]));

        return new Color(r,g,b);
    }
    

    private void showLeaderboardWindow(JFrame parentWindow) {
        if(!checkServerConnection()) return;

        parentWindow.setEnabled(false);
        JFrame leaderboardWindow = new JFrame();
        leaderboardWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardWindow.setSize(530, 300);
        leaderboardWindow.setResizable(false);
        leaderboardWindow.setLayout(new BorderLayout());
        leaderboardWindow.setIconImage(icon.getImage());
        centerWindow(leaderboardWindow);

        leaderboardWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                parentWindow.setEnabled(true);
                parentWindow.toFront();
            }
        });

//        leaderboardWindow.setTitle("Loading data...");
        leaderboardWindow.setTitle("Cargando datos...");
        leaderboardWindow.setVisible(true);
        
        SwingUtilities.invokeLater(() -> {
//            String[] columnNames = {"Top", "Nickname", "Date", "Demands", "Rating"};
            String[] columnNames = {"Top", "Apodo", "Fecha", "Exigencias", "Calificación"};
            String[][] data = fetchTopSchedules(backendUrl + "/registros");
            
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(10);
            columnModel.getColumn(0).setCellRenderer(centerRenderer);
            columnModel.getColumn(1).setPreferredWidth(50);
            columnModel.getColumn(1).setCellRenderer(centerRenderer);
            columnModel.getColumn(2).setPreferredWidth(45);
            columnModel.getColumn(2).setCellRenderer(centerRenderer);
            columnModel.getColumn(3).setPreferredWidth(125);
            columnModel.getColumn(3).setCellRenderer(centerRenderer);
            columnModel.getColumn(4).setPreferredWidth(95);
            columnModel.getColumn(4).setCellRenderer(centerRenderer);
            
            JScrollPane scrollPane = new JScrollPane(table);
            leaderboardWindow.add(scrollPane, BorderLayout.CENTER);
//            leaderboardWindow.setTitle("Top schedules leaderboard");
            leaderboardWindow.setTitle("Tabla de posiciones mejores horarios");
            leaderboardWindow.setVisible(true);
        });
    }

    private void showPublishConfirmation(String message, JFrame previousWindow, JFrame mainWindow) {
        previousWindow.setEnabled(false);
        mainWindow.setEnabled(true);

//        JFrame confirmationWindow = new JFrame("Publish");
        JFrame confirmationWindow = new JFrame("Publicar");
        confirmationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmationWindow.setSize(350, 120);
        confirmationWindow.setResizable(false);
        confirmationWindow.setLayout(new FlowLayout());
        confirmationWindow.setIconImage(icon.getImage());
        centerWindow(confirmationWindow);

        confirmationWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                previousWindow.dispose();
            }
        });

        JLabel label = new JLabel(message);
        confirmationWindow.add(label);
        confirmationWindow.setVisible(true);
        confirmationWindow.toFront();
    }

    private void centerWindow(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private String[][] fetchTopSchedules(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            String[][] data = new String[jsonArray.length()][5];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                data[i][0] = String.valueOf(obj.getInt("top"));
                data[i][1] = obj.getString("apodo");
                data[i][2] = obj.getString("fecha");
                data[i][3] = obj.getString("exigencias");
                data[i][4] = obj.getString("calificacion");
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0][0];
        }
    }

    private boolean checkServerConnection() {
        HttpURLConnection connection = null;
        BufferedReader br = null;
        try {
            URL url = new URL(backendUrl + "/echo");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            return response.toString().trim().equals("Hay conexión con el servidor");
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {}
            if (connection != null) connection.disconnect();
        }
    }
    
    private String sendGetRequest(String urlString) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Cookie", getCookies());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();
                storeCookies(connection);
            } else {
                response.append("Error: ").append(responseCode);
            }
        } catch (Exception ex) {
            response.append("Exception: ").append(ex.getMessage());
        }
        return response.toString();
    }
    
    private List<Schedule> parseSchedules(String jsonResponse) {
        List<Schedule> schedules = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonSchedule = jsonArray.getJSONObject(i);
            
            int id = jsonSchedule.getInt("id"),
                subjectsLoaded = jsonSchedule.getInt("materiasCargadas"),
                freeHours = jsonSchedule.getInt("horasLibres"),
                entryTime = jsonSchedule.getInt("horaDeEntrada");
            double rating = jsonSchedule.getDouble("calificacion");
            
            JSONArray scheduleArray = jsonSchedule.getJSONArray("horario");
            int[] scheduleDemands = new int[scheduleArray.length()];
            for (int j = 0; j < scheduleArray.length(); j++) {
                scheduleDemands[j] = scheduleArray.getInt(j);
            }
            
            Schedule schedule = new Schedule(id, subjectsLoaded, freeHours, entryTime, scheduleDemands, rating);
            schedules.add(schedule);
        }
        return schedules;
    }
    
    public Schedule parseSchedule(String jsonResponse) {
        try {
            JSONObject scheduleObj = new JSONObject(jsonResponse);

            int id = scheduleObj.getInt("id"),
                subjectsLoaded = scheduleObj.getInt("materiasCargadas"),
                freeHours = scheduleObj.getInt("horasLibres"),
                entryTime = scheduleObj.getInt("horaDeEntrada");
            double rating = scheduleObj.getDouble("calificacion");
            
            JSONArray scheduleArray = scheduleObj.getJSONArray("horario");
            int[] scheduleDemands = new int[scheduleArray.length()];
            for (int i = 0; i < scheduleArray.length(); i++) {
                scheduleDemands[i] = scheduleArray.getInt(i);
            }

            return new Schedule(id, subjectsLoaded, freeHours, entryTime, scheduleDemands, rating);
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    private String getCookies() {
        StringBuilder cookies = new StringBuilder();
        for (java.net.HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            cookies.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        return cookies.toString();
    }

    private void storeCookies(HttpURLConnection connection) {
        String cookiesHeader = connection.getHeaderField("Set-Cookie");
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader.split(";")) {
                java.net.HttpCookie httpCookie = java.net.HttpCookie.parse(cookie).get(0);
                cookieManager.getCookieStore().add(null, httpCookie);
            }
        }
    }
}


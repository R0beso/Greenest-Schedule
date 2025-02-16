import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Schedule {
    
    private int id;
    private int subjectsLoaded;
    private int freeHours;
    private int entryTime;
    private int[] schedule;
    private double rating;
    
    public Schedule(Schedule other) {
        this.id = other.id;
        this.subjectsLoaded = other.subjectsLoaded;
        this.freeHours = other.freeHours;
        this.entryTime = other.entryTime;
        this.rating = other.rating;
        this.schedule = other.schedule;
    }
    
    public Schedule(int id, int subjectsLoaded, int freeHours, int entryTime, int[] schedule, double rating) {
        this.id = id;
        this.subjectsLoaded = subjectsLoaded;
        this.freeHours = freeHours;
        this.entryTime = entryTime;
        this.rating = rating;
        this.schedule = schedule;
    }

    
    public void drawSchedule(Graphics g) {
        Font font = new Font("Serif", Font.PLAIN, 19);
        g.setFont(font);

        g.setColor(new Color(248, 248, 248));
        g.fillRect(30, 85, 370, 350);
        
        g.setColor(new Color(34, 42, 53));
        g.fillRect(30, 85, 370, 25);

        g.setColor(new Color(238, 238, 238));
        g.drawRect(30, 85, 70, 350);
        for (int i = 0; i < 5; i++) {
            g.drawRect(100, 85, 60 + (i * 60), 350);
        }
        
        for (int i = 0; i < schedule.length; i++) {
            g.setColor(new Color(34, 42, 53));
            if (schedule[i] != -1) {
                drawSubject(i, g);
            }
        }
        
        drawTimeBackground(g);
        for (int i = 0; i < schedule.length; i++) {
            g.drawString((i + 7) + ":00", 43, 130 + (i * 25));
        }
        
        for (int i = 0; i < 14; i++) {
            g.setColor(new Color(238, 238, 238));
            g.drawRect(30, (85 + (i * 25)), 370, 25);
        }

        g.drawLine(100, 85, 100, 435);
    }
    
    private void drawSubject(int index, Graphics g) {
        g.setColor(interpolateColor(mapDemand(schedule[index])));
        g.fillRect(30, (110 + (index * 25)), 370, 25);
        g.setColor(Color.WHITE);
        g.drawString("Exigencia del profesor " + schedule[index] + "%", 143, 130 + (index * 25));
    }
    
    private void drawTimeBackground(Graphics g) {
        g.setColor(interpolateColor(mapRatingValue((int) rating)));
        g.fillRect(30, 85, 70, 350);
        g.setColor(Color.WHITE);
    }
    
    public int mapRatingValue(int rating) {
        int num = 100 - rating;
        return num > 5 ? (num + 20) : (num * 5);
    }
    
    private int mapDemand(int demand) {
        return demand > 50 
            ? ((demand - 50) * 2) 
            : (100 - (demand * 2));
    }
    
    public Color interpolateColor(int value) {
        // Define colors with their associated values
        int[][] colors = {
            {56, 86, 36, 0},   // Green
            {255, 192, 0, 50}, // Yellow
            {196, 89, 17, 100} // Orange
        };

        // Identify the appropriate segment
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

        // Calculate the intensity ratio between the two colors
        double range = endColor[3] - startColor[3];
        double factor = (value - startColor[3]) / range;

        // Interpolate each color channel (R, G, B)
        int r = (int) (startColor[0] + factor * (endColor[0] - startColor[0]));
        int g = (int) (startColor[1] + factor * (endColor[1] - startColor[1]));
        int b = (int) (startColor[2] + factor * (endColor[2] - startColor[2]));

        return new Color(r, g, b);
    }

    public void drawFollowingSchedulesRatings(Graphics g, List<Schedule> scheduleList, int counter) {
        g.setColor(new Color(210, 210, 210));
        g.drawRect(30, 447, 370, 75);
        
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        String ratingStr;
        int index;
        
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 7; i++) {
                index = i + (j * 7);
                if (index < scheduleList.size()) {
                    g.setColor(new Color(225, 225, 225));
                    g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
                    ratingStr = ((int) Math.floor(scheduleList.get(index).getRating())) + "";
                    g.setColor(Integer.parseInt(ratingStr) < 95 ? new Color(155, 155, 155) : Color.BLACK );
                    g.drawString(ratingStr, 35 + (j * 15), 520 - (i * 10));
                } else {
                    if (counter % 2 == 0 && i % 2 == 0) {
                        g.setColor(new Color(235, 235, 235));
                        g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
                    } else if (counter % 2 != 0 && i % 2 != 0) {
                        g.setColor(new Color(235, 235, 235));
                        g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
                    }
                }
            }
        }
    }

    public String getLoadedSubjectsAndFreeHours() {
		return "Materias cargadas: "+subjectsLoaded
				+"      \tHoras libres: "+freeHours;
    }
    
    public int getAverageModerateDemand() {
        int total = 0;
        for (int demand : schedule) {
            if (demand != -1) total += getModerateProfessorDemand(demand);
        }
        return total / subjectsLoaded;
    }
    
    private int getModerateProfessorDemand(int demand) {
        return demand > 50 ? (100 - ((demand - 50) * 2)) : (demand * 2);
    }
    
    public String getEntryTimeAndAverage() {
        return "Hora de entrada: " + entryTime + "      \tExigencia Moderada: " + getAverageModerateDemand() + "%";
    }
    
    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    // Getters and Setters
    public int getSubjectsLoaded() { return subjectsLoaded; }
    
    public int getFreeHours() { return freeHours; }
    
    public double getRating() { return rating; }
    
    public void setRating(double rating) { this.rating = rating; }
    
    public void setId(int id) { this.id = id; }
    
    public void setSubjectsLoaded(int subjectsLoaded) { this.subjectsLoaded = subjectsLoaded; }
    
    public void setFreeHours(int freeHours) { this.freeHours = freeHours; }
    
    public int getEntryTime() { return entryTime; }
    
    public void setEntryTime(int entryTime) { this.entryTime = entryTime; }
    
    public void setSchedule(int[] schedule) { this.schedule = schedule; }
    
    public int getId() { return id; }
    
    public int[] getSchedule() { return schedule; }
    
    public String getDate() { return getCurrentDate(); }

    @Override
    public String toString() {
        return "\nSchedule " + id + " [subjectsLoaded=" + subjectsLoaded 
            + ", freeHours=" + freeHours + ", entryTime=" + entryTime 
            + ", schedule=" + Arrays.toString(schedule) + ", rating=" + rating + "]\n";
    }
}


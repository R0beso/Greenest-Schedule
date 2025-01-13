
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Horario {
	
	private int id;
	private int materiasCargadas;
	private int horasLibres;
	private int horaDeEntrada;
	private int[] horario;
	private double calificacion;
	
    public Horario(Horario otro) {
        this.id = otro.id;
        this.materiasCargadas = otro.materiasCargadas;
        this.horasLibres = otro.horasLibres;
        this.horaDeEntrada = otro.horaDeEntrada;
        this.calificacion = otro.calificacion;
        this.horario = otro.horario;
    }
    
    public Horario(int id, int materiasCargadas, int horasLibres, int horaDeEntrada, int[] horario, double calificacion) {
        this.id = id;
        this.materiasCargadas = materiasCargadas;
        this.horasLibres = horasLibres;
        this.horaDeEntrada = horaDeEntrada;
        this.calificacion = calificacion;
        this.horario = horario;
    }

	
	public void graficarHorario(Graphics g) {
		
		Font font = new Font("Serif", Font.PLAIN, 19);
	    g.setFont(font);

		g.setColor(new Color(188,188,188));
		g.drawRect(30, 447, 370, 75);
		
		g.setColor(new Color(248,248,248));
		g.fillRect(30, 85, 370, 350);
		
		g.setColor(new Color(34,42,53));
		g.fillRect(30, 85, 370, 25);

		g.setColor(new Color(238,238,238));
		g.drawRect(30, 85, 70, 350);
		for(int i=0; i<5; i++) {
			g.drawRect(100, 85, 60+(i*60), 350);
		}
		
		for(int i=0; i<horario.length; i++) {
			g.setColor(new Color(34,42,53));
			if (horario[i] != -1) {
				pintarMateria(i, g);
			}
		}
		
		pintarFondoHora(g);
		for(int i=0; i<horario.length; i++) {
			g.drawString((i+7)+":00", 43, 130+(i*25));
		}
		
		for(int i=0; i<14; i++) {
			g.setColor(new Color(238,238,238));
			g.drawRect(30, (85+(i*25)), 370, 25);
		}

		g.drawLine(100, 85, 100, 435);
		
	}
	
	private void pintarMateria(int i, Graphics g) {
		g.setColor(interpolarColor(mapExigencia( horario[i]) ));
		g.fillRect(30, (110+(i*25)), 370, 25);
		g.setColor(Color.WHITE);
	    g.drawString("Exigencia del profesor "+horario[i]+"%", 125, 130+(i*25));
	}
	
	private void pintarFondoHora(Graphics g) {
		g.setColor(interpolarColor( mapNumber((int)calificacion)) );
		g.fillRect(30, 85, 70, 350);
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

	public void graficarCalificacionSiguientesHorarios(Graphics g, List<Horario> listaHorarios, int contador) {
		g.setFont(new Font("Arial", Font.PLAIN, 11)); // Cambiar la fuente y tamaño
	    String calificacion;
	    int index;
	    
	
	    for (int j = 0; j < 3; j++) {
	        for (int i = 0; i < 7; i++) {
	            // Verificar si el índice está dentro de los límites
	            index = i + (j * 7);
	            if (index < listaHorarios.size()) {
	                g.setColor(new Color(225, 225, 225));
	                g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
	                calificacion = ((int) Math.floor(listaHorarios.get(index).getCalificacion())) + "";
	                if (Integer.parseInt(calificacion) < 95) {
		                g.setColor(new Color(155,155,155));
	                }else {
		                g.setColor(Color.BLACK);
	                }
	                g.drawString(calificacion, 35 + (j * 15), 520 - (i * 10));
	            }else {
	            	if( contador % 2 == 0 && i % 2 == 0) {
	                	g.setColor(new Color(235, 235, 235));
		                g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
	            	} else if ( contador % 2 != 0 && i % 2 != 0) {
	                	g.setColor(new Color(235, 235, 235));
		                g.fillRect(32 + (j * 15), 510 - (i * 10), 16, 11);
	            	}
	            }
	        }
	    }
	}

	public int getMateriasCargadas() {
		return materiasCargadas;
	}

	public int getHorasLibres() {
		return horasLibres;
	}
	
	public String getMateriasCargadasYHorasLibres() {
		return "Materias cargadas: "+materiasCargadas
				+"      \tHoras libres: "+horasLibres;
	}

	public int getHoraDeEntrada() {
		return horaDeEntrada;
	}

	public int getPromedioDeExigenciaModeradaProfesores() {
		
		int promedio = 0;
		for(int i =0; i<horario.length; i++) {
			if(horario[i] != -1) {
				promedio += getExigenciaModeradaProfesor(horario[i]);
			}
		}
		return promedio / materiasCargadas;
		
	}
	
	private int getExigenciaModeradaProfesor(int exigencia) {
		return exigencia > 50 
			?( 
				100 - ((exigencia - 50) * 2)
			):( 
				exigencia * 2
			);
	}
	
	public String getHoraDeEntradaYPromedio() {
		return "Hora de entrada: "+horaDeEntrada
				+"      \tExigencia Moderada: "
				+getPromedioDeExigenciaModeradaProfesores()+"%";
	}
	
    private String obtenerFechaActual() {
        LocalDate fechaActual = LocalDate.now(); // Fecha actual
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaActual.format(formato); // Convertir a String
    }

	public double getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(double calificacion) {
		this.calificacion = calificacion;
	}

	public void setId(int id) {
		this.id = id;
		
	}
	
	public void setMateriasCargadas(int materiasCargadas) {
		this.materiasCargadas = materiasCargadas;
	}

	public void setHorasLibres(int horasLibres) {
		this.horasLibres = horasLibres;
	}

	public void setHoraDeEntrada(int horaDeEntrada) {
		this.horaDeEntrada = horaDeEntrada;
	}

	public void setHorario(int[] horario) {
		this.horario = horario;
	}
	
	public void setHoraEntrada(int horaDeEntrada) {
		this.horaDeEntrada = horaDeEntrada;
	}
	
    public int getId() {
        return id;
    }

    public int[] getHorario() {
        return horario;
    }

    public String getFecha() {
        return obtenerFechaActual();
    }
	
	@Override
	public String toString() {
		return "\nHorario " + id + " [materiasCargadas=" + materiasCargadas + ", horasLibres=" + horasLibres
				+ ", horaDeEntrada=" + horaDeEntrada + ", horario=" + Arrays.toString(horario) + ", calificacion="
				+ calificacion + "]\n";
	}
	
}


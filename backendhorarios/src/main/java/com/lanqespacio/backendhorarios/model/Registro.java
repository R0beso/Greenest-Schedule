package com.lanqespacio.backendhorarios.model;

import com.lanqespacio.backendhorarios.service.session.Horario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Data
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 8, message = "El apodo debe contener máximo 8 caracteres.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "El apodo solo puede contener letras.")
    private String apodo;

    @NotNull
    private double calificacion;

    @NotNull
    private String exigencias;

    @NotNull
    private String fecha;

    public Registro(String a, HttpSession session){
        this.apodo = a.toUpperCase();
        this.fecha = obtenerFechaActual();
        this.calificacion = getCalificacion(session);
        this.calificacion = truncarNumero(this.calificacion);
        this.calificacion = formatearNumero(this.calificacion);
        this.exigencias = getExigencias(session);
    }

    private String obtenerFechaActual() {
        LocalDate fechaActual = LocalDate.now(); // Fecha actual
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaActual.format(formato); // Convertir a String
    }

    private double truncarNumero(double numero) {
        // Si tiene más de 15 caracteres, truncar la cadena
        String numeroComoCadena = String.valueOf(numero);
        if (numeroComoCadena.length() > 15) {
            numeroComoCadena = numeroComoCadena.substring(0, 15);
        }
        return Double.parseDouble(numeroComoCadena);
    }

    private double formatearNumero(double numero) {
        // Convertir el número a cadena con 15 decimales
        String numeroFormateado = String.format("%.15f", numero);
        // Asegurarse de que el número tenga exactamente 15 caracteres, añadiendo un "5" en la última posición
        numeroFormateado = numeroFormateado.substring(0, 14) + "5";
        return Double.parseDouble(numeroFormateado);
    }

    private double getCalificacion(HttpSession session) {
        return ((Horario) session.getAttribute("mejorHorario")).getCalificacion();
    }

    private String getExigencias(HttpSession session) {
        return arrayAString(((Horario) session.getAttribute("mejorHorario")).getHorario());
    }

    private String arrayAString(int[] array) {
        return Arrays.stream(array)  // Convierte el arreglo en un stream
                .filter(num -> num != -1)  // Filtra los elementos distintos de -1
                .mapToObj(String::valueOf)  // Convierte los números a Strings
                .collect(Collectors.joining(","));  // Une los Strings con comas
    }

}

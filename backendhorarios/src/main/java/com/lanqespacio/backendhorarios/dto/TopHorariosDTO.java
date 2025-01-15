package com.lanqespacio.backendhorarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

//@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopHorariosDTO {

    @JsonProperty("top")
    private Integer top;
    
    @JsonProperty("apodo")
    private String apodo;
    
    @JsonProperty("fecha")
    private String fecha;
    
    @JsonProperty("exigencias")
    private String exigencias;
    
    @JsonProperty("calificacion")
    private String calificacion;

    public TopHorariosDTO(Integer top, String apodo, String fecha, String exigencias, String calificacion) {
        this.top = top != null ? top : 0;
        this.apodo = apodo != null ? apodo : "";
        this.fecha = fecha != null ? fecha : "";
        this.exigencias = exigencias != null ? exigencias : "";
        this.calificacion = calificacion != null ? calificacion : "";
    }

    // Getters
    public int getTop() {
        return top;
    }

    public String getApodo() {
        return apodo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getExigencias() {
        return exigencias;
    }

    public String getCalificacion() {
        return calificacion;
    }

}

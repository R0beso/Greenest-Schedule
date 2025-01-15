package com.lanqespacio.backendhorarios.service.session;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Horario implements Serializable {

    private int id;
    private int materiasCargadas;
    private int horasLibres;
    private int horaEntrada;
    private double calificacion;
    private int[] horario;
    private Random r;

    public Horario(int id) {
        this.id = id;
        this.r = new Random();
        this.materiasCargadas = generaMateriasCargadas();
        this.horasLibres = generaHorasLibres();
        this.horaEntrada = generaHoraDeEntrada();
    }

    // Constructor de copia para establecer el horario con mayor calificacion
    public Horario(Horario otro) {
        this.id = otro.id;
        this.materiasCargadas = otro.materiasCargadas;
        this.horasLibres = otro.horasLibres;
        this.horaEntrada = otro.horaEntrada;
        this.calificacion = otro.calificacion;
        this.horario = otro.horario;
    }

    private int generaMateriasCargadas() {

        int materiasCargadas = 0;
        double nAleatorio = r.nextDouble();

        if(nAleatorio < 0.1) {
            if(nAleatorio < 0.05) materiasCargadas = 3;
            else materiasCargadas = 4;

        } else if(nAleatorio < 0.3) {
            if(nAleatorio < 0.2) materiasCargadas = 5;
            else materiasCargadas = 8;

        } else {
            if(nAleatorio < 0.65) materiasCargadas = 6;
            else materiasCargadas = 7;
        }
        return materiasCargadas;
    }

    private int generaHorasLibres() {

        double nAleatorio = r.nextDouble();
        int horasLibres = 0;

        if(nAleatorio < 0.1) {
            if (nAleatorio < 0.066) horasLibres = 3;
            else if (nAleatorio < 0.088) horasLibres = 4;
            else horasLibres = 5;

        } else if(nAleatorio < 0.2) {
            horasLibres = 2;

        } else {
            if (nAleatorio < 0.6) horasLibres = 1;
            // else horasLibres = 0;
        }
        return horasLibres;
    }

    private int generaHoraDeEntrada() {

        int horaEntrada = 0;
        double nAleatorio;
        do {
            nAleatorio = r.nextDouble();

            if(nAleatorio < 0.3) {

                if(nAleatorio < 0.15) horaEntrada = 7;
                else horaEntrada = 8;

            } else if(nAleatorio < 0.5) {

                if(nAleatorio < 0.35) horaEntrada = 9;
                if(nAleatorio < 0.4) horaEntrada = 10;
                if(nAleatorio < 0.45) horaEntrada = 11;
                else horaEntrada = 12;

            } else if(nAleatorio < 0.8) {

                if(nAleatorio < 0.65) horaEntrada = 13;
                else horaEntrada = 14;

            } else {

                if(nAleatorio < 0.866) horaEntrada = 15;
                if(nAleatorio < 0.933) horaEntrada = 16;
                else horaEntrada = 17;
            }
        }while((materiasCargadas+horasLibres+horaEntrada)>=21);
        return horaEntrada;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public void setHorario(int[] horario) {
        this.horario = horario;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int[] getHorario() {
        return horario;
    }

    public int getMateriasCargadas() {
        return materiasCargadas;
    }

    public int getHorasLibres() {
        return horasLibres;
    }

    public int getHoraDeEntrada() {
        return horaEntrada;
    }

    public int[] getExigencias() {
        return horario;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\nHorario " + id + " [materiasCargadas=" + materiasCargadas + ", horasLibres=" + horasLibres
                + ", horaDeEntrada=" + horaEntrada + ", horario=" + Arrays.toString(horario) + ", calificacion="
                + calificacion + "]\n";
    }
}

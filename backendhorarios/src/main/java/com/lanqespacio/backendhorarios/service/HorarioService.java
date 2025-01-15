package com.lanqespacio.backendhorarios.service;

import com.lanqespacio.backendhorarios.service.session.Horario;
import com.lanqespacio.backendhorarios.service.session.Horarios;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class HorarioService {

    private BufferedImage graficas;

    public HorarioService(){
 //       this.graficas = new BufferedImage(130, 130, 0);
    }

    public List<Horario> nuevosHorarios(HttpSession session) {

        List<Horario> list = new ArrayList<>();
        List<Horario> nuevosHorarios;
        Integer id = (Integer) session.getAttribute("id");

        if (id == null)
            id = 0;

        try {
            list = ((Horarios) session.getAttribute("horariosActuales")).get();
        } catch (Exception _){}


 //       list = generarHorarios(id, session);
        nuevosHorarios = generarHorarios(id, session);
        list.addAll(nuevosHorarios);

        id+=10;
        session.setAttribute("id", id);
        session.setAttribute("horariosActuales", new Horarios(list));

        System.out.println(list);
        System.out.println();
        return nuevosHorarios;
    }

    public Horario mejorHorario(HttpSession session, int idActual) {

        int i = 0;
        Horarios hs = (Horarios) session.getAttribute("horariosActuales");
        Horario horario;
        do {
            horario = new Horario(hs.getHorario(i));

            // Verifica si el horario tiene mejor calificacion
            if(horario.getCalificacion() > ((Horario) session.getAttribute("mejorHorario")).getCalificacion())
                session.setAttribute("mejorHorario", new Horario(horario));

            i++;
        } while(idActual > i);

        return (Horario) session.getAttribute("mejorHorario");
    }


    public void eliminarHorario(HttpSession session) {

        Horarios hs = (Horarios) session.getAttribute("horariosActuales");

        // Define que horario es el mejor antes de eliminar la lista actual de horarios
        if (hs != null)
            mejorHorario(session, 10);


        hs.eliminar10horarios();

        session.setAttribute("horariosActuales", hs);

    }

    private List<Horario> generarHorarios(int id , HttpSession session) {
        Horario horario;
        List<Horario> list = new ArrayList<>();
        int j = 0;
        for(int i =0; i<10; i++) {

            horario = new Horario(id + j);


            horario.setHorario(
                    generaHorarioRandom(
                            horario.getMateriasCargadas(),
                            horario.getHorasLibres(),
                            horario.getHoraDeEntrada()
                    )
            );

            // Calcula calificación con Lógica Difusa
            horario.setCalificacion(
                    aplicaLogicaDifusa(
                            horario.getMateriasCargadas(),
                            horario.getHorasLibres(),
                            horario.getHoraDeEntrada(),
                            horario.getExigencias()
                    )
            );
            list.add(horario);


            if (session.getAttribute("mejorHorario") == null) {
                session.setAttribute("mejorHorario", new Horario(horario));
            }

            j++;
        }
        return list;
    }

    private int[] generaHorarioRandom(int materiasCargadas, int horasLibres, int horaDeEntrada) {

        Random r = new Random();
        int[] horario = new int[13];
        int asignar = materiasCargadas+horasLibres;
        int materiasPorBorrar = horasLibres;
        int posicion = horaDeEntrada-6;

        for(int i =0; i<horario.length; i++) {
            horario[i] = -1;
            if(asignar > 0 && i >=  horaDeEntrada-7) {
                horario[i] = generaExigenciaProfesor();
                asignar--;
            }
        }

        while(materiasPorBorrar > 0) {

            if(posicion == horaDeEntrada+materiasCargadas+horasLibres-8) {
                posicion = horaDeEntrada-6;
            }
            if (r.nextInt(5) == 0 && horario[posicion] != -1) {
                horario[posicion] = -1;
                materiasPorBorrar--;
            }
            posicion++;
        }

        return horario;
    }

    private int generaExigenciaProfesor() {
        Random r = new Random();
        double nAleatorio = r.nextDouble();
        int exigencia = 0;

        if(nAleatorio < 0.8) {
            exigencia = r.nextInt(40)+31;

        } else if(nAleatorio < 0.95) {
            if(nAleatorio < 0.875) exigencia = r.nextInt(20)+11;
            else exigencia = r.nextInt(20)+71;

        } else {
            if(nAleatorio < 0.975) exigencia = r.nextInt(11);
            else exigencia = r.nextInt(10)+91;
        }
        return exigencia;
    }

    private double aplicaLogicaDifusa(int materiasCargadas, int horasLibres, int horaDeEntrada, int[] horario) {
        try {
            FunctionBlock fb = new FunctionBlock(null);

            // Cadena FCL en lugar de leer un archivo .fcl
            String fuzzyLogicString =
                    """
                        FUNCTION_BLOCK fcl
                        
                        VAR_INPUT
                            materiasCargadas : REAL; // (3 a 8)
                            horasLibres : REAL;     // (0 a 6)
                            horaDeEntrada : REAL;   // (7 a 17)
                            promedioExigenciaModeradaProfesores : REAL;  // (0 a 100)
                        END_VAR
                        
                        VAR_OUTPUT
                            // (0 a 100)
                            calificacionHorarioRango33a67 : REAL;
                        END_VAR
                        
                        FUZZIFY materiasCargadas
                            TERM pocas := (2.5, 0) (2.5, 1) (3.8, 1) (5.3, 0);
                            TERM buenas := (2.5, 0) (4.5, 0.1) (5.5, 1) (6.8, 1) (7.5, 0.1) (8.5, 0);
                            TERM elMaximo := (7.5, 0) (7.8, 1) (8.5, 1) (8.5, 0);
                        END_FUZZIFY
                        
                        FUZZIFY horasLibres
                            TERM ningunaOUna := (-0.5, 0) (-0.5, 1) (0.9, 1) (2.4, 0.2) (5.5, 0);
                            TERM muchas := (-0.5, 0) (1.4, 0.1) (4.2, 1) (5.5, 1) (5.5, 0);
                        END_FUZZIFY
                        
                        FUZZIFY horaDeEntrada
                            TERM matutino := (6.5, 0) (6.5, 1) (7.9, 1) (10.6, 0);
                            TERM mixto := (8.5, 0) (9.5, 1) (10.5, 1) (12.5, 0);
                            TERM vespertino := (10.4, 0) (12.5, 1) (17.5, 1) (17.5, 0);
                        END_FUZZIFY
                        
                        FUZZIFY promedioExigenciaModeradaProfesores
                            TERM poco := (0, 0) (0, 1) (100, 0);
                            TERM mucho := (0, 0) (100, 1) (100, 0);
                        END_FUZZIFY
                        
                        DEFUZZIFY calificacionHorarioRango33a67
                            TERM malo := (0, 0) (0, 1) (100, 0);
                            TERM bueno := (0, 0) (100, 1) (100, 0);
                            METHOD : COG;
                        END_DEFUZZIFY
                        
                        RULEBLOCK No1
                        
                            RULE 1 : IF ((materiasCargadas IS pocas) OR (materiasCargadas IS elMaximo)) AND (horasLibres IS muchas) AND (horaDeEntrada IS mixto) AND (promedioExigenciaModeradaProfesores IS poco) THEN calificacionHorarioRango33a67 IS malo;
                            RULE 2 : IF materiasCargadas IS buenas AND horasLibres IS ningunaOUna AND (horaDeEntrada IS matutino OR horaDeEntrada IS vespertino) AND promedioExigenciaModeradaProfesores IS mucho THEN calificacionHorarioRango33a67 IS bueno;
                        
                        END_RULEBLOCK
                        
                        END_FUNCTION_BLOCK
                    """;

            FIS fis = FIS.createFromString(fuzzyLogicString, true);
            fb = fis.getFunctionBlock(null);


            // Asignar valores a las variables de entrada
            fb.setVariable("materiasCargadas", materiasCargadas);
            fb.setVariable("horasLibres", horasLibres);
            fb.setVariable("horaDeEntrada", horaDeEntrada);
            fb.setVariable("promedioExigenciaModeradaProfesores", getPromedioDeExigenciaModeradaProfesores(materiasCargadas, horario));

            // Evaluar el sistema difuso
            fb.evaluate();

            // Obtener la salida de calificación y ajustarla al rango esperado
            Variable calificacionHorario = fb.getVariable("calificacionHorarioRango33a67");


            return ((calificacionHorario.getValue() - 33.3) * 3);
        } catch (Exception e) {
            System.err.println("Error en lógica difusa: " + e.getMessage());
            return 0;
        }
    }

    private int getPromedioDeExigenciaModeradaProfesores(int materiasCargadas, int[] horario) {
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

}
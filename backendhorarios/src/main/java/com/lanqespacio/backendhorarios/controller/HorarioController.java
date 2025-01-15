package com.lanqespacio.backendhorarios.controller;

import com.lanqespacio.backendhorarios.dto.TopHorariosDTO;
import com.lanqespacio.backendhorarios.service.HorarioService;
import com.lanqespacio.backendhorarios.service.session.Horario;
import com.lanqespacio.backendhorarios.model.Registro;
import com.lanqespacio.backendhorarios.repository.RegistroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/horario-api")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;
    @Autowired
    private RegistroRepository registroRepository;

    @GetMapping("/nuevosHorarios")
    public List<Horario> getNuevosHorarios(HttpSession session) {
        return horarioService.nuevosHorarios(session);
    }

    @GetMapping("/eliminarHorarios")
    public String deleteHorarios(HttpSession session) {
        try {
            horarioService.eliminarHorario(session);
            return "Horarios eliminados exitosamente.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/mejorHorario")
    public Horario getMejorHorario(@RequestParam int posicion, HttpSession session) {
        return horarioService.mejorHorario(session, posicion);
    }

    @GetMapping("/guardarHorario")
    public String guardarRegistro(@RequestParam String apodo, HttpSession session) {
        try {
            registroRepository.save(new Registro(apodo, session));
            return "Horario guardado exitosamente.";
        } catch (Exception e) {
            return "Hubo un error al publicar el horario.";
        }
    }

    @GetMapping("/registros")
    public List<TopHorariosDTO> getMejoresPosiciones() {
        List<Object[]> registros = registroRepository.topHorarios();
        return registros.stream()
                .map(record -> {
                    try {
                        return new TopHorariosDTO(
                                ((Number) record[0]).intValue(),
                                (String) record[1],
                                (String) record[2],
                                (String) record[3],
                                (String) record[4]
                        );
                    } catch (Exception e) {
                        return new TopHorariosDTO(500,e.getMessage(),"error","","");
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/rubrica")
    @ResponseBody
    public ResponseEntity<Resource> getRubricaImagen() {
        Resource image = new ClassPathResource("static/images/img-rubrica.png");
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(image);
    }

    @GetMapping("/icono")
    @ResponseBody
    public ResponseEntity<Resource> getIconoImagen() {
        Resource image = new ClassPathResource("static/images/icono.png");
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(image);
    }

    @GetMapping("/echo")
    public String echo() {
        return "Hay conexión con el servidor";
    }

}


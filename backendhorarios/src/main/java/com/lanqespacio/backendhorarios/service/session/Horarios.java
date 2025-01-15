package com.lanqespacio.backendhorarios.service.session;

import java.io.Serializable;
import java.util.List;

public class Horarios implements Serializable {

    private List<Horario> hs;

    public Horarios(List<Horario> hs){
        this.hs = hs;
    }

    public Horario getHorario(int i) {
        return hs.get(i);
    }

    public void eliminar10horarios() {
        hs.subList(0, 10).clear();
    }

    public List<Horario> get() {
        return hs;
    }

}

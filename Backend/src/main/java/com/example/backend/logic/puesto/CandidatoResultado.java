package com.example.backend.logic.puesto;

import com.example.backend.logic.oferente.Oferente;
import lombok.Getter;

@Getter
public class CandidatoResultado {
    private final Oferente oferente;
    private final int cumplidos;
    private final int total;
    private final double porcentaje;

    public CandidatoResultado(Oferente oferente, int cumplidos, int total) {
        this.oferente = oferente;
        this.cumplidos = cumplidos;
        this.total = total;
        this.porcentaje = total > 0 ? (cumplidos * 100.0 / total) : 0;
    }
}
package com.example.backend.logic.puesto;

import com.example.backend.logic.oferente.Oferente;
import lombok.Getter;

import java.util.List;

@Getter
public class CandidatoResultado {
    private final Oferente oferente;
    private final int cumplidos;
    private final int total;
    private final double porcentaje;
    private final int exceso;
    private final List<DetalleRequisito> detalle;

    public CandidatoResultado(Oferente oferente, int cumplidos, int total, int exceso, List<DetalleRequisito> detalle) {
        this.oferente = oferente;
        this.cumplidos = cumplidos;
        this.total = total;
        this.exceso = exceso;
        this.detalle = detalle;
        this.porcentaje = total > 0 ? (cumplidos * 100.0 / total) : 0;
    }
}
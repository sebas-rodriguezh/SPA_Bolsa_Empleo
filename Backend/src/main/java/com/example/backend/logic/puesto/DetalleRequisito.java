package com.example.backend.logic.puesto;

import lombok.Getter;

@Getter
public class DetalleRequisito {
    private final String nombreCaracteristica;
    private final int nivelTiene;
    private final int nivelPide;

    public DetalleRequisito(String nombreCaracteristica, int nivelTiene, int nivelPide) {
        this.nombreCaracteristica = nombreCaracteristica;
        this.nivelTiene = nivelTiene;
        this.nivelPide = nivelPide;
    }
}

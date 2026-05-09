package com.example.backend.dto.oferente;

public class AgregarHabilidadDTO {
    private Integer caracteristicaId;
    private Integer nivel;

    public Integer getCaracteristicaId() { return caracteristicaId; }
    public void setCaracteristicaId(Integer id) { this.caracteristicaId = id; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
}
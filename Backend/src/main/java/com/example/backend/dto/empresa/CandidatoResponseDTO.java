package com.example.backend.dto.empresa;

import com.example.backend.logic.puesto.DetalleRequisito;

import java.util.List;

public class CandidatoResponseDTO {
    private Integer oferenteId;
    private String nombre;
    private String primerApellido;
    private String correo;
    private String telefono;
    private String lugarResidencia;
    private String identificacion;
    private Integer cumplidos;
    private Integer total;
    private Double porcentaje;
    private String rutaCurriculum;
    private List<DetalleRequisito> detalle;

    public CandidatoResponseDTO(Integer oferenteId, String nombre, String primerApellido, String correo, String telefono, String lugarResidencia, String identificacion, Integer cumplidos, Integer total, Double porcentaje, String rutaCurriculum, List<DetalleRequisito> detalle) {
        this.oferenteId = oferenteId;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.correo = correo;
        this.telefono = telefono;
        this.lugarResidencia = lugarResidencia;
        this.identificacion = identificacion;
        this.cumplidos = cumplidos;
        this.total = total;
        this.porcentaje = porcentaje;
        this.rutaCurriculum = rutaCurriculum;
        this.detalle = detalle;
    }
    public Integer getOferenteId() { return oferenteId; }
    public String getNombre() { return nombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getLugarResidencia() { return lugarResidencia; }
    public String getIdentificacion() { return identificacion; }
    public Integer getCumplidos() { return cumplidos; }
    public Integer getTotal() { return total; }
    public Double getPorcentaje() { return porcentaje; }
    public String getRutaCurriculum() { return rutaCurriculum; }
    public List<DetalleRequisito> getDetalle() { return detalle; }
}
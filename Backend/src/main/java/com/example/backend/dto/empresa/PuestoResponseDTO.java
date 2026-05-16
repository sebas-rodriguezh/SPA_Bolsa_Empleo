package com.example.backend.dto.empresa;

import java.util.List;
import java.util.Map;

public class PuestoResponseDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double salario;
    private String moneda;
    private Boolean esPublico;
    private Boolean activo;
    private String fechaRegistro;
    private String empresaNombre;

    private List<Map<String, Object>> requisitos;

    public PuestoResponseDTO(Integer id, String nombre, String descripcion, Double salario, String moneda, Boolean esPublico, Boolean activo, String fechaRegistro, String empresaNombre, List<Map<String, Object>> requisitos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.salario = salario;
        this.moneda = moneda;
        this.esPublico = esPublico;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.empresaNombre = empresaNombre;
        this.requisitos = requisitos;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getSalario() { return salario; }
    public String getMoneda() { return moneda; }
    public Boolean getEsPublico() { return esPublico; }
    public Boolean getActivo() { return activo; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getEmpresaNombre() { return empresaNombre; }
    public List<Map<String, Object>> getRequisitos() { return requisitos; }
}
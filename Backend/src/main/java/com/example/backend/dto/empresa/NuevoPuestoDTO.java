package com.example.backend.dto.empresa;

public class NuevoPuestoDTO {
    private String nombre;
    private String descripcion;
    private Double salario;
    private Boolean esPublico;
    private String moneda;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
    public Boolean getEsPublico() { return esPublico; }
    public void setEsPublico(Boolean esPublico) { this.esPublico = esPublico; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
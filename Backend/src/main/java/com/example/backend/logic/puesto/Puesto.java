package com.example.backend.logic.puesto;

import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.puestoCaracteristica.PuestoCaracteristica;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "puesto")
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "salario", nullable = false)
    private Double salario;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "es_publico", nullable = false)
    private Boolean esPublico;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @NotNull
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
//
//    @OneToMany(mappedBy = "puesto")
//    private Set<PuestoCaracteristica> puestoCaracteristicas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PuestoCaracteristica> requisitos = new LinkedHashSet<>();
    @Size(max = 10)

    @NotNull
    @ColumnDefault("'CRC'")
    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda;
    @Size(max = 200)

    @NotNull
    @ColumnDefault("'Sin nombre'")
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

}
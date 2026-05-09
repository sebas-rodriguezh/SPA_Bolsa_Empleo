package com.example.backend.logic.empresa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 200)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Size(max = 300)
    @NotNull
    @Column(name = "localizacion", nullable = false, length = 300)
    private String localizacion;

    @Size(max = 150)
    @NotNull
    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @Size(max = 255)
    @NotNull
    @Column(name = "clave", nullable = false)
    private String clave;

    @Size(max = 20)
    @NotNull
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotNull
    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "autorizada", nullable = false)
    private Boolean autorizada;


}
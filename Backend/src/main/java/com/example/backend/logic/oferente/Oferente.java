package com.example.backend.logic.oferente;

import com.example.backend.logic.oferenteHabilidad.OferenteHabilidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "oferente")
public class Oferente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "identificacion", nullable = false, length = 20)
    private String identificacion;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 100)
    @NotNull
    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Size(max = 100)
    @NotNull
    @Column(name = "nacionalidad", nullable = false, length = 100)
    private String nacionalidad;

    @Size(max = 20)
    @NotNull
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Size(max = 100)
    @NotNull
    @Column(name = "correo", nullable = false, length = 100)
    private String correo;

    @Size(max = 255)
    @NotNull
    @Column(name = "clave", nullable = false)
    private String clave;

    @Size(max = 300)
    @NotNull
    @Column(name = "lugar_residencia", nullable = false, length = 300)
    private String lugarResidencia;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "autorizado", nullable = false)
    private Boolean autorizado;

    @Size(max = 255)
    @Column(name = "ruta_curriculum")
    private String rutaCurriculum;

//    @OneToMany(mappedBy = "oferente")
//    private Set<OferenteHabilidad> oferenteHabilidads = new LinkedHashSet<>();

    @OneToMany(mappedBy = "oferente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OferenteHabilidad> habilidades = new LinkedHashSet<>();
}
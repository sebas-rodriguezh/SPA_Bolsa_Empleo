package com.example.backend.logic.postulacion;

import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.puesto.Puesto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "postulacion")
public class Postulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferente_id", nullable = false)
    private Oferente oferente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @NotNull
    @Column(name = "fecha_postulacion", nullable = false)
    private LocalDate fechaPostulacion;

//    @NotNull
//    @ColumnDefault("'PENDIENTE'")
//    @Lob
//    @Column(name = "estado", nullable = false)
//    private String estado;

    @NotNull
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
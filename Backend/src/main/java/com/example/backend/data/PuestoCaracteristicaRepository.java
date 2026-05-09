package com.example.backend.data;

import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puestoCaracteristica.PuestoCaracteristica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoCaracteristicaRepository extends CrudRepository<PuestoCaracteristica, Integer> {
    List<PuestoCaracteristica> findByPuesto(Puesto puesto);
    Optional<PuestoCaracteristica> findByPuestoAndCaracteristica(Puesto puesto, Caracteristica caracteristica);
}

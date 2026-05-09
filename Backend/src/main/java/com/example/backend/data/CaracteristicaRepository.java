package com.example.backend.data;

import com.example.backend.logic.caracteristica.Caracteristica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaracteristicaRepository extends CrudRepository<Caracteristica, Integer> {

    List<Caracteristica> findByPadreIsNull();
    List<Caracteristica> findByPadre(Caracteristica padre);
    Optional<Caracteristica> findByNombreIgnoreCaseAndPadre(String nombre, Caracteristica padre);
    Optional<Caracteristica> findByNombreIgnoreCaseAndPadreIsNull(String nombre);


}

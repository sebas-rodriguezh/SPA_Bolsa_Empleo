package com.example.backend.data;

import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferenteHabilidad.OferenteHabilidad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteHabilidadRepository extends  CrudRepository<OferenteHabilidad, Integer> {
    List<OferenteHabilidad> findByOferente(Oferente oferente);
    Optional<OferenteHabilidad> findByOferenteAndCaracteristica(Oferente oferente, Caracteristica caracteristica);

}

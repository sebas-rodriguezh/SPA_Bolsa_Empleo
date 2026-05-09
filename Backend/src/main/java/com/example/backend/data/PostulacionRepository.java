package com.example.backend.data;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.postulacion.Postulacion;
import com.example.backend.logic.puesto.Puesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulacionRepository extends CrudRepository<Postulacion, Integer> {
    Optional<Postulacion> findByOferenteAndPuesto(Oferente oferente, Puesto puesto);
    List<Postulacion> findByPuesto(Puesto puesto);
    List<Postulacion> findByOferente(Oferente oferente);
}

package com.example.backend.data;

import com.example.backend.logic.oferente.Oferente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OferenteRepository extends CrudRepository<Oferente, Integer> {
    Oferente findOferenteByCorreoAndClave(String correo, String clave);
    Oferente findByCorreo(String correo);
    List<Oferente> findByAutorizadoFalse();
    Oferente findByIdentificacion(String identificacion);

}

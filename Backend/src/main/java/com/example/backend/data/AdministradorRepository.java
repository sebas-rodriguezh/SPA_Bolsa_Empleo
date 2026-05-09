package com.example.backend.data;

import com.example.backend.logic.administrador.Administrador;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends CrudRepository<Administrador,Integer> {
    Administrador findAdministradorByCorreoAndClave(String correo, String clave);
    Administrador findByCorreo(String correo);
}

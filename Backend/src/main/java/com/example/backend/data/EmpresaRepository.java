package com.example.backend.data;

import com.example.backend.logic.empresa.Empresa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends CrudRepository<Empresa, Integer> {

    Empresa findEmpresaByCorreoAndClave(String correo, String clave);
    Empresa findByCorreo(String correo);
    List<Empresa> findByAutorizadaFalse();
}

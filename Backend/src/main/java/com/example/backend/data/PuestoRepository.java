package com.example.backend.data;

import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.puesto.Puesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto, Integer> {
    List<Puesto> findByEmpresa(Empresa empresa);
    List<Puesto> findByActivoTrueOrderByFechaRegistroDescIdDesc();

    List<Puesto> findDistinctByEsPublicoTrueAndActivoTrueAndRequisitosCaracteristicaIdIn(List<Integer> ids);
    List<Puesto> findDistinctByActivoTrueAndRequisitosCaracteristicaIdIn(List<Integer> ids);


    List<Puesto> findTop5ByEsPublicoTrueAndActivoTrueOrderByFechaRegistroDescIdDesc();

}

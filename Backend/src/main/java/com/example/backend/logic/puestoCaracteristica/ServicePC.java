package com.example.backend.logic.puestoCaracteristica;

import com.example.backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class ServicePC {
    @Autowired
    private PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    public Iterable<PuestoCaracteristica> puestoCaracteristicaFindAll () {
        return puestoCaracteristicaRepository.findAll();
    }
}

package com.example.backend.logic.empresa;

import com.example.backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceE {
    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Iterable<Empresa> empresaFindAll () {
        return empresaRepository.findAll();
    }

    public void registrarEmpresa(Empresa empresa) {
        empresa.setClave(passwordEncoder.encode(empresa.getClave()));
        empresaRepository.save(empresa);
    }

    public List<Empresa> findPendientes() {
        return empresaRepository.findByAutorizadaFalse();
    }


    public void aprobarEmpresa(int id)
    {
        empresaRepository.findById(id).ifPresent (empresa -> {
            empresa.setAutorizada(true);
            empresaRepository.save(empresa);
        });
    }


    public String validarRegistro(String correo) {
        if (empresaRepository.findByCorreo(correo) != null) {
            return "Ya existe una empresa registrada con ese correo.";
        }
        return null;
    }

    public Empresa findByCorreo(String correo) {
        return empresaRepository.findByCorreo(correo);
    }
}

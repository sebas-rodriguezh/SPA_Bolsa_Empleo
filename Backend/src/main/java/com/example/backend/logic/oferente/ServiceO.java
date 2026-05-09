package com.example.backend.logic.oferente;

import com.example.backend.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceO {
    @Autowired
    private OferenteRepository oferenteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Iterable<Oferente> oferentesFindAll() {
        return oferenteRepository.findAll();
    }

    public void registrarOferente(Oferente oferente) {
        oferente.setClave(passwordEncoder.encode(oferente.getClave()));
        oferenteRepository.save(oferente);
    }

    public void actualizarOferente(Oferente oferente) {
        oferenteRepository.save(oferente);
    }

    public List<Oferente> findPendientes() {
        return oferenteRepository.findByAutorizadoFalse();
    }


    //CAMBIO.
    public void aprobarOferente(int id)
    {
        oferenteRepository.findById(id).ifPresent(oferente -> {
            oferente.setAutorizado(true);
            oferenteRepository.save(oferente);
        });
    }

    public String validarRegistro(String correo, String identificacion) {
        if (oferenteRepository.findByCorreo(correo) != null) {
            return "Ya existe un oferente registrado con ese correo.";
        }
        if (oferenteRepository.findByIdentificacion(identificacion) != null) {
            return "Ya existe un oferente registrado con esa identificación.";
        }
        return null;
    }

    public Oferente findById(Integer id) {
        return oferenteRepository.findById(id).orElse(null);
    }

    public Oferente findByCorreo(String correo) {
        return oferenteRepository.findByCorreo(correo);
    }

}

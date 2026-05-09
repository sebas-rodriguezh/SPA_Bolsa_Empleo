package com.example.backend.logic.oferenteHabilidad;

import com.example.backend.data.*;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.oferente.Oferente;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceOH {
    @Autowired
    private OferenteHabilidadRepository oferenteHabilidadRepository;

    public Iterable<OferenteHabilidad> oferenteHabilidadFindAll() {
        return oferenteHabilidadRepository.findAll();
    }



    //Cambio.
    public List<OferenteHabilidad> findByOferente(Oferente oferente)
    {
        return oferenteHabilidadRepository.findByOferente(oferente);
    }


    public void agregarOActualizar(Oferente oferente, Caracteristica caracteristica, int nivel) {
        Optional<OferenteHabilidad> existente = oferenteHabilidadRepository.findByOferenteAndCaracteristica(oferente, caracteristica);

        OferenteHabilidad h = existente.orElse(new OferenteHabilidad());
        h.setOferente(oferente);
        h.setCaracteristica(caracteristica);
        h.setNivel(nivel);
        oferenteHabilidadRepository.save(h);
    }

    public void eliminar(Integer id) {
        oferenteHabilidadRepository.deleteById(id);
    }
}

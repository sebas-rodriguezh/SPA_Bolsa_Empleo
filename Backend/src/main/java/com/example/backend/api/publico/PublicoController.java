package com.example.backend.api.publico;

import com.example.backend.dto.empresa.PuestoResponseDTO;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/publico")
public class PublicoController {

    @Autowired private ServiceP serviceP;
    @Autowired private ServiceC serviceC;


    @GetMapping("/puestos/recientes")
    public ResponseEntity<?> puestosRecientes() {
        List<PuestoResponseDTO> puestos = serviceP.getUltimosPuestosPublicos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(puestos);
    }

    @PostMapping("/puestos/buscar")
    public ResponseEntity<?> buscarPuestos(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("caracteristicaIds");
        String moneda = (String) body.get("moneda");
        List<PuestoResponseDTO> resultados = serviceP
                .buscarPuestosPublicos(ids, moneda)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/caracteristicas")
    public ResponseEntity<?> caracteristicas() {
        List<Map<String, Object>> arbol = serviceC.getArbolOrdenado().stream().map(c -> {
            List<Caracteristica> ruta = serviceC.buildRuta(c);
            int nivel = ruta.size() - 1;

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            map.put("nombre", c.getNombre());
            map.put("nivel", nivel);
            map.put("padreId", c.getPadre() != null ? c.getPadre().getId() : null);

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(arbol);
    }

    private PuestoResponseDTO toDTO(Puesto p) {
        List<Map<String, Object>> requisitos = serviceP
                .findRequisitosByPuesto(p)
                .stream()
                .map(r -> {
                    Map<String, Object> req = new java.util.LinkedHashMap<>();
                    req.put("caracteristicaNombre", r.getCaracteristica().getNombre());
                    req.put("rutaCompleta", serviceC.buildRutaString(r.getCaracteristica()));
                    req.put("nivel", r.getNivel());
                    return req;
                })
                .collect(Collectors.toList());

        return new PuestoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getSalario(),
                p.getMoneda(),
                p.getEsPublico(),
                p.getActivo(),
                p.getFechaRegistro().toString(),
                p.getEmpresa().getNombre(),
                requisitos
        );
    }
}
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


// api/publico/** (sin token)


@RestController
@RequestMapping("/api/publico")
public class PublicoController {
    @Autowired private ServiceP serviceP;

    @Autowired private ServiceC serviceC;


    /**
     * GET /api/publico/puestos/recientes
     * Los 5 puestos públicos más recientes (página de inicio).
     */

    @GetMapping("/puestos/recientes")
    public ResponseEntity<?> puestosRecientes() {
        List<PuestoResponseDTO> puestos = serviceP.getUltimosPuestosPublicos().stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(puestos);
    }

    /**
     * POST /api/publico/puestos/buscar
     * Búsqueda pública de puestos por características.
     * Body: { "caracteristicaIds": [1, 3, 5], "moneda": "CRC" }
     */

    @PostMapping("/puestos/buscar")
    public ResponseEntity<?> buscarPuestos(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("caracteristicaIds");
        String moneda = (String) body.get("moneda");
        List<PuestoResponseDTO> resultados = serviceP.buscarPuestosPublicos(ids, moneda).stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(resultados);
    }

    /**
     * GET /api/publico/caracteristicas
     * Árbol de características para los checkboxes de búsqueda.
     */

    @GetMapping("/caracteristicas")
    public ResponseEntity<?> caracteristicas()
    {
        List<Map<String, Object>> arbol = serviceC.getArbolOrdenado().stream().map(c -> {
            List<Caracteristica> ruta = serviceC.buildRuta(c);
            int nivel = ruta.size() - 1;
            return Map.of(
                    "id", (Object) c.getId(),
                    "nombre", c.getNombre(),
                    "nivel", nivel,
                    "padreId", c.getPadre() != null ? c.getPadre().getId() : null
            );
        }).collect(Collectors.toList());

        return  ResponseEntity.ok(arbol);
    }

    private PuestoResponseDTO toDTO(Puesto p) {
        return new PuestoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getSalario(),
                p.getMoneda(),
                p.getEsPublico(),
                p.getActivo(),
                p.getFechaRegistro().toString(),
                p.getEmpresa().getNombre()
        );
    }
}

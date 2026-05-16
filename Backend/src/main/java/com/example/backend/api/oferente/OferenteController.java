package com.example.backend.api.oferente;

import com.example.backend.dto.empresa.PuestoResponseDTO;
import com.example.backend.dto.oferente.ActualizarCVDTO;
import com.example.backend.dto.oferente.AgregarHabilidadDTO;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.logic.oferenteHabilidad.ServiceOH;
import com.example.backend.logic.postulacion.ServicePO;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/oferente")
public class OferenteController {

    @Autowired private ServiceO serviceO;
    @Autowired private ServiceOH serviceOH;
    @Autowired private ServiceC serviceC;
    @Autowired private ServiceP serviceP;
    @Autowired private ServicePO servicePO;

    private Oferente getOferente(Authentication auth) {
        return serviceO.findByCorreo(auth.getName());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        return ResponseEntity.ok(Map.of(
                "id", oferente.getId(),
                "nombre", oferente.getNombre(),
                "primerApellido", oferente.getPrimerApellido(),
                "identificacion", oferente.getIdentificacion(),
                "correo", oferente.getCorreo(),
                "telefono", oferente.getTelefono(),
                "nacionalidad", oferente.getNacionalidad(),
                "lugarResidencia", oferente.getLugarResidencia(),
                "rutaCurriculum", oferente.getRutaCurriculum() != null ? oferente.getRutaCurriculum() : ""
        ));
    }

    @GetMapping("/habilidades")
    public ResponseEntity<?> listarHabilidades(Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        List<Map<String, Object>> habilidades = serviceOH.findByOferente(oferente)
                .stream()
                .map(h -> Map.of(
                        "id", (Object) h.getId(),
                        "caracteristicaId",h.getCaracteristica().getId(),
                        "caracteristica", h.getCaracteristica().getNombre(),
                        "rutaCompleta", serviceC.buildRutaString(h.getCaracteristica()),
                        "nivel", h.getNivel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(habilidades);
    }

    @PostMapping("/habilidades")
    public ResponseEntity<?> agregarHabilidad(@RequestBody AgregarHabilidadDTO dto, Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        if (dto.getNivel() == null || dto.getNivel() < 1 || dto.getNivel() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "El nivel debe estar entre 1 y 5"));

        Caracteristica c = serviceC.findById(dto.getCaracteristicaId());
        if (c == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Característica no encontrada"));

        serviceOH.agregarOActualizar(oferente, c, dto.getNivel());
        return ResponseEntity.ok(Map.of("mensaje", "Habilidad registrada/actualizada correctamente"));
    }


    @DeleteMapping("/habilidades/{id}")
    public ResponseEntity<?> eliminarHabilidad(@PathVariable Integer id, Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        serviceOH.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Habilidad eliminada correctamente"));
    }

    @GetMapping("/cv")
    public ResponseEntity<?> verCV(Authentication auth) {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        return ResponseEntity.ok(Map.of(
                "rutaCurriculum", oferente.getRutaCurriculum() != null ? oferente.getRutaCurriculum() : ""
        ));
    }

    @PutMapping("/cv")
    public ResponseEntity<?> actualizarCV(@RequestBody ActualizarCVDTO dto, Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        String url = dto.getRutaCurriculum();
        if (url == null || url.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "El link no puede estar vacío"));

        boolean esDrive = url.contains("drive.google.com") || url.contains("docs.google.com");
        boolean esOneDrive = url.contains("onedrive.live.com") || url.contains("1drv.ms") || url.contains("sharepoint.com");

        if (!esDrive && !esOneDrive)
            return ResponseEntity.badRequest().body(Map.of("error", "El link debe ser de Google Drive o OneDrive"));

        oferente.setRutaCurriculum(url.trim());
        serviceO.actualizarOferente(oferente);
        return ResponseEntity.ok(Map.of("mensaje", "CV actualizado correctamente"));
    }


    @DeleteMapping("/cv")
    public ResponseEntity<?> eliminarCV(Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        oferente.setRutaCurriculum(null);
        serviceO.actualizarOferente(oferente);
        return ResponseEntity.ok(Map.of("mensaje", "CV eliminado correctamente"));
    }

    @GetMapping("/puestos")
    public ResponseEntity<?> verPuestosDisponibles(Authentication auth) {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        List<Map<String, Object>> puestos = serviceP.findAllActivos()
                .stream()
                .map(p -> toDTOConYaPostulado(p, oferente))
                .collect(Collectors.toList());

        return ResponseEntity.ok(puestos);
    }

    @PostMapping("/postulaciones")
    public ResponseEntity<?> postular(@RequestBody Map<String, Integer> body, Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        Integer puestoId = body.get("puestoId");
        if (puestoId == null)
            return ResponseEntity.badRequest().body(Map.of("error", "puestoId es requerido"));

        Puesto puesto = serviceP.findById(puestoId).orElse(null);
        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        if (!puesto.getActivo())
            return ResponseEntity.badRequest().body(Map.of("error", "El puesto no está activo"));

        if (servicePO.yaPostulado(oferente, puesto))
            return ResponseEntity.badRequest().body(Map.of("error", "Ya te postulaste a este puesto anteriormente"));

        servicePO.postular(oferente, puesto);
        return ResponseEntity.ok(Map.of("mensaje", "Postulación registrada correctamente"));
    }

    @GetMapping("/postulaciones")
    public ResponseEntity<?> misPostulaciones(Authentication auth)
    {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        List<Map<String, Object>> postulaciones = servicePO.findByOferente(oferente)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "puestoNombre", p.getPuesto().getNombre(),
                        "empresaNombre", p.getPuesto().getEmpresa().getNombre(),
                        "salario", p.getPuesto().getSalario(),
                        "moneda", p.getPuesto().getMoneda(),
                        "fechaPostulacion",p.getFechaPostulacion().toString(),
                        "estado", p.getEstado()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postulaciones);
    }

    @PostMapping("/puestos/buscar")
    public ResponseEntity<?> buscarPuestos(@RequestBody Map<String, Object> body, Authentication auth) {
        Oferente oferente = getOferente(auth);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "Oferente no encontrado"));

        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("caracteristicaIds");
        String moneda = (String) body.get("moneda");

        if (ids == null || ids.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Debe seleccionar al menos una característica"));

        List<Map<String, Object>> resultados = serviceP.buscarPuestosParaOferente(ids, moneda)
                .stream()
                .map(p -> toDTOConYaPostulado(p, oferente))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultados);
    }

    private Map<String, Object> toDTOConYaPostulado(Puesto p, Oferente oferente) {
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

        Map<String, Object> dto = new java.util.LinkedHashMap<>();
        dto.put("id", p.getId());
        dto.put("nombre", p.getNombre());
        dto.put("descripcion", p.getDescripcion());
        dto.put("salario", p.getSalario());
        dto.put("moneda", p.getMoneda());
        dto.put("esPublico", p.getEsPublico());
        dto.put("empresaNombre", p.getEmpresa().getNombre());
        dto.put("fechaRegistro", p.getFechaRegistro().toString());
        dto.put("yaPostulado", servicePO.yaPostulado(oferente, p));
        dto.put("requisitos", requisitos);

        return dto;
    }
}

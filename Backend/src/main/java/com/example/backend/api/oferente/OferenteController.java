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

    private Oferente getOferenteAutenticado(HttpSession session) {
        Object u = session.getAttribute("usuario");
        if (u instanceof Oferente) return (Oferente) u;
        return null;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        return ResponseEntity.ok(Map.of(
                "nombre", oferente.getNombre(),
                "primerApellido", oferente.getPrimerApellido(),
                "correo", oferente.getCorreo(),
                "identificacion", oferente.getIdentificacion(),
                "telefono", oferente.getTelefono(),
                "lugarResidencia", oferente.getLugarResidencia(),
                "rutaCurriculum", oferente.getRutaCurriculum() != null ? oferente.getRutaCurriculum() : ""
        ));
    }

    @GetMapping("/habilidades")
    public ResponseEntity<?> listarHabilidades(HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        List<Map<String, Object>> habilidades = serviceOH.findByOferente(oferente)
                .stream()
                .map(h -> Map.of(
                        "id", (Object) h.getId(),
                        "caracteristicaId", h.getCaracteristica().getId(),
                        "caracteristica", h.getCaracteristica().getNombre(),
                        "rutaCompleta", serviceC.buildRutaString(h.getCaracteristica()),
                        "nivel", h.getNivel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(habilidades);
    }

    @PostMapping("/habilidades")
    public ResponseEntity<?> agregarHabilidad(@RequestBody AgregarHabilidadDTO dto, HttpSession session)
    {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        if (dto.getNivel() == null || dto.getNivel() < 1 || dto.getNivel() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "El nivel debe estar entre 1 y 5"));

        Caracteristica c = serviceC.findById(dto.getCaracteristicaId());
        if (c == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Característica no encontrada"));

        serviceOH.agregarOActualizar(oferente, c, dto.getNivel());
        return ResponseEntity.ok(Map.of("mensaje", "Habilidad registrada correctamente"));
    }

    @DeleteMapping("/habilidades/{id}")
    public ResponseEntity<?> eliminarHabilidad(@PathVariable Integer id, HttpSession session)
    {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        serviceOH.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Habilidad eliminada"));
    }

    @PutMapping("/cv")
    public ResponseEntity<?> actualizarCV(@RequestBody ActualizarCVDTO dto, HttpSession session)
    {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        String url = dto.getRutaCurriculum();
        if (url == null || url.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "El link no puede estar vacío"));

        boolean esDrive = url.contains("drive.google.com") || url.contains("docs.google.com");
        boolean esOneDrive = url.contains("onedrive.live.com") || url.contains("1drv.ms") || url.contains("sharepoint.com");

        if (!esDrive && !esOneDrive)
            return ResponseEntity.badRequest().body(Map.of("error", "El link debe ser de Google Drive o OneDrive"));

        oferente.setRutaCurriculum(url);
        serviceO.actualizarOferente(oferente);
        session.setAttribute("usuario", oferente);
        return ResponseEntity.ok(Map.of("mensaje", "CV actualizado correctamente"));
    }

    @DeleteMapping("/cv")
    public ResponseEntity<?> eliminarCV(HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        oferente.setRutaCurriculum(null);
        serviceO.actualizarOferente(oferente);
        session.setAttribute("usuario", oferente);
        return ResponseEntity.ok(Map.of("mensaje", "CV eliminado"));
    }

    @GetMapping("/puestos")
    public ResponseEntity<?> verPuestosDisponibles(HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        List<Map<String, Object>> puestos = serviceP.findAllActivos()
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "nombre", p.getNombre(),
                        "descripcion", p.getDescripcion(),
                        "salario", p.getSalario(),
                        "moneda", p.getMoneda(),
                        "esPublico", p.getEsPublico(),
                        "empresaNombre", p.getEmpresa().getNombre(),
                        "fechaRegistro", p.getFechaRegistro().toString(),
                        "yaPostulado", servicePO.yaPostulado(oferente, p)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(puestos);
    }

    @PostMapping("/postulaciones")
    public ResponseEntity<?> postular(@RequestBody Map<String, Integer> body, HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        Integer puestoId = body.get("puestoId");
        Puesto puesto = serviceP.findById(puestoId).orElse(null);
        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        if (servicePO.yaPostulado(oferente, puesto))
            return ResponseEntity.badRequest().body(Map.of("error", "Ya te postulaste a este puesto"));

        servicePO.postular(oferente, puesto);
        return ResponseEntity.ok(Map.of("mensaje", "Postulación registrada correctamente"));
    }

    @GetMapping("/postulaciones")
    public ResponseEntity<?> misPostulaciones(HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        List<Map<String, Object>> postulaciones = servicePO.findByOferente(oferente)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "puestoNombre", p.getPuesto().getNombre(),
                        "empresaNombre", p.getPuesto().getEmpresa().getNombre(),
                        "salario", p.getPuesto().getSalario(),
                        "moneda", p.getPuesto().getMoneda(),
                        "fechaPostulacion", p.getFechaPostulacion().toString(),
                        "estado", p.getEstado()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postulaciones);
    }

    @PostMapping("/puestos/buscar")
    public ResponseEntity<?> buscarPuestos(@RequestBody Map<String, Object> body, HttpSession session) {
        Oferente oferente = getOferenteAutenticado(session);
        if (oferente == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("caracteristicaIds");
        String moneda = (String) body.get("moneda");

        List<Map<String, Object>> resultados = serviceP.buscarPuestosParaOferente(ids, moneda)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "nombre", p.getNombre(),
                        "salario", p.getSalario(),
                        "moneda", p.getMoneda(),
                        "empresaNombre", p.getEmpresa().getNombre(),
                        "yaPostulado", servicePO.yaPostulado(oferente, p)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultados);
    }
}

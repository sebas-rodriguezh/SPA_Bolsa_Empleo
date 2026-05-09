package com.example.backend.api.empresa;

import com.example.backend.dto.empresa.*;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.empresa.ServiceE;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.logic.oferenteHabilidad.OferenteHabilidad;
import com.example.backend.logic.oferenteHabilidad.ServiceOH;
import com.example.backend.logic.postulacion.Postulacion;
import com.example.backend.logic.postulacion.ServicePO;
import com.example.backend.logic.puesto.CandidatoResultado;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import com.example.backend.logic.puestoCaracteristica.PuestoCaracteristica;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {

    @Autowired private ServiceE serviceE;
    @Autowired private ServiceP serviceP;
    @Autowired private ServiceC serviceC;
    @Autowired private ServiceO serviceO;
    @Autowired private ServiceOH serviceOH;
    @Autowired private ServicePO servicePO;

    // ─── MÉTODO AUXILIAR ────────────────────────────────────────────────────────
    // Temporal: mientras no hay JWT, la empresa se identifica por sesión.
    // Cuando implementen JWT, este método se reemplaza por leer el token.
    private Empresa getEmpresaAutenticada(HttpSession session) {
        Object u = session.getAttribute("usuario");
        if (u instanceof Empresa) return (Empresa) u;
        return null;
    }

    private Empresa getEmpresa(Authentication auth) {
        return serviceE.findByCorreo(auth.getName());
    }


    // ─── DASHBOARD ──────────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/dashboard
     * Devuelve información básica de la empresa logueada.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        return ResponseEntity.ok(Map.of(
                "nombre", empresa.getNombre(),
                "correo", empresa.getCorreo(),
                "localizacion", empresa.getLocalizacion(),
                "telefono", empresa.getTelefono()
        ));
    }

    // ─── PUESTOS ─────────────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/puestos
     * Lista todos los puestos de la empresa autenticada.
     */
    @GetMapping("/puestos")
    public ResponseEntity<?> listarPuestos(Authentication authentication) {
        Empresa empresa = getEmpresa(authentication);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        List<PuestoResponseDTO> puestos = serviceP.findByEmpresa(empresa)
                .stream()
                .map(p -> new PuestoResponseDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getSalario(),
                        p.getMoneda(),
                        p.getEsPublico(),
                        p.getActivo(),
                        p.getFechaRegistro().toString(),
                        p.getEmpresa().getNombre()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(puestos);
    }

    /**
     * POST /api/empresa/puestos
     * Crea un nuevo puesto para la empresa autenticada.
     * Body: { nombre, descripcion, salario, esPublico, moneda }
     */
    @PostMapping("/puestos")
    public ResponseEntity<?> crearPuesto(@RequestBody NuevoPuestoDTO dto, HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        // Validaciones (misma lógica que el controller SSR)
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre del puesto no puede estar vacío"));

        if (dto.getDescripcion() == null || dto.getDescripcion().trim().length() < 10)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La descripción debe tener al menos 10 caracteres"));

        if (dto.getSalario() == null || dto.getSalario() <= 0)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El salario debe ser mayor a 0"));

        if (dto.getEsPublico() == null)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe indicar si el puesto es público o privado"));

        Puesto puesto = serviceP.crearPuesto(
                empresa,
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getSalario(),
                dto.getEsPublico(),
                dto.getMoneda() != null ? dto.getMoneda() : "CRC"
        );

        return ResponseEntity.status(201).body(Map.of(
                "id", puesto.getId(),
                "mensaje", "Puesto creado correctamente"
        ));
    }

    /**
     * POST /api/empresa/puestos/{id}/desactivar
     * Desactiva un puesto de la empresa autenticada.
     */
    @PostMapping("/puestos/{id}/desactivar")
    public ResponseEntity<?> desactivarPuesto(@PathVariable Integer id,
                                              HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        boolean encontrado = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .map(p -> { serviceP.desactivarPuesto(p.getId()); return true; })
                .orElse(false);

        if (!encontrado)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado o no pertenece a esta empresa"));

        return ResponseEntity.ok(Map.of("mensaje", "Puesto desactivado correctamente"));
    }

    // ─── REQUISITOS ──────────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/puestos/{id}/requisitos
     * Devuelve los requisitos (características) de un puesto.
     */
    @GetMapping("/puestos/{id}/requisitos")
    public ResponseEntity<?> listarRequisitos(@PathVariable Integer id,
                                              HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        Puesto puesto = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        List<Map<String, Object>> requisitos = serviceP.findRequisitosByPuesto(puesto)
                .stream()
                .map(r -> Map.of(
                        "id", (Object) r.getId(),
                        "caracteristicaId", r.getCaracteristica().getId(),
                        "caracteristicaNombre", r.getCaracteristica().getNombre(),
                        "rutaCompleta", serviceC.buildRutaString(r.getCaracteristica()),
                        "nivel", r.getNivel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(requisitos);
    }

    /**
     * POST /api/empresa/puestos/{id}/requisitos
     * Agrega o actualiza un requisito al puesto.
     * Body: { caracteristicaId, nivel }
     */
    @PostMapping("/puestos/{id}/requisitos")
    public ResponseEntity<?> agregarRequisito(@PathVariable Integer id,
                                              @RequestBody AgregarRequisitoDTO dto,
                                              HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        Puesto puesto = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        if (dto.getNivel() == null || dto.getNivel() < 1 || dto.getNivel() > 5)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nivel debe estar entre 1 y 5"));

        Caracteristica c = serviceC.findById(dto.getCaracteristicaId());
        if (c == null)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Característica no encontrada"));

        serviceP.agregarOActualizarRequisito(puesto, c, dto.getNivel());
        return ResponseEntity.ok(Map.of("mensaje", "Requisito agregado correctamente"));
    }

    /**
     * DELETE /api/empresa/puestos/{puestoId}/requisitos/{pcId}
     * Elimina un requisito de un puesto.
     */
    @DeleteMapping("/puestos/{puestoId}/requisitos/{pcId}")
    public ResponseEntity<?> quitarRequisito(@PathVariable Integer puestoId,
                                             @PathVariable Integer pcId,
                                             HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        // Verificar que el puesto pertenece a la empresa
        boolean pertenece = serviceP.findById(puestoId)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .isPresent();

        if (!pertenece)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        serviceP.quitarRequisito(pcId);
        return ResponseEntity.ok(Map.of("mensaje", "Requisito eliminado"));
    }

    // ─── CANDIDATOS ──────────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/puestos/{id}/candidatos?modo=parcial|completo
     * Busca candidatos cuyas habilidades coincidan con los requisitos del puesto.
     */
    @GetMapping("/puestos/{id}/candidatos")
    public ResponseEntity<?> buscarCandidatos(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "parcial") String modo,
            HttpSession session) {

        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        Puesto puesto = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        boolean soloCompletos = "completo".equals(modo);
        List<CandidatoResponseDTO> candidatos = serviceP
                .buscarCandidatos(puesto, soloCompletos)
                .stream()
                .map(c -> new CandidatoResponseDTO(
                        c.getOferente().getId(),
                        c.getOferente().getNombre(),
                        c.getOferente().getPrimerApellido(),
                        c.getOferente().getCorreo(),
                        c.getOferente().getTelefono(),
                        c.getOferente().getLugarResidencia(),
                        c.getOferente().getIdentificacion(),
                        c.getCumplidos(),
                        c.getTotal(),
                        c.getPorcentaje(),
                        c.getOferente().getRutaCurriculum()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(candidatos);
    }

    /**
     * GET /api/empresa/candidatos/{oferenteId}?puestoId=X
     * Ver el detalle completo de un candidato.
     */
    @GetMapping("/candidatos/{oferenteId}")
    public ResponseEntity<?> verDetalleCandidato(
            @PathVariable Integer oferenteId,
            @RequestParam Integer puestoId,
            HttpSession session) {

        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        boolean puestoValido = serviceP.findById(puestoId)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .isPresent();

        if (!puestoValido)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        Oferente oferente = serviceO.findById(oferenteId);
        if (oferente == null)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Oferente no encontrado"));

        List<Map<String, Object>> habilidades = serviceOH.findByOferente(oferente)
                .stream()
                .map(h -> Map.of(
                        "id", (Object) h.getId(),
                        "caracteristica", h.getCaracteristica().getNombre(),
                        "nivel", h.getNivel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "id", oferente.getId(),
                "nombre", oferente.getNombre(),
                "primerApellido", oferente.getPrimerApellido(),
                "identificacion", oferente.getIdentificacion(),
                "correo", oferente.getCorreo(),
                "telefono", oferente.getTelefono(),
                "lugarResidencia", oferente.getLugarResidencia(),
                "rutaCurriculum", oferente.getRutaCurriculum() != null ? oferente.getRutaCurriculum() : "",
                "habilidades", habilidades
        ));
    }

    // ─── POSTULACIONES ───────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/puestos/{id}/postulaciones
     * Lista todas las postulaciones recibidas para un puesto.
     */
    @GetMapping("/puestos/{id}/postulaciones")
    public ResponseEntity<?> verPostulaciones(@PathVariable Integer id,
                                              HttpSession session) {
        Empresa empresa = getEmpresaAutenticada(session);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));

        Puesto puesto = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Puesto no encontrado"));

        List<Map<String, Object>> postulaciones = servicePO.findByPuesto(puesto)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "oferenteNombre", p.getOferente().getNombre() + " " + p.getOferente().getPrimerApellido(),
                        "oferenteCorreo", p.getOferente().getCorreo(),
                        "oferenteTelefono", p.getOferente().getTelefono(),
                        "identificacion", p.getOferente().getIdentificacion(),
                        "fechaPostulacion", p.getFechaPostulacion().toString(),
                        "estado", p.getEstado(),
                        "rutaCurriculum", p.getOferente().getRutaCurriculum() != null
                                ? p.getOferente().getRutaCurriculum() : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postulaciones);
    }

    // ─── REPORTES ────────────────────────────────────────────────────────────────

    /**
     * GET /api/empresa/reportes?puestoId=X
     * Reporte de postulaciones por puesto.
     */
//    @GetMapping("/reportes")
//    public ResponseEntity<?> reportes(@RequestParam(required = false) Integer puestoId,
//                                      HttpSession session) {
//        Empresa empresa = getEmpresaAutenticada(session);
//        if (empresa == null)
//            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
//
//        List<Map<String, Object>> puestos = serviceP.findByEmpresa(empresa)
//                .stream()
//                .map(p -> Map.of(
//                        "id", (Object) p.getId(),
//                        "nombre", p.getNombre(),
//                        "activo", p.getActivo()
//                ))
//                .collect(Collectors.toList());
//
//        if (puestoId == null)
//            return ResponseEntity.ok(Map.of("puestos", puestos));
//
//        Puesto puestoSeleccionado = serviceP.findById(puestoId)
//                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
//                .orElse(null);
//
//        if (puestoSeleccionado == null)
//            return ResponseEntity.status(404)
//                    .body(Map.of("error", "Puesto no encontrado"));
//
//        List<Map<String, Object>> postulaciones = servicePO
//                .findByPuesto(puestoSeleccionado)
//                .stream()
//                .map(p -> Map.of(
//                        "nombre", p.getOferente().getNombre() + " " + p.getOferente().getPrimerApellido(),
//                        "identificacion", p.getOferente().getIdentificacion(),
//                        "correo", p.getOferente().getCorreo(),
//                        "telefono", p.getOferente().getTelefono(),
//                        "residencia", p.getOferente().getLugarResidencia(),
//                        "fechaPostulacion", p.getFechaPostulacion().toString(),
//                        "estado", p.getEstado()
//                ))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(Map.of(
//                "puestos", puestos,
//                "puesto", Map.of(
//                        "id", puestoSeleccionado.getId(),
//                        "nombre", puestoSeleccionado.getNombre(),
//                        "salario", puestoSeleccionado.getSalario(),
//                        "moneda", puestoSeleccionado.getMoneda(),
//                        "totalPostulantes", postulaciones.size()
//                ),
//                "postulaciones", postulaciones
//        ));
//    }
}
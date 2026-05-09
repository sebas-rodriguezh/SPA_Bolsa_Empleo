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

    private Empresa getEmpresa(Authentication auth) {
        return serviceE.findByCorreo(auth.getName());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Authentication auth) {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        return ResponseEntity.ok(Map.of(
                "id", empresa.getId(),
                "nombre", empresa.getNombre(),
                "correo", empresa.getCorreo(),
                "localizacion",empresa.getLocalizacion(),
                "telefono", empresa.getTelefono(),
                "descripcion", empresa.getDescripcion()
        ));
    }

    @GetMapping("/puestos")
    public ResponseEntity<?> listarPuestos(Authentication auth) {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

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
                        p.getEmpresa().getNombre())
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(puestos);
    }

    @PostMapping("/puestos")
    public ResponseEntity<?> crearPuesto(@RequestBody NuevoPuestoDTO dto, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        if (dto.getNombre() == null || dto.getNombre().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre del puesto no puede estar vacío"));

        if (dto.getDescripcion() == null || dto.getDescripcion().trim().length() < 10)
            return ResponseEntity.badRequest().body(Map.of("error", "La descripción debe tener al menos 10 caracteres"));

        if (dto.getSalario() == null || dto.getSalario() <= 0)
            return ResponseEntity.badRequest().body(Map.of("error", "El salario debe ser mayor a 0"));

        if (dto.getEsPublico() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Debe indicar si el puesto es público o privado"));

        Puesto puesto = serviceP.crearPuesto(empresa, dto.getNombre(), dto.getDescripcion(), dto.getSalario(), dto.getEsPublico(), dto.getMoneda() != null ? dto.getMoneda() : "CRC");

        return ResponseEntity.status(201).body(Map.of(
                "id", puesto.getId(),
                "mensaje", "Puesto creado. Ahora agregue las características requeridas."
        ));
    }

    @PostMapping("/puestos/{id}/desactivar")
    public ResponseEntity<?> desactivarPuesto(@PathVariable Integer id, Authentication auth) {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        boolean ok = serviceP.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .map(p -> { serviceP.desactivarPuesto(p.getId()); return true; })
                .orElse(false);

        if (!ok)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado o no pertenece a esta empresa"));

        return ResponseEntity.ok(Map.of("mensaje", "Puesto desactivado correctamente"));
    }

    @GetMapping("/puestos/{id}/requisitos")
    public ResponseEntity<?> listarRequisitos(@PathVariable Integer id, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        Puesto puesto = serviceP.findById(id).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        List<Map<String, Object>> requisitos = serviceP.findRequisitosByPuesto(puesto)
                .stream()
                .map(r -> Map.of(
                        "id", (Object) r.getId(),
                        "caracteristicaId", r.getCaracteristica().getId(),
                        "caracteristicaNombre",r.getCaracteristica().getNombre(),
                        "rutaCompleta", serviceC.buildRutaString(r.getCaracteristica()),
                        "nivel", r.getNivel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(requisitos);
    }

    @PostMapping("/puestos/{id}/requisitos")
    public ResponseEntity<?> agregarRequisito(@PathVariable Integer id, @RequestBody AgregarRequisitoDTO dto, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        Puesto puesto = serviceP.findById(id).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        if (dto.getNivel() == null || dto.getNivel() < 1 || dto.getNivel() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "El nivel debe estar entre 1 y 5"));

        Caracteristica c = serviceC.findById(dto.getCaracteristicaId());
        if (c == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Característica no encontrada"));

        serviceP.agregarOActualizarRequisito(puesto, c, dto.getNivel());
        return ResponseEntity.ok(Map.of("mensaje", "Requisito agregado/actualizado correctamente"));
    }

    @DeleteMapping("/puestos/{puestoId}/requisitos/{pcId}")
    public ResponseEntity<?> quitarRequisito(@PathVariable Integer puestoId, @PathVariable Integer pcId, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        boolean pertenece = serviceP.findById(puestoId).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).isPresent();

        if (!pertenece)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        serviceP.quitarRequisito(pcId);
        return ResponseEntity.ok(Map.of("mensaje", "Requisito eliminado correctamente"));
    }

    @GetMapping("/puestos/{id}/candidatos")
    public ResponseEntity<?> buscarCandidatos(@PathVariable Integer id, @RequestParam(defaultValue = "parcial") String modo, Authentication auth)
    {

        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        Puesto puesto = serviceP.findById(id).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

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
                        c.getOferente().getRutaCurriculum()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(candidatos);
    }

    @GetMapping("/candidatos/{oferenteId}")
    public ResponseEntity<?> verDetalleCandidato(@PathVariable Integer oferenteId, @RequestParam Integer puestoId, Authentication auth)
    {

        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        boolean puestoValido = serviceP.findById(puestoId).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).isPresent();

        if (!puestoValido)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        Oferente oferente = serviceO.findById(oferenteId);
        if (oferente == null)
            return ResponseEntity.status(404).body(Map.of("error", "Oferente no encontrado"));

        List<Map<String, Object>> habilidades = serviceOH.findByOferente(oferente)
                .stream()
                .map(h -> Map.of(
                        "id", (Object) h.getId(),
                        "caracteristica",h.getCaracteristica().getNombre(),
                        "rutaCompleta", serviceC.buildRutaString(h.getCaracteristica()),
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

    @GetMapping("/puestos/{id}/postulaciones")
    public ResponseEntity<?> verPostulaciones(@PathVariable Integer id, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        Puesto puesto = serviceP.findById(id).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).orElse(null);

        if (puesto == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        List<Map<String, Object>> postulaciones = servicePO.findByPuesto(puesto)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "oferenteNombre",p.getOferente().getNombre() + " " + p.getOferente().getPrimerApellido(),
                        "oferenteCorreo", p.getOferente().getCorreo(),
                        "oferenteTelefono", p.getOferente().getTelefono(),
                        "identificacion", p.getOferente().getIdentificacion(),
                        "lugarResidencia", p.getOferente().getLugarResidencia(),
                        "fechaPostulacion", p.getFechaPostulacion().toString(),
                        "estado", p.getEstado(),
                        "rutaCurriculum", p.getOferente().getRutaCurriculum() != null ? p.getOferente().getRutaCurriculum() : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "puestoId", puesto.getId(),
                "puestoNombre", puesto.getNombre(),
                "total", postulaciones.size(),
                "postulaciones", postulaciones
        ));
    }


    @GetMapping("/reportes")
    public ResponseEntity<?> reportesEmpresa(@RequestParam(required = false) Integer puestoId, Authentication auth)
    {
        Empresa empresa = getEmpresa(auth);
        if (empresa == null)
            return ResponseEntity.status(401).body(Map.of("error", "Empresa no encontrada"));

        List<Map<String, Object>> puestos = serviceP.findByEmpresa(empresa)
                .stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "nombre", p.getNombre(),
                        "activo", p.getActivo()
                ))
                .collect(Collectors.toList());

        if (puestoId == null)
            return ResponseEntity.ok(Map.of("puestos", puestos));

        Puesto sel = serviceP.findById(puestoId).filter(p -> p.getEmpresa().getId().equals(empresa.getId())).orElse(null);

        if (sel == null)
            return ResponseEntity.status(404).body(Map.of("error", "Puesto no encontrado"));

        List<Map<String, Object>> postulaciones = servicePO.findByPuesto(sel)
                .stream()
                .map(p -> Map.<String, Object>of(
                        "nombre",p.getOferente().getNombre() + " " + p.getOferente().getPrimerApellido(),
                        "identificacion", p.getOferente().getIdentificacion(),
                        "correo", p.getOferente().getCorreo(),
                        "telefono", p.getOferente().getTelefono(),
                        "residencia", p.getOferente().getLugarResidencia(),
                        "fechaPostulacion",p.getFechaPostulacion().toString(),
                        "estado", p.getEstado()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "puestos", puestos,
                "puesto", Map.of(
                        "id", sel.getId(),
                        "nombre", sel.getNombre(),
                        "salario", sel.getSalario(),
                        "moneda", sel.getMoneda(),
                        "esPublico", sel.getEsPublico(),
                        "totalPostulantes",postulaciones.size()
                ),
                "postulaciones", postulaciones
        ));
    }
}
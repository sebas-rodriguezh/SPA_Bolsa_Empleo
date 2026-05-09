package com.example.backend.api.admin;

import com.example.backend.dto.admin.CrearCaracteristicaDTO;
import com.example.backend.logic.administrador.Administrador;
import com.example.backend.logic.administrador.ServiceA;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.empresa.ServiceE;
import com.example.backend.logic.oferente.ServiceO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ServiceA serviceA;
    @Autowired private ServiceC serviceC;
    @Autowired private ServiceE serviceE;
    @Autowired private ServiceO serviceO;

    private boolean esAdmin(HttpSession session) {
        return session.getAttribute("usuario") instanceof Administrador;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "correo", auth.getName(),
                "rol",    "ADMIN"
        ));
    }

    @GetMapping("/empresas/pendientes")
    public ResponseEntity<?> empresasPendientes() {
        List<Map<String, Object>> empresas = serviceE.findPendientes()
                .stream()
                .map(e -> Map.of(
                        "id", (Object) e.getId(),
                        "nombre", e.getNombre(),
                        "correo", e.getCorreo(),
                        "telefono", e.getTelefono(),
                        "localizacion",e.getLocalizacion(),
                        "descripcion", e.getDescripcion()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(empresas);
    }

    @PostMapping("/empresas/{id}/aprobar")
    public ResponseEntity<?> aprobarEmpresa(@PathVariable Integer id) {
        serviceE.aprobarEmpresa(id);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa aprobada correctamente"));
    }

    @GetMapping("/oferentes/pendientes")
    public ResponseEntity<?> oferentesPendientes() {
        List<Map<String, Object>> oferentes = serviceO.findPendientes()
                .stream()
                .map(o -> Map.of(
                        "id", (Object) o.getId(),
                        "nombre",o.getNombre() + " " + o.getPrimerApellido(),
                        "correo", o.getCorreo(),
                        "identificacion",o.getIdentificacion(),
                        "telefono", o.getTelefono(),
                        "lugarResidencia",o.getLugarResidencia(),
                        "nacionalidad", o.getNacionalidad()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(oferentes);
    }

    @PostMapping("/oferentes/{id}/aprobar")
    public ResponseEntity<?> aprobarOferente(@PathVariable Integer id) {
        serviceO.aprobarOferente(id);
        return ResponseEntity.ok(Map.of("mensaje", "Oferente aprobado correctamente"));
    }

    @GetMapping("/caracteristicas")
    public ResponseEntity<?> listarCaracteristicas() {
        List<Map<String, Object>> arbol = serviceC.getArbolOrdenado()
                .stream()
                .map(c -> Map.of(
                        "id", (Object) c.getId(),
                        "nombre", c.getNombre(),
                        "padreId", c.getPadre() != null ? c.getPadre().getId() : "",
                        "rutaCompleta",serviceC.buildRutaString(c)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(arbol);
    }

    @PostMapping("/caracteristicas")
    public ResponseEntity<?> crearCaracteristica(@RequestBody CrearCaracteristicaDTO dto) {
        try
        {
            serviceC.crearCaracteristica(dto.getNombre(), dto.getPadreId());
            return ResponseEntity.status(201).body(Map.of("mensaje", "Característica creada correctamente"));
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reportes")
    public ResponseEntity<?> reportes(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer anio)
    {

        if (mes != null && anio != null)
        {
            if (mes < 1 || mes > 12) {
                return ResponseEntity.badRequest().body(Map.of("error", "El mes debe estar entre 1 y 12"));
            }

            Map<Integer, Long> conteos = serviceA.contarPostulacionesPorMes(mes, anio);

            List<Map<String, Object>> puestos = serviceA
                    .getPuestosSolicitadosPorMesYAnio(mes, anio)
                    .stream()
                    .map(p -> Map.of(
                            "id", (Object) p.getId(),
                            "nombre", p.getNombre(),
                            "empresaNombre",p.getEmpresa().getNombre(),
                            "salario", p.getSalario(),
                            "moneda", p.getMoneda(),
                            "esPublico", p.getEsPublico(),
                            "fechaRegistro",p.getFechaRegistro().toString(),
                            "postulaciones",conteos.getOrDefault(p.getId(), 0L)
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "mes",    mes,
                    "anio",   anio,
                    "total",  puestos.size(),
                    "puestos",puestos
            ));
        }

        Map<String, Object> reporte = new java.util.LinkedHashMap<>();
        Map<String, Map<Integer, Long>> conteosPorMes = serviceA.contarPostulacionesPorTodosMeses();

        serviceA.getPuestosSolicitadosPorMes().forEach((clave, lista) -> {
            Map<Integer, Long> conteosDelMes = conteosPorMes.getOrDefault(clave, Map.of());

            reporte.put(clave, lista.stream()
                    .map(p -> Map.of(
                            "id", (Object) p.getId(),
                            "nombre", p.getNombre(),
                            "empresaNombre",p.getEmpresa().getNombre(),
                            "postulaciones",conteosDelMes.getOrDefault(p.getId(), 0L)
                    ))
                    .collect(Collectors.toList()));
        });

        return ResponseEntity.ok(reporte);
    }
}
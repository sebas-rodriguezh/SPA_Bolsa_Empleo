package com.example.backend.logic.administrador;

import com.example.backend.data.*;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.postulacion.Postulacion;
import com.example.backend.logic.puesto.Puesto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceA {
    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private OferenteRepository oferenteRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    public Iterable<Administrador> administradorFindAll () {
        return administradorRepository.findAll();
    }

//    public Administrador findByCorreo(String correo) {
//        return AdministradorRepository.findByCorreo(correo);
//    }

    public Object findUserByEmailAndPassword(String correo, String clave) {
        Empresa empresa = empresaRepository.findByCorreo(correo);
        if (empresa != null && passwordEncoder.matches(clave, empresa.getClave())) {
            if (!empresa.getAutorizada()) {
                return "PENDIENTE";
            }
            return empresa;
        }

        Oferente oferente = oferenteRepository.findByCorreo(correo);
        if (oferente != null && passwordEncoder.matches(clave, oferente.getClave())) {
            if (!oferente.getAutorizado()) {
                return "PENDIENTE";
            }
            return oferente;
        }

        Administrador administrador = administradorRepository.findByCorreo(correo);
        if (administrador != null && passwordEncoder.matches(clave, administrador.getClave())) {
            return administrador;
        }
        return null;
    }

    public void registrarAdministrador(Administrador administrador) {
        administrador.setClave(passwordEncoder.encode(administrador.getClave()));
        administradorRepository.save(administrador);
    }

    // CASO B: agrupados por mes de postulación
    public Map<String, List<Puesto>> getPuestosSolicitadosPorMes() {
        List<Postulacion> todas = new ArrayList<>();
        postulacionRepository.findAll().forEach(todas::add);

        return todas.stream()
                .collect(Collectors.groupingBy(
                        p -> {
                            String clave = p.getFechaPostulacion()
                                    .format(DateTimeFormatter.ofPattern("MMMM yyyy",
                                            new Locale("es", "CR")));
                            return clave.substring(0,1).toUpperCase() + clave.substring(1);
                        },
                        Collectors.mapping(
                                Postulacion::getPuesto,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        lista -> lista.stream().distinct().collect(Collectors.toList())
                                )
                        )
                ));
    }

    // CASO A: mes/año específico
    public List<Puesto> getPuestosSolicitadosPorMesYAnio(int mes, int anio) {
        List<Postulacion> todas = new ArrayList<>();
        postulacionRepository.findAll().forEach(todas::add);

        return todas.stream()
                .filter(p -> p.getFechaPostulacion().getMonthValue() == mes
                        && p.getFechaPostulacion().getYear() == anio)
                .map(Postulacion::getPuesto)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> contarPostulacionesPorMes(int mes, int anio) {
        List<Postulacion> todas = new ArrayList<>();
        postulacionRepository.findAll().forEach(todas::add);

        return todas.stream()
                .filter(p -> p.getFechaPostulacion().getMonthValue() == mes
                        && p.getFechaPostulacion().getYear() == anio)
                .collect(Collectors.groupingBy(
                        p -> p.getPuesto().getId(),
                        Collectors.counting()
                ));
    }

    public Map<String, Map<Integer, Long>> contarPostulacionesPorTodosMeses() {
        List<Postulacion> todas = new ArrayList<>();
        postulacionRepository.findAll().forEach(todas::add);

        return todas.stream()
                .collect(Collectors.groupingBy(
                        p -> {
                            String clave = p.getFechaPostulacion()
                                    .format(DateTimeFormatter.ofPattern("MMMM yyyy",
                                            new Locale("es", "CR")));
                            return clave.substring(0,1).toUpperCase() + clave.substring(1);
                        },
                        Collectors.groupingBy(
                                p -> p.getPuesto().getId(),
                                Collectors.counting()
                        )
                ));
    }
}

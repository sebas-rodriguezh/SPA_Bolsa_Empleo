package com.example.backend.logic.puesto;

import com.example.backend.data.*;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.puestoCaracteristica.PuestoCaracteristica;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backend.logic.oferenteHabilidad.OferenteHabilidad;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Service
public class ServiceP {
    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    @Autowired
    private OferenteRepository oferenteRepository;

    @Autowired
    private OferenteHabilidadRepository oferenteHabilidadRepository;

    @Autowired
    private ServiceC serviceC;

    public Iterable<Puesto> puestosFindAll () {
        return puestoRepository.findAll();
    }

    public List<Puesto> findByEmpresa(Empresa empresa) {
        return puestoRepository.findByEmpresa(empresa);
    }

    public Optional<Puesto> findById(Integer id) {
        return puestoRepository.findById(id);
    }

//    public List<Puesto> getUltimosPuestosPublicos() {
//        return puestoRepository.findTop5ByEsPublicoTrueAndActivoTrueOrderByFechaRegistroDesc();
//    }

    public List<Puesto> getUltimosPuestosPublicos() {
        return puestoRepository.findTop5ByEsPublicoTrueAndActivoTrueOrderByFechaRegistroDescIdDesc();
    }


    public List<Puesto> findAllActivos() {
        return puestoRepository.findByActivoTrueOrderByFechaRegistroDescIdDesc();
    }


    //Cambio
    public Puesto crearPuesto(Empresa empresa, String nombre, String descripcion, Double salario, Boolean esPublico, String moneda)
    {
        Puesto p = new Puesto();
        p.setEmpresa(empresa);
        p.setNombre(nombre.trim());
        p.setDescripcion(descripcion.trim());
        p.setSalario(salario);
        p.setEsPublico(esPublico);
        p.setMoneda(moneda != null ? moneda : "CRC");
        p.setActivo(true);
        p.setFechaRegistro(LocalDate.now());
        return puestoRepository.save(p);
    }
    //actualizar puesto existente
    // Nuevo método para actualizar un puesto existente
    public Puesto actualizarPuesto(Puesto puesto, String nombre, String descripcion, Double salario, Boolean esPublico, String moneda) {
        puesto.setNombre(nombre.trim());
        puesto.setDescripcion(descripcion.trim());
        puesto.setSalario(salario);
        puesto.setEsPublico(esPublico);
        puesto.setMoneda(moneda != null ? moneda : "CRC");

        return puestoRepository.save(puesto);
    }
    //Cambio
    public List<Puesto> buscarPuestosPublicos(List<Integer> caracteristicaIds, String moneda)
    {
        if (caracteristicaIds == null || caracteristicaIds.isEmpty())
            return new ArrayList<>();

        List<Integer> expandidos = serviceC.expandirConDescendientes(caracteristicaIds);
        List<Puesto> resultados = puestoRepository.findDistinctByEsPublicoTrueAndActivoTrueAndRequisitosCaracteristicaIdIn(expandidos);

        if (moneda == null || moneda.isBlank()) return resultados;

        return resultados.stream().filter(p -> p.getMoneda().equals(moneda)).collect(toList());
    }


    //Cambio
    public List<Puesto> buscarPuestosParaOferente(List<Integer> caracteristicaIds, String moneda, Oferente oferente) {
        if (caracteristicaIds == null || caracteristicaIds.isEmpty())
            return new ArrayList<>();

        List<Integer> expandidos = serviceC.expandirConDescendientes(caracteristicaIds);

        List<OferenteHabilidad> habilidades = oferenteHabilidadRepository.findByOferente(oferente);
        Map<Integer, Integer> nivelPorCaracteristica = habilidades.stream()
                .collect(Collectors.toMap(
                        h -> h.getCaracteristica().getId(),
                        OferenteHabilidad::getNivel
                ));

        List<Puesto> candidatos = puestoRepository.findDistinctByActivoTrueAndRequisitosCaracteristicaIdIn(expandidos);

        List<Puesto> resultados = candidatos.stream()
                .filter(puesto -> {
                    List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuesto(puesto);
                    return requisitos.stream()
                            .filter(req -> expandidos.contains(req.getCaracteristica().getId()))
                            .anyMatch(req -> {
                                Integer nivelOferente = nivelPorCaracteristica.get(req.getCaracteristica().getId());
                                return nivelOferente != null && nivelOferente >= req.getNivel();
                            });
                })
                .collect(Collectors.toList());

        if (moneda == null || moneda.isBlank()) return resultados;

        return candidatos.stream().filter(p -> p.getMoneda().equals(moneda)).collect(toList());
    }


     //Cambio
    public void desactivarPuesto(Integer id)
    {
        puestoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            puestoRepository.save(p);
        });
    }

    public List<PuestoCaracteristica> findRequisitosByPuesto(Puesto puesto) {
        return puestoCaracteristicaRepository.findByPuesto(puesto);
    }


    //Cambio
    public void agregarOActualizarRequisito(Puesto puesto, Caracteristica c, int nivel)
    {
        Optional<PuestoCaracteristica> existente = puestoCaracteristicaRepository.findByPuestoAndCaracteristica(puesto, c);

        PuestoCaracteristica pc = existente.orElse(new PuestoCaracteristica());
        pc.setPuesto(puesto);
        pc.setCaracteristica(c);
        pc.setNivel(nivel);
        puestoCaracteristicaRepository.save(pc);
    }


    public void quitarRequisito(Integer pcId) {
        puestoCaracteristicaRepository.deleteById(pcId);
    }


    public List<CandidatoResultado> buscarCandidatos(Puesto puesto, boolean soloCompletos) {
        List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuesto(puesto);
        List<CandidatoResultado> resultados = new ArrayList<>();

        for (Oferente oferente : oferenteRepository.findAll()) {
            if (!oferente.getAutorizado()) continue;

            int cumplidos = 0;
            int exceso = 0;
            List<DetalleRequisito> detalle = new ArrayList<>();

            for (PuestoCaracteristica req : requisitos) {
                var habilidad = oferenteHabilidadRepository.findByOferenteAndCaracteristica(oferente, req.getCaracteristica());
                if (habilidad.isPresent() && habilidad.get().getNivel() >= req.getNivel())
                {
                    cumplidos++;
                    exceso += habilidad.get().getNivel() - req.getNivel();
                    detalle.add(new DetalleRequisito(
                            req.getCaracteristica().getNombre(),
                            habilidad.get().getNivel(),
                            req.getNivel()
                    ));
                }
            }

            boolean incluir = soloCompletos ? (cumplidos == requisitos.size() && cumplidos > 0) : cumplidos > 0;

            if (incluir)
            {
                resultados.add(new CandidatoResultado(oferente, cumplidos, requisitos.size(), exceso, detalle));
            }
        }

        resultados.sort((a, b) -> Double.compare(b.getPorcentaje(), a.getPorcentaje()));
        return resultados;
    }

    public Map<String, List<Puesto>> getPuestosPorMes() {
        List<Puesto> todos = new ArrayList<>();
        puestoRepository.findAll().forEach(todos::add);

        return todos.stream()
                .sorted(Comparator.comparing(Puesto::getFechaRegistro))
                .collect(Collectors.groupingBy(
                        p -> {
                            String mes = p.getFechaRegistro()
                                    .getMonth()
                                    .getDisplayName(TextStyle.FULL, new Locale("es", "CR"));
                            mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
                            return mes + " " + p.getFechaRegistro().getYear();
                        },
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public List<Puesto> getPuestosPorMesYAnio(int mes, int anio) {
        List<Puesto> todos = new ArrayList<>();
        puestoRepository.findAll().forEach(todos::add);

        return todos.stream()
                .filter(p -> p.getFechaRegistro().getMonthValue() == mes
                        && p.getFechaRegistro().getYear() == anio)
                .sorted(Comparator.comparing(Puesto::getFechaRegistro))
                .collect(Collectors.toList());
    }
}

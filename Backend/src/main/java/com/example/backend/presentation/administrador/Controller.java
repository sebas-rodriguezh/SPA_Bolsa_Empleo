package com.example.backend.presentation.administrador;

import com.example.backend.logic.administrador.Administrador;
import com.example.backend.logic.administrador.ServiceA;
import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.empresa.ServiceE;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

// Acá van los principales cambios del Backend.


@org.springframework.stereotype.Controller("administrador")
public class Controller {
    @Autowired
    private ServiceA service;

    @Autowired
    private ServiceC serviceC;

    @Autowired
    private ServiceO serviceO;

    @Autowired
    private ServiceE serviceE;

    @Autowired
    private ServiceP serviceP;

    @Autowired
    private ServiceA serviceA;

    private boolean esAdmin(HttpSession session) {
        Object u = session.getAttribute("usuario");
        return (u instanceof Administrador);
    }

    @GetMapping("/presentation/administrador/show")
    public String show(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/";
        }

        if (!(usuario instanceof Administrador)) {
            return "redirect:/";
        }

        model.addAttribute("administrador", service.administradorFindAll());
        model.addAttribute("usuario", usuario);
        return "/presentation/administrador/View";

    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "presentation/administrador/View";
    }


    //Cambio.
    @GetMapping("/presentation/administrador/caracteristicas")
    public String caracteristicas (Model model, HttpSession session)
    {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Administrador)) {
            return "redirect:/";
        }

        model.addAttribute("caracteristicas", serviceC.caracteristicaFindAll());
        model.addAttribute("usuario", usuario);
        return "/presentation/administrador/ViewCaracteristicasAdmin";
    }


    //Cambio.
    @GetMapping("/admin/caracteristicas")
    public String caracteristicas(
            @RequestParam(required = false) Integer actualId,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/";
        cargarModeloCaracteristicas(actualId, model, session, null);
        return "presentation/administrador/ViewCaracteristicasAdmin";
    }


    //Cambio.
    @PostMapping("/admin/caracteristicas")
    public String crearCaracteristica(
            @RequestParam String nombre,
            @RequestParam(required = false) Integer padreId,
            @RequestParam(required = false) Integer actualId,
            HttpSession session,
            Model model)
    {
        if (!esAdmin(session)) return "redirect:/";


        try {
            serviceC.crearCaracteristica(nombre, padreId);
        } catch (IllegalArgumentException e) {
            cargarModeloCaracteristicas(actualId, model, session, e.getMessage());
            return "presentation/administrador/ViewCaracteristicasAdmin";
        }

        if (actualId != null) {
            return "redirect:/admin/caracteristicas?actualId=" + actualId;
        }
        return "redirect:/admin/caracteristicas";
    }

    //Cambios.
    private void cargarModeloCaracteristicas(Integer actualId, Model model,
                                             HttpSession session, String error)
    {
        Caracteristica actual = serviceC.findById(actualId);

        List<Caracteristica> categorias = (actual == null) ? serviceC.findRoots() : serviceC.findHijos(actual);

        List<Caracteristica> ruta = serviceC.buildRuta(actual);
        List<Caracteristica> opcionesPadre = categorias;

        model.addAttribute("usuario", session.getAttribute("usuario"));
        model.addAttribute("actual", actual);
        model.addAttribute("categorias", categorias);
        model.addAttribute("ruta", ruta);
        model.addAttribute("opcionesPadre", opcionesPadre);

        if (error != null) {
            model.addAttribute("error", error);
        }
    }


    //Cambios.
    @GetMapping("/admin/empresas/pendientes")
    public String empresasPendientes(Model model, HttpSession session)
    {
        if (!esAdmin(session)) return "redirect:/";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        model.addAttribute("empresas", serviceE.findPendientes());
        return "presentation/administrador/ViewEmpresasPendientes";
    }


    //Cambios.
    @GetMapping("/admin/oferentes/pendientes")
    public String oferentesPendientes(Model model, HttpSession session)
    {
        if (!esAdmin(session)) return "redirect:/";
        model.addAttribute("usuario",session.getAttribute("usuario"));
        model.addAttribute("oferentes", serviceO.findPendientes());
        return "presentation/administrador/ViewOferentesPendientes";
    }

    //Cambios.
    @PostMapping("/admin/empresas/pendientes/aprobar")
    public String aprobarEmpresa(@RequestParam Integer id, HttpSession session)
    {
        if (!esAdmin(session)) return "redirect:/";
        serviceE.aprobarEmpresa(id);
        return "redirect:/admin/empresas/pendientes";
    }


    //Cambios.
    @PostMapping("/admin/oferentes/pendientes/aprobar")
    public String aprobarOferente(@RequestParam Integer id, HttpSession session)
    {
        if (!esAdmin(session)) return "redirect:/";
        serviceO.aprobarOferente(id);
        return "redirect:/admin/oferentes/pendientes";
    }


    @GetMapping("/admin/reportes")
    public String reportes(@RequestParam(required = false) Integer mes,
                           @RequestParam(required = false) Integer anio,
                           HttpSession session, Model model) {

        if (!esAdmin(session)) return "redirect:/";
        model.addAttribute("usuario", session.getAttribute("usuario"));

        Map<String, List<Puesto>> puestosPorMes = serviceA.getPuestosSolicitadosPorMes();
        model.addAttribute("puestosPorMes", puestosPorMes);

        if (mes != null && anio != null) {
            model.addAttribute("filtrados",
                    serviceA.getPuestosSolicitadosPorMesYAnio(mes, anio));
            model.addAttribute("mesFiltro", mes);
            model.addAttribute("anioFiltro", anio);
            model.addAttribute("conteoPostulaciones",
                    serviceA.contarPostulacionesPorMes(mes, anio));

            String nombreMes = java.time.Month.of(mes)
                    .getDisplayName(java.time.format.TextStyle.FULL,
                            new java.util.Locale("es", "CR"));
            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
            model.addAttribute("nombreMesFiltro", nombreMes);
        } else {
            model.addAttribute("conteoPorMes",
                    serviceA.contarPostulacionesPorTodosMeses());
        }

        int totalPuestos = puestosPorMes.values().stream()
                .mapToInt(List::size)
                .sum();
        model.addAttribute("totalPuestos", totalPuestos);

        return "presentation/administrador/ViewReportes";
    }

}


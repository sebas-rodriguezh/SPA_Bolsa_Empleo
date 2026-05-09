package com.example.backend.presentation.oferentes;

import com.example.backend.logic.caracteristica.Caracteristica;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.logic.oferenteHabilidad.OferenteHabilidad;
import com.example.backend.logic.oferenteHabilidad.ServiceOH;
import com.example.backend.logic.postulacion.Postulacion;
import com.example.backend.logic.postulacion.ServicePO;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@org.springframework.stereotype.Controller("oferentes")
public class Controller {
    @Autowired
    private ServiceO serviceO;

    @Autowired
    private ServiceOH serviceOH;

    @Autowired
    private ServiceC serviceC;

    @Autowired
    private ServiceP serviceP;

    @Autowired
    private ServicePO servicePO;

    private boolean esOferente(HttpSession session) {
        return session.getAttribute("usuario") instanceof Oferente;
    }


    //Cambios
    @GetMapping("/presentation/oferentes/show")
    public String show(Model model, HttpSession session)
    {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Oferente)) {
            return "redirect:/";
        }

        model.addAttribute("oferentes", serviceO.oferentesFindAll());
        model.addAttribute("usuario", usuario);

        return "/presentation/oferentes/ViewDashboard";
    }


    //Cambios
    @GetMapping("/presentation/oferentes/habilidades")
    public String habilidades(Model model, HttpSession session)
    {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || !(usuario instanceof Oferente)) {
            return "redirect:/";
        }
        model.addAttribute("oferenteHabilidad", serviceOH.oferenteHabilidadFindAll());
        model.addAttribute("usuario", usuario);

        return "/presentation/oferentes/ViewParaHabilidadesDelOferente";
    }

    @GetMapping("/oferente/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");
        if (!(usuario instanceof Oferente)) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        return "presentation/oferentes/ViewDashboard";
    }

    @GetMapping("/oferente/cv")
    public String verCV(HttpSession session, Model model) {
        Oferente oferente = (Oferente) session.getAttribute("usuario");
        if (!(session.getAttribute("usuario") instanceof Oferente)) {
            return "redirect:/";
        }
        model.addAttribute("usuario", oferente);

        if (oferente.getRutaCurriculum() != null && !oferente.getRutaCurriculum().isBlank()) {
            String urlPreview = oferente.getRutaCurriculum()
                    .replace("/view", "/preview")
                    .replace("/edit", "/preview");
            model.addAttribute("urlPreview", urlPreview);
        }

        return "presentation/oferentes/ViewCV";
    }

    @PostMapping("/oferente/cv")
    public String guardarCV(@RequestParam String rutaCurriculum,
                            HttpSession session, Model model) {
        Oferente oferente = (Oferente) session.getAttribute("usuario");

        if (rutaCurriculum == null || rutaCurriculum.isBlank()) {
            model.addAttribute("usuario", oferente);
            model.addAttribute("error", "El link no puede estar vacío.");
            return "presentation/oferentes/ViewCV";
        }

        boolean esDrive = rutaCurriculum.contains("drive.google.com") ||
                rutaCurriculum.contains("docs.google.com");
        boolean esOneDrive = rutaCurriculum.contains("onedrive.live.com") ||
                rutaCurriculum.contains("1drv.ms") ||
                rutaCurriculum.contains("sharepoint.com");

        if (!esDrive && !esOneDrive) {
            model.addAttribute("usuario", oferente);
            model.addAttribute("error", "El link debe ser de Google Drive o OneDrive.");
            return "presentation/oferentes/ViewCV";
        }

        oferente.setRutaCurriculum(rutaCurriculum);
        serviceO.actualizarOferente(oferente);
        session.setAttribute("usuario", oferente);
        return "redirect:/oferente/cv";
    }

    @PostMapping("/oferente/cv/eliminar")
    public String eliminarCV(HttpSession session) {
        Oferente oferente = (Oferente) session.getAttribute("usuario");
        oferente.setRutaCurriculum(null);
        serviceO.actualizarOferente(oferente);
        session.setAttribute("usuario", oferente);
        return "redirect:/oferente/cv";
    }


    //Cambios
    @GetMapping("/oferente/habilidades")
    public String habilidades(@RequestParam(required = false) Integer actualId,
                              HttpSession session, Model model)
    {
        if (!esOferente(session)) return "redirect:/";
        Oferente oferente = (Oferente) session.getAttribute("usuario");

        Caracteristica actual = serviceC.findById(actualId);
        List<Caracteristica> categorias = (actual == null) ? serviceC.findRoots() : serviceC.findHijos(actual);

        List<OferenteHabilidad> habilidades = serviceOH.findByOferente(oferente);
        Map<Integer, String> rutasHabilidades = new HashMap<>();

        for (var h : habilidades) { rutasHabilidades.put(h.getId(), serviceC.buildRutaString(h.getCaracteristica())); }

        model.addAttribute("usuario", oferente);
        model.addAttribute("actual", actual);
        model.addAttribute("categorias", categorias);
        model.addAttribute("ruta", serviceC.buildRuta(actual));
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("rutasHabilidades", rutasHabilidades);

        return "presentation/oferentes/ViewHabilidades";
    }


    //Cambios
    @PostMapping("/oferente/habilidades/agregar")
    public String agregar(@RequestParam Integer caracteristicaId,
                          @RequestParam int nivel,
                          @RequestParam(required = false) Integer actualId,
                          HttpSession session)
    {
        if (!esOferente(session)) return "redirect:/";
        Oferente oferente = (Oferente) session.getAttribute("usuario");
        Caracteristica c = serviceC.findById(caracteristicaId);

        if (c != null) {
            serviceOH.agregarOActualizar(oferente, c, nivel);
        }

        String redirect = "/oferente/habilidades";
        if (actualId != null) redirect += "?actualId=" + actualId;
        return "redirect:" + redirect;
    }


    //Cambios
    @PostMapping("/oferente/habilidades/eliminar")
    public String eliminar(@RequestParam Integer habilidadId, @RequestParam(required = false) Integer actualId, HttpSession session)
    {
        if (!esOferente(session)) return "redirect:/";
        serviceOH.eliminar(habilidadId);

        String redirect = "/oferente/habilidades";
        if (actualId != null) redirect += "?actualId=" + actualId;
        return "redirect:" + redirect;
    }

    @GetMapping("/oferente/postulacion")
    public String formPostulacion(HttpSession session, Model model) {
        if (!esOferente(session)) return "redirect:/";
        Oferente oferente = (Oferente) session.getAttribute("usuario");

        List<Puesto> puestosDisponibles = serviceP.findAllActivos().stream().filter(p -> !servicePO.yaPostulado(oferente, p)).toList();

        model.addAttribute("usuario", oferente);
        model.addAttribute("puestos", puestosDisponibles);
        return "presentation/oferentes/ViewPostulacion";
    }

    @PostMapping("/oferente/postulacion")
    public String guardarPostulacion(@RequestParam Integer puestoId, HttpSession session)
    {
        if (!esOferente(session)) return "redirect:/";

        Oferente oferente = (Oferente) session.getAttribute("usuario");

        serviceP.findById(puestoId).ifPresent(puesto -> servicePO.postular(oferente, puesto));

        return "redirect:/oferente/postulaciones";
    }


    //Cambios

    @GetMapping("/oferente/postulaciones")
    public String verMisPostulaciones(HttpSession session, Model model)
    {
        if (!esOferente(session) || session.getAttribute("usuario") == null) return "redirect:/";

        Oferente oferente = (Oferente) session.getAttribute("usuario");
        List<Postulacion> postulaciones = servicePO.findByOferente(oferente);
        model.addAttribute("oferente", oferente);
        model.addAttribute("postulaciones", postulaciones);

        return "presentation/oferentes/ViewMisPostulaciones";
    }

}




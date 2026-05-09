package com.example.backend.presentation.login;

import com.example.backend.logic.administrador.Administrador;
import com.example.backend.logic.administrador.ServiceA;
import com.example.backend.logic.caracteristica.ServiceC;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.empresa.ServiceE;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.logic.postulacion.ServicePO;
import com.example.backend.logic.puesto.Puesto;
import com.example.backend.logic.puesto.ServiceP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@org.springframework.stereotype.Controller("usuario")
@SessionAttributes("usuario")
public class Controller {
    @Autowired
    private ServiceA serviceA;

    @Autowired
    private ServiceE serviceE;

    @Autowired
    private ServiceO serviceO;

    @Autowired
    private ServiceP serviceP;

    @Autowired
    private ServiceC serviceC;

    @Autowired
    private ServicePO servicePO;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");

        if (usuario instanceof Administrador) return "redirect:/admin/dashboard";
        if (usuario instanceof Empresa) return "redirect:/empresa/dashboard";
        if (usuario instanceof Oferente) return "redirect:/oferente/dashboard";

        model.addAttribute("ultimosPuestos", serviceP.getUltimosPuestosPublicos());
        return "presentation/index";
    }

    @GetMapping("/puestos")
    public String todosPuestos(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuario");

        if (!(usuario instanceof Empresa) && !(usuario instanceof Oferente)) {
            return "redirect:/presentation/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("todosPuestos", serviceP.findAllActivos());
        return "presentation/puestos/ViewTodosPuestos";
    }

    @GetMapping("/presentation/login")
    public String loginForm(Model model) {
        return "/presentation/login/View";
    }

    //Cambios.
    @PostMapping("/presentation/login")
    public String autenticar(@RequestParam("correo") String correo, @RequestParam("clave") String clave, HttpSession session, Model model)
    {
        Object usuario = serviceA.findUserByEmailAndPassword(correo, clave);
        System.out.println("Usuario encontrado: " + (usuario != null ? usuario.getClass().getSimpleName() : "NULL"));

        if (usuario == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "/presentation/login/View";
        }
        session.setAttribute("loginTemp", usuario);
        model.addAttribute("estado", "verificando");
        model.addAttribute("redirectUrl", "/presentation/login/verificar");

        return "/presentation/login/ViewAutorizacion";
    }

    @GetMapping("/presentation/login/verificar")
    public String verificarAutorizacion(HttpSession session, Model model) {

        Object usuario = session.getAttribute("loginTemp");

        if (usuario == null) {
            return "redirect:/presentation/login";
        }

        boolean autorizado = false;

        if (usuario instanceof Administrador) {
            autorizado = true;
        } else if (usuario instanceof Empresa empresa) {
            autorizado = Boolean.TRUE.equals(empresa.getAutorizada());
        } else if (usuario instanceof Oferente oferente) {
            autorizado = Boolean.TRUE.equals(oferente.getAutorizado());
        }
        session.removeAttribute("loginTemp");

        if (!autorizado) {
            model.addAttribute("estado", "denegado");
            return "/presentation/login/ViewAutorizacion";
        }

        session.setAttribute("usuario", usuario);
        model.addAttribute("usuario", usuario);

        if (usuario instanceof Administrador) {
            return "redirect:/presentation/administrador/show";
        } else if (usuario instanceof Empresa) {
            return "redirect:/presentation/empresas/show";
        } else if (usuario instanceof Oferente) {
            return "redirect:/presentation/oferentes/show";
        }

        return "redirect:/";
    }

    @GetMapping("/presentation/login/logout")
    public String logout(HttpSession session, SessionStatus status) {
        session.invalidate();
        status.setComplete();
        return "redirect:/";
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    @GetMapping("/registro")
    public String seleccionRegistro() {
        return "presentation/login/ViewSeleccion";
    }

    @GetMapping("/registro/empresa")
    public String formEmpresa() {
        return "presentation/empresas/ViewRegistro";
    }

    @PostMapping("/registro/empresa")
    public String registrarEmpresa(
            @RequestParam String nombre,
            @RequestParam String localizacion,
            @RequestParam String correo,
            @RequestParam String clave,
            @RequestParam String telefono,
            @RequestParam String descripcion,
            Model model) {

        String error = null;

        if (nombre == null || nombre.isBlank() || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"))
            error = "El nombre de la empresa solo puede contener letras.";

        if (error == null && (localizacion == null || localizacion.isBlank() || !localizacion.matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*")))
            error = "La localización debe contener al menos una letra.";

        if (error == null && (clave == null || clave.length() < 8))
            error = "La contraseña debe tener al menos 8 caracteres.";

        if (error == null && (telefono == null || !telefono.matches("\\d{8}")))
            error = "El teléfono debe contener exactamente 8 números.";

        if (error == null && (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")))
            error = "El correo electrónico no es válido (debe tener formato usuario@dominio.com).";

        if (error == null)
            error = serviceE.validarRegistro(correo);

        if (error != null) {
            model.addAttribute("error", error);
            return "presentation/empresas/ViewRegistro";
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setCorreo(correo);
        empresa.setClave(clave);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);
        empresa.setAutorizada(false);

        serviceE.registrarEmpresa(empresa);
        return "redirect:/registro-pendiente";
    }

    @GetMapping("/registro/oferente")
    public String formOferente() {
        return "presentation/oferentes/ViewRegistro";
    }

    @GetMapping("/registro/administrador")
    public String formAdministrador() {
        return "presentation/administrador/ViewRegistro";
    }


    @PostMapping("/registro/oferente")
    public String registrarOferente(
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String primerApellido,
            @RequestParam String nacionalidad,
            @RequestParam String telefono,
            @RequestParam String correo,
            @RequestParam String clave,
            @RequestParam String lugarResidencia,
            @RequestParam(required = false) String rutaCurriculum,
            Model model) {

        String error = null;

        if (identificacion == null || !identificacion.matches("\\d+"))
            error = "La identificación debe contener solo números.";

        if (error == null && (nombre == null || nombre.isBlank() || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")))
            error = "El nombre solo puede contener letras.";

        if (error == null && (primerApellido == null || primerApellido.isBlank() || !primerApellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")))
            error = "El primer apellido solo puede contener letras.";

        if (error == null && (nacionalidad == null || nacionalidad.isBlank() || !nacionalidad.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")))
            error = "La nacionalidad solo puede contener letras.";

        if (error == null && (lugarResidencia == null || lugarResidencia.isBlank() || !lugarResidencia.matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*")))
            error = "El lugar de residencia debe contener al menos una letra.";

        if (error == null && (telefono == null || !telefono.matches("\\d{8}")))
            error = "El teléfono debe contener exactamente 8 números.";

        if (error == null && (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")))
            error = "El correo electrónico no es válido (debe tener formato usuario@dominio.com).";

        if (error == null)
            error = serviceO.validarRegistro(correo, identificacion);

        if (error != null) {
            model.addAttribute("error", error);
            return "presentation/oferentes/ViewRegistro";
        }

        Oferente oferente = new Oferente();
        oferente.setIdentificacion(identificacion);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setNacionalidad(nacionalidad);
        oferente.setTelefono(telefono);
        oferente.setCorreo(correo);
        oferente.setClave(clave);
        oferente.setLugarResidencia(lugarResidencia);
        oferente.setRutaCurriculum(rutaCurriculum);
        oferente.setAutorizado(false);

        serviceO.registrarOferente(oferente);
        return "redirect:/registro-pendiente";
    }

    @PostMapping("/registro/administrador")
    public String registrarAdministrador(
            @RequestParam String correo,
            @RequestParam String clave) {

        Administrador administrador = new Administrador();
        administrador.setCorreo(correo);
        administrador.setClave(clave);

        serviceA.registrarAdministrador(administrador);
        return "redirect:/presentation/login";
    }

    @GetMapping("/registro-pendiente")
    public String registroPendiente() {
        return "presentation/login/ViewRegistroPendiente";
    }


    //Cambios.
    @GetMapping("/buscar-puestos")
    public String formBuscarPuestos(Model model, HttpSession session)
    {
        model.addAttribute("arbol", serviceC.getArbolOrdenado());
        model.addAttribute("niveles", serviceC.getNivelesArbol());
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "presentation/puestos/ViewBuscarPuestos";
    }


    //Cambios.
    @PostMapping("/buscar-puestos")
    public String buscarPuestos(@RequestParam(required = false) List<Integer> caracteristicaIds, @RequestParam(required = false) String moneda, Model model, HttpSession session)
    {
        Object usuario = session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        model.addAttribute("arbol", serviceC.getArbolOrdenado());
        model.addAttribute("niveles", serviceC.getNivelesArbol());
        model.addAttribute("seleccionados", caracteristicaIds != null ? caracteristicaIds : new ArrayList<>());
        model.addAttribute("monedaSeleccionada", moneda);

        if (usuario instanceof Oferente oferente) {
            model.addAttribute("resultados", serviceP.buscarPuestosParaOferente(caracteristicaIds, moneda));

            Set<Integer> postuladosIds = serviceP.findAllActivos().stream().filter(p -> servicePO.yaPostulado(oferente, p)).map(Puesto::getId).collect(toSet());
            model.addAttribute("postulados", postuladosIds);
        }
        else
        {
            model.addAttribute("resultados", serviceP.buscarPuestosPublicos(caracteristicaIds, moneda));
        }
        return "presentation/puestos/ViewBuscarPuestos";
    }


}

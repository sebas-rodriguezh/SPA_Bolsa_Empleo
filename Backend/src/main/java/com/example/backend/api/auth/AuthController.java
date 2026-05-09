package com.example.backend.api.auth;

import com.example.backend.dto.auth.LoginDTO;
import com.example.backend.dto.empresa.RegistroEmpresaDTO;
import com.example.backend.dto.oferente.RegistroOferenteDTO;
import com.example.backend.logic.administrador.Administrador;
import com.example.backend.logic.administrador.ServiceA;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.empresa.ServiceE;
import com.example.backend.logic.oferente.Oferente;
import com.example.backend.logic.oferente.ServiceO;
import com.example.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private ServiceA serviceA;
    @Autowired private ServiceE serviceE;
    @Autowired private ServiceO serviceO;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        if (dto.getCorreo() == null || dto.getCorreo().isBlank() || dto.getClave() == null || dto.getClave().isBlank())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Correo y contraseña son obligatorios"));
        }

        Object usuarioObj = serviceA.findUserByEmailAndPassword(dto.getCorreo().trim(), dto.getClave());

        if (usuarioObj == null)
        {
            return ResponseEntity.status(401).body(Map.of("error", "Correo o contraseña incorrectos"));
        }

        if ("PENDIENTE".equals(usuarioObj))
        {
            return ResponseEntity.status(403).body(Map.of("error", "Cuenta pendiente de aprobación por un administrador"));
        }

        String rol;
        String nombre;
        Integer id;

        if (usuarioObj instanceof Administrador a) {
            rol    = "ADMIN";
            nombre = a.getCorreo();
            id     = a.getId();
        } else if (usuarioObj instanceof Empresa e) {
            rol    = "EMPRESA";
            nombre = e.getNombre();
            id     = e.getId();
        } else {
            Oferente o = (Oferente) usuarioObj;
            rol    = "OFERENTE";
            nombre = o.getNombre() + " " + o.getPrimerApellido();
            id     = o.getId();
        }

        String token = jwtService.generateToken(dto.getCorreo().trim(), rol);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType","Bearer",
                "rol", rol,
                "nombre", nombre,
                "correo", dto.getCorreo().trim(),
                "id", id
        ));
    }

    @PostMapping("/registro/empresa")
    public ResponseEntity<?> registrarEmpresa(@RequestBody RegistroEmpresaDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank() || !dto.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"))
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre solo puede contener letras"));

        if (dto.getClave() == null || dto.getClave().length() < 8)
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 8 caracteres"));

        if (dto.getLocalizacion() == null || dto.getLocalizacion().isBlank() || !dto.getLocalizacion().matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*"))
            return ResponseEntity.badRequest().body(Map.of("error", "La localización debe contener al menos una letra"));

        if (dto.getTelefono() == null || !dto.getTelefono().matches("\\d{8}"))
            return ResponseEntity.badRequest().body(Map.of("error", "El teléfono debe tener exactamente 8 dígitos"));

        if (dto.getCorreo() == null || !dto.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            return ResponseEntity.badRequest().body(Map.of("error", "El correo no es válido"));

        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "La descripción es obligatoria"));

        String error = serviceE.validarRegistro(dto.getCorreo());
        if (error != null)
            return ResponseEntity.badRequest().body(Map.of("error", error));

        Empresa empresa = new Empresa();
        empresa.setNombre(dto.getNombre().trim());
        empresa.setLocalizacion(dto.getLocalizacion().trim());
        empresa.setCorreo(dto.getCorreo().trim());
        empresa.setClave(dto.getClave());
        empresa.setTelefono(dto.getTelefono().trim());
        empresa.setDescripcion(dto.getDescripcion().trim());
        empresa.setAutorizada(false);
        serviceE.registrarEmpresa(empresa);

        return ResponseEntity.status(201).body(Map.of("mensaje", "Empresa registrada. Pendiente de aprobación."));
    }

    @PostMapping("/registro/oferente")
    public ResponseEntity<?> registrarOferente(@RequestBody RegistroOferenteDTO dto) {

        if (dto.getIdentificacion() == null || !dto.getIdentificacion().matches("\\d+"))
            return ResponseEntity.badRequest().body(Map.of("error", "La identificación debe contener solo números"));

        if (dto.getNombre() == null || dto.getNombre().isBlank() || !dto.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"))
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre solo puede contener letras"));

        if (dto.getClave() == null || dto.getClave().length() < 8)
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 8 caracteres"));

        if (dto.getTelefono() == null || !dto.getTelefono().matches("\\d{8}"))
            return ResponseEntity.badRequest().body(Map.of("error", "El teléfono debe tener exactamente 8 dígitos"));

        if (dto.getCorreo() == null || !dto.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            return ResponseEntity.badRequest().body(Map.of("error", "El correo no es válido"));

        if (dto.getPrimerApellido() == null || dto.getPrimerApellido().isBlank() || !dto.getPrimerApellido().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"))
            return ResponseEntity.badRequest().body(Map.of("error", "El primer apellido solo puede contener letras"));

        if (dto.getNacionalidad() == null || dto.getNacionalidad().isBlank() || !dto.getNacionalidad().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"))
            return ResponseEntity.badRequest().body(Map.of("error", "La nacionalidad solo puede contener letras"));

        if (dto.getLugarResidencia() == null || dto.getLugarResidencia().isBlank() || !dto.getLugarResidencia().matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*"))
            return ResponseEntity.badRequest().body(Map.of("error", "El lugar de residencia debe contener al menos una letra"));

        String error = serviceO.validarRegistro(dto.getCorreo(), dto.getIdentificacion());
        if (error != null)
            return ResponseEntity.badRequest().body(Map.of("error", error));

        Oferente oferente = new Oferente();
        oferente.setIdentificacion(dto.getIdentificacion().trim());
        oferente.setNombre(dto.getNombre().trim());
        oferente.setPrimerApellido(dto.getPrimerApellido().trim());
        oferente.setNacionalidad(dto.getNacionalidad().trim());
        oferente.setTelefono(dto.getTelefono().trim());
        oferente.setCorreo(dto.getCorreo().trim());
        oferente.setClave(dto.getClave());
        oferente.setLugarResidencia(dto.getLugarResidencia().trim());
        oferente.setAutorizado(false);
        serviceO.registrarOferente(oferente);

        return ResponseEntity.status(201).body(Map.of("mensaje", "Oferente registrado. Pendiente de aprobación."));
    }
}
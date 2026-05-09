package com.example.backend.security;


import com.example.backend.data.AdministradorRepository;
import com.example.backend.data.EmpresaRepository;
import com.example.backend.data.OferenteRepository;
import com.example.backend.logic.administrador.Administrador;
import com.example.backend.logic.empresa.Empresa;
import com.example.backend.logic.oferente.Oferente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private OferenteRepository oferenteRepository;
    @Autowired private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Empresa empresa = empresaRepository.findByCorreo(correo);
        if (empresa != null) {
            return User.builder()
                    .username(empresa.getCorreo())
                    .password(empresa.getClave())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_EMPRESA")))
                    .build();
        }

        Oferente oferente = oferenteRepository.findByCorreo(correo);
        if (oferente != null) {
            return User.builder()
                    .username(oferente.getCorreo())
                    .password(oferente.getClave())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_OFERENTE")))
                    .build();
        }

        Administrador admin = administradorRepository.findByCorreo(correo);
        if (admin != null) {
            return User.builder()
                    .username(admin.getCorreo())
                    .password(admin.getClave())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }
}
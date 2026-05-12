package com.example.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    // Redirige todas las rutas no-API a index.html
    // sin esto, al refrescar /empresa/dashboard Spring da 404
    @GetMapping(value = {
            "/", "/{p1:[^\\.]*}",
            "/{p1:[^\\.]*}/{p2:[^\\.]*}",
            "/{p1:[^\\.]*}/{p2:[^\\.]*}/{p3:[^\\.]*}"
    })
    public String spa() {
        return "forward:/index.html";
    }
}
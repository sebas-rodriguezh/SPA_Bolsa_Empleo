package com.example.backend.dto.auth;

public class LoginResponseDTO {
    private String rol;
    private String correo;
    private String nombre;
    // token se agregará acá JWT
    // private String token;

    public LoginResponseDTO(String rol, String correo, String nombre) {
        this.rol = rol;
        this.correo = correo;
        this.nombre = nombre;
    }
    public String getRol() { return rol; }
    public String getCorreo() { return correo; }
    public String getNombre() { return nombre; }
}
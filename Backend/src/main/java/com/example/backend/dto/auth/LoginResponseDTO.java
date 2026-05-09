package com.example.backend.dto.auth;


public class LoginResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private String rol;
    private String correo;
    private String nombre;
    private Integer id;

    public LoginResponseDTO(String token, String rol, String correo, String nombre, Integer id) {
        this.token = token;
        this.rol = rol;
        this.correo = correo;
        this.nombre = nombre;
        this.id = id;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public String getRol() { return rol; }
    public String getCorreo() { return correo; }
    public String getNombre() { return nombre; }
    public Integer getId() { return id; }
}


//public class LoginResponseDTO {
//    private String rol;
//    private String correo;
//    private String nombre;
//    // token se agregará acá JWT
//    // private String token;
//
//    public LoginResponseDTO(String rol, String correo, String nombre) {
//        this.rol = rol;
//        this.correo = correo;
//        this.nombre = nombre;
//    }
//    public String getRol() { return rol; }
//    public String getCorreo() { return correo; }
//    public String getNombre() { return nombre; }
//}
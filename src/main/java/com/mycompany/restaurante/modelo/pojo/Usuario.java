package com.mycompany.restaurante.modelo.pojo;

public class Usuario {
    private String username;
    private String password;
    private String rol;

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() { return username; }
    public String getRol() { return rol; }
}
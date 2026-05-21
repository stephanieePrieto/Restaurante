package com.mycompany.restaurante.modelo.pojo;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String password;
    private int idRol;
    private String rol;

    public Usuario() {}

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(int id, String nombre, String username, String password, int idRol, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.idRol = idRol;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getNombreRol() { return rol; }
    public void setNombreRol(String rol) { this.rol = rol; }
}
package com.mycompany.restaurante.modelo.pojo;

public class Asistencia {
    private int id; // Nuevo
    private String usuario;
    private String entrada;
    private String salida;
    private String estado;
    private String horasTrabajadas;

    // Constructor para mostrar en tabla
    public Asistencia(int id, String usuario, String entrada, String salida, String estado, String horasTrabajadas) {
        this.id = id;
        this.usuario = usuario;
        this.entrada = entrada;
        this.salida = salida;
        this.estado = estado;
        this.horasTrabajadas = horasTrabajadas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(String horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
    
}
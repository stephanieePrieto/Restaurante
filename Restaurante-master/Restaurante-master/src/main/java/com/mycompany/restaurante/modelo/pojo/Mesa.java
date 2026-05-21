package com.mycompany.restaurante.modelo.pojo;

public class Mesa {

    private int idMesa;
    private String estado;
    private String detalles;

    public Mesa() {
    }

    public Mesa(int idMesa, String estado, String detalles) {
        this.idMesa = idMesa;
        this.estado = estado;
        this.detalles = detalles;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
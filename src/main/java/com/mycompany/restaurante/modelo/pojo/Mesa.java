
package com.mycompany.restaurante.modelo.pojo;


public class Mesa {
    private int idMesa;
    private String estado; // Libre / Ocupada

    // Constructor para crear la mesa con datos
    public Mesa(int idMesa, String estado) {
        this.idMesa = idMesa;
        this.estado = estado;
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
}
//gets y sets 
package com.mycompany.restaurante.modelo.pojo;

import java.time.LocalDateTime;
import java.util.List;

public class Pedido { // Nombre actualizado

    private int idPedido; // Coincide con tu SQL
    private String estado;
    private LocalDateTime fechaHora; // Coincide con tu SQL
    private int idMesa;
    private int idEmpleado; // Coincide con tu SQL
    private List<Platillo> listaPlatillos;
    private double total;
    private String detalleTexto; // Usaremos esto para el texto aesthetic de la tabla

    public Pedido() {}

    // Getters y Setters actualizados
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getDetalleTexto() { return detalleTexto; }
    public void setDetalleTexto(String detalleTexto) { this.detalleTexto = detalleTexto; }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public List<Platillo> getListaPlatillos() {
        return listaPlatillos;
    }

    public void setListaPlatillos(List<Platillo> listaPlatillos) {
        this.listaPlatillos = listaPlatillos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    
}
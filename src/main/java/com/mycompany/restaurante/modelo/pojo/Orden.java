package com.mycompany.restaurante.modelo.pojo;

import java.time.LocalDateTime;
import java.util.List;

public class Orden {

    private int idOrden;
    private String estado;
    private LocalDateTime fechaHoraLlegada;
    private int idMesa;
    private int idMesero;
    private List listaPlatillo;
    private double total;
    private String metodoPago;

    public Orden() {
    }

    public Orden(int idOrden, String estado, LocalDateTime fechaHoraLlegada, 
            int idMesa, int idMesero, List listaPlatillo, double total, 
            String metodoPago) {
        this.idOrden = idOrden;
        this.estado = estado;
        this.fechaHoraLlegada = fechaHoraLlegada;
        this.idMesa = idMesa;
        this.idMesero = idMesero;
        this.listaPlatillo = listaPlatillo;
        this.total = total;
        this.metodoPago = metodoPago;
    }

    public int getIdOrden() {
        return idOrden;
    }
    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDateTime getFechaHoraLlegada() {
        return fechaHoraLlegada;
    }
    public void setFechaHoraLlegada(LocalDateTime fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }
    public int getIdMesa() {
        return idMesa;
    }
    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }
    public int getIdMesero() {
        return idMesero;
    }
    public void setIdMesero(int idMesero) {
        this.idMesero = idMesero;
    }
    public List getListaPlatillo() {
        return listaPlatillo;
    }
    public void setListaPlatillo(List listaPlatillo) {
        this.listaPlatillo = listaPlatillo;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    @Override
    public String toString() {
        return "Orden{" + "idOrden=" + idOrden + ", estado=" + estado +
                ", fechaHoraLlegada=" + fechaHoraLlegada + ", idMesa=" +
                idMesa + ", idMesero=" + idMesero + ", listaPlatillo=" +
                listaPlatillo + ", total=" + total + ", metodoPago=" +
                metodoPago + '}';
    }
    
    
}

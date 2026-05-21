package com.mycompany.restaurante.modelo.pojo;

public class VentaReporte {
    private String fecha;
    private double total;

    public VentaReporte(String fecha, double total) {
        this.fecha = fecha;
        this.total = total;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
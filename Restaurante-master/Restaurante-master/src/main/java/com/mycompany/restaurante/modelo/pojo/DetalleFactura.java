package com.mycompany.restaurante.modelo.pojo;

public class DetalleFactura {
    
    private String claveProdServ;
    private int cantidad;
    private String unidad;
    private String platillo;
    private double precioUnitario;
    private double total;

    // Constructor vacío
    public DetalleFactura() {
    }

    // Constructor completo
    public DetalleFactura(String claveProdServ, int cantidad, String unidad, String platillo, double precioUnitario, double total) {
        this.claveProdServ = claveProdServ;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.platillo = platillo;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    // Métodos Getter y Setter
    public String getClaveProdServ() { return claveProdServ; }
    public void setClaveProdServ(String claveProdServ) { this.claveProdServ = claveProdServ; }

    public int getQuantity() { return cantidad; } 
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getPlatillo() { return platillo; }
    public void setPlatillo(String platillo) { this.platillo = platillo; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
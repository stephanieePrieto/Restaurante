package com.mycompany.restaurante.modelo.pojo;

public class DetalleFactura {
    private String platillo;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private double total;

    public DetalleFactura(String platillo, int cantidad, double precioUnitario, double subtotal, double total) {
        this.platillo = platillo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.total = total;
    }

    // Getters (Muy importantes para que la tabla los lea)
    public String getPlatillo() { return platillo; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public double getTotal() { return total; }
}

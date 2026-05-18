
package com.mycompany.restaurante.modelo.pojo;


public class DetallePedido {
    private String platillo;
    private int cantidad;
    private double precio;
    private double subtotal;

    public DetallePedido(String platillo, int cantidad, double precio, double subtotal) {
        this.platillo = platillo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }
    // Getters necesarios para la TableView
    public String getPlatillo() { return platillo; }
    public int getCantidad() { return cantidad; }
    public double getPrecio() { return precio; }
    public double getSubtotal() { return subtotal; }
}

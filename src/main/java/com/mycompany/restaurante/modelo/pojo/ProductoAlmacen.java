package com.mycompany.restaurante.modelo.pojo;

public class ProductoAlmacen {
    private int idProducto;
    private String nombre;
    private double cantidad;
    private String unidad;
    private double stockMinimo;

    public ProductoAlmacen() {}

    public ProductoAlmacen(int idProducto, String nombre, double cantidad, String unidad, double stockMinimo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.stockMinimo = stockMinimo;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }
}
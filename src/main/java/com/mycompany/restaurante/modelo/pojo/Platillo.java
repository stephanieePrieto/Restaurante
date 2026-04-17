package com.mycompany.restaurante.modelo.pojo;

public class Platillo {
    
    private int idPlatillo;
    private String nombre;
    private double precio;
    private String categoria;
    private String descripcion;
    private boolean esBebida;
    private boolean disponibilidad;
    private int cantidad;

    public Platillo() {
    }

    public Platillo(int idPlatillo, String nombre, double precio,
            String categoria, String descripcion, boolean esBebida,
            boolean disponibilidad) {
        this.idPlatillo = idPlatillo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.esBebida = esBebida;
        this.disponibilidad = disponibilidad;
    }
    public int getIdPlatillo() {
        return idPlatillo;
    }
    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public boolean isEsBebida() {
        return esBebida;
    }
    public void setEsBebida(boolean esBebida) {
        this.esBebida = esBebida;
    }
    public boolean isDisponibilidad() {
        return disponibilidad;
    }
    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Platillo{" + "idPlatillo=" + idPlatillo + ", nombre=" + nombre + ", precio=" + precio + ", categoria=" + categoria + ", descripcion=" + descripcion + ", esBebida=" + esBebida + ", disponibilidad=" + disponibilidad + ", cantidad=" + cantidad + '}';
    }
    
    
    
}
//hola

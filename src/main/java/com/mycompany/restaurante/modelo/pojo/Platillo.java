package com.mycompany.restaurante.modelo.pojo;

/**
 * Clase POJO unificada para representar un Platillo en el sistema.
 */
public class Platillo {
    
    // Atributos consolidados
    private int idPlatillo;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private int idCategoria; // <-- AGREGAMOS ESTE ATRIBUTO PARA LA BASE DE DATOS
    private String imagen; 
    private boolean esBebida;
    private boolean disponibilidad;
    private int cantidad;

    // 1. Constructor vacío
    public Platillo() {
    }

    // 2. Constructor completo (Actualizado con idCategoria)
    public Platillo(int idPlatillo, String nombre, String descripcion, double precio, 
                    String categoria, String imagen, boolean esBebida, 
                    boolean disponibilidad, int idCategoria) { // <-- Se lo pasamos aquí
        this.idPlatillo = idPlatillo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.imagen = imagen;
        this.esBebida = esBebida;
        this.disponibilidad = disponibilidad;
        this.idCategoria = idCategoria; // <-- Y lo asignamos aquí
        this.cantidad = 0; // Por defecto empezamos en 0
    }

    // --- Getters y Setters NUEVOS para idCategoria ---

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    // --- Getters y Setters de los demás campos (Se mantienen igual) ---

    public int getIdPlatillo() { return idPlatillo; }
    public void setIdPlatillo(int idPlatillo) { this.idPlatillo = idPlatillo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public boolean isEsBebida() { return esBebida; }
    public void setEsBebida(boolean esBebida) { this.esBebida = esBebida; }
    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "Platillo{" +
                "idPlatillo=" + idPlatillo +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", categoria='" + categoria + '\'' +
                ", idCategoria=" + idCategoria + // Agregado al toString
                ", disponibilidad=" + disponibilidad +
                ", cantidad=" + cantidad +
                '}';
    }
}
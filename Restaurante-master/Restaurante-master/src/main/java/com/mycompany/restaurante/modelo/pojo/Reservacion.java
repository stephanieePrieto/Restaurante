package com.mycompany.restaurante.modelo.pojo;

public class Reservacion {
    private int idReservacion;
    private String folioUnico;
    private String idCliente;
    private String nombreCliente; // Para guardar el nombre real traído con el JOIN
    private int idMesa;
    private String fecha;
    private String hora;
    private int numPersonas;
    private String estado;

    // Constructor completo para el DAO
    public Reservacion(int idReservacion, String folioUnico, String idCliente, String nombreCliente, 
                       int idMesa, String fecha, String hora, int numPersonas, String estado) {
        this.idReservacion = idReservacion;
        this.folioUnico = folioUnico;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idMesa = idMesa;
        this.fecha = fecha;
        this.hora = hora;
        this.numPersonas = numPersonas;
        this.estado = estado;
    }

    // Getters esenciales para que las TableColumn de JavaFX puedan leer las celdas
    public int getIdReservacion() { return idReservacion; }
    public String getFolioUnico() { return folioUnico; }
    public String getIdCliente() { return idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public int getIdMesa() { return idMesa; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getNumPersonas() { return numPersonas; }
    public String getEstado() { return estado; }
}
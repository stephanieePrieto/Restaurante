package com.mycompany.restaurante.modelo.pojo;

public class Pago {
    private double total;
    private String metodo;
    private int idPedido;
    private int idPago;      
    private int idMesa;
    private double monto;



    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    




    // GET Y SET

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

}

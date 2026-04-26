package com.mycompany.restaurante.modelo.pojo;

import com.mycompany.restaurante.dao.CuentaDAO;
import com.mycompany.restaurante.dao.MesaDAO;
import com.mycompany.restaurante.dao.PagoDAO;

public class ServicioPago {

    private CuentaDAO cuentaDAO = new CuentaDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    private MesaDAO mesaDAO = new MesaDAO();

    public boolean registrarPago(int idMesa, double monto, String metodo) {

        double total = cuentaDAO.obtenerTotalPorMesa(idMesa);
        int idPedido = cuentaDAO.obtenerPedidoPorMesa(idMesa);

        if (monto < total) {
            return false;
        }

        Pago pago = new Pago();
        pago.setMonto(total);
        pago.setMetodo(metodo);
        pago.setIdPedido(idPedido);

        boolean guardado = pagoDAO.insertarPago(pago);

        if (guardado) {
            mesaDAO.liberarMesa(idMesa);
            return true;
        }

        return false;
    }
}
package com.mycompany.restaurante.modelo.pojo;

import com.mycompany.restaurante.dao.CuentaDAO;
import com.mycompany.restaurante.dao.MesaDAO;
import com.mycompany.restaurante.dao.PagoDAO;
import com.mycompany.restaurante.dao.TicketDAO;

public class ServicioPago {

    CuentaDAO cuentaDAO = new CuentaDAO();
    PagoDAO pagoDAO = new PagoDAO();
    MesaDAO mesaDAO = new MesaDAO();
    TicketDAO ticketDAO = new TicketDAO();

    public boolean registrarPago(Pago pago) {

        double total = cuentaDAO.obtenerTotalPorMesa(pago.getIdMesa());
        int idPedido = cuentaDAO.obtenerPedidoPorMesa(pago.getIdMesa());

        if (pago.getMonto() < total) {
            return false;
        }

        pago.setTotal(total);
        pago.setIdPedido(idPedido);
        
        int idPago = pagoDAO.insertarPago(pago);
        boolean guardado = idPago > 0;

        if (guardado) {
            ticketDAO.generarTicket(idPedido); // 
            mesaDAO.liberarMesa(pago.getIdMesa());
        }

        return guardado;
    }
}
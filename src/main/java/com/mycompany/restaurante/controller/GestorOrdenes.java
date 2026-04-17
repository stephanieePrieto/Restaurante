
package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.OrdenDAO;
import com.mycompany.restaurante.dao.PlatilloDAO;
import com.mycompany.restaurante.modelo.pojo.Orden;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import java.sql.SQLException;
import java.util.List;

public class GestorOrdenes {
    private OrdenDAO ordenDAO;
    private PlatilloDAO platilloDAO;

    public GestorOrdenes(java.sql.Connection conexion) {
        this.ordenDAO = new OrdenDAO(conexion);
        this.platilloDAO = new PlatilloDAO(conexion);
    }

    public List<Orden> obtenerComandasParaChef() throws SQLException {
        // 1. Buscamos órdenes con estado Pendiente [cite: 2]
        List<Orden> pendientes = ordenDAO.buscarOrdenesPorEstado("Pendiente");

        // 2. Para cada orden, recuperamos sus platillos (Paso 3 del flujo normal) [cite: 2]
        for (Orden orden : pendientes) {
            List<Platillo> detalles = platilloDAO.obtenerPlatillosPorOrden(orden.getIdOrden());
            orden.setListaPlatillo(detalles);
        }
        
        return pendientes;
    }
}
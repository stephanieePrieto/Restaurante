package com.mycompany.restaurante;

import com.mycompany.restaurante.controller.GestorOrdenes;
import com.mycompany.restaurante.modelo.pojo.Orden;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import com.mycompany.restaurante.utils.ConexionBD;
import java.util.List;

public class MainPrueba {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST DE SISTEMA RESTAURANTE 2026 ===");
            
            // Inicializamos el gestor con la conexión simulada
            GestorOrdenes gestor = new GestorOrdenes(ConexionBD.obtenerConexion());
            
            // Ejecutamos el flujo del CU-14 [cite: 2]
            List<Orden> listaParaChef = gestor.obtenerComandasParaChef();
            
            if (listaParaChef.isEmpty()) {
                System.out.println("Resultado: No hay órdenes pendientes [cite: 2]");
            } else {
                for (Orden o : listaParaChef) {
                    System.out.println("\n------------------------------------");
                    System.out.println("ORDEN #" + o.getIdOrden() + " | MESA: " + o.getIdMesa()); // [cite: 17]
                    System.out.println("ESTADO: " + o.getEstado());
                    System.out.println("PLATILLOS A PREPARAR:");
                    
                    for (Object obj : o.getListaPlatillo()) {
                        Platillo p = (Platillo) obj;
                        System.out.println(" > " + p.getNombre() + " [Cantidad: " + p.getCantidad() + "]");
                    }
                }
            }
            
        } catch (Exception e) {
            // Aquí se captura el Error de Conexión Ex-01 [cite: 2]
            System.err.println("\n[ERROR CRÍTICO]: " + e.getMessage());
        }
    }
}

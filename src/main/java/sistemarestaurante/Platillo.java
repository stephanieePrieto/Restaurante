
package sistemarestaurante;


public class Platillo {
   
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private String imagen;

    // Metodo constructor del platillo
    public Platillo(String nombre, String descripcion, double precio, String categoria, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public String getImagen() { return imagen; }
}

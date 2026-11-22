// Paquete "modelo".
package modelo;

// Solo necesitamos LocalDate para la fecha de ingreso y los métodos con "hoy".
import java.time.LocalDate;

/**
 * CLASE ProductoNoPerecedero (subclase de Producto).
 *
 * Representa productos que NO caducan o cuya caducidad no es relevante
 * para la tienda: ferretería (tornillos, clavos), limpieza, granos secos, etc.
 * Hereda todo de Producto y agrega una "categoría".
 */
public class ProductoNoPerecedero extends Producto {

    // Atributo propio: categoría del producto (ej. "Ferretería", "Limpieza", "Abarrotes").
    private final String categoria;

    /** Constructor: datos comunes + categoría. */
    public ProductoNoPerecedero(String codigo, String nombre, int cantidad, double costoUnitario,
                                int stockMinimo, double volumenUnitario, LocalDate fechaIngreso,
                                String categoria) {
        // Primero el padre guarda y valida los datos comunes.
        super(codigo, nombre, cantidad, costoUnitario, stockMinimo, volumenUnitario, fechaIngreso);
        // Luego guardamos el dato propio.
        this.categoria = categoria;
    }

    // Getter del atributo propio.
    public String getCategoria() { return categoria; }

    // ============ POLIMORFISMO: implementación propia de los métodos abstractos ============

    /** Un no perecedero conserva su valor completo: cantidad × costo unitario. */
    @Override
    public double calcularValorInventario(LocalDate hoy) {
        return calcularCostoTotal();
    }

    /** Un no perecedero solo genera alerta por stock bajo. */
    @Override
    public boolean requiereAlerta(LocalDate hoy) {
        return tieneStockBajo();
    }

    /** Texto con el motivo de la alerta (solo puede ser stock bajo). */
    @Override
    public String obtenerDetalleAlerta(LocalDate hoy) {
        if (tieneStockBajo()) {
            return "Stock bajo (" + getCantidad() + " de mínimo " + getStockMinimo() + ").";
        }
        // Cadena vacía: no hay alerta.
        return "";
    }

    @Override
    public String getTipo() {
        return "No perecedero";
    }

    /** Agrega la categoría al texto que arma el padre. */
    @Override
    public String toString() {
        return super.toString() + " | Categoría: " + categoria;
    }
}

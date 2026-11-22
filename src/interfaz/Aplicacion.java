// Paquete "interfaz".
package interfaz;

import modelo.ProductoNoPerecedero;
import modelo.ProductoPerecedero;
import negocio.Inventario;
import negocio.InventarioException;

import java.time.LocalDate;

/**
 * CLASE Aplicacion: PUNTO DE ENTRADA del programa.
 *
 * Java siempre empieza a ejecutar por el método "main".
 * Aquí se configuran las restricciones de la empresa (presupuesto y espacio),
 * se cargan algunos productos de ejemplo y se abre el menú.
 */
public class Aplicacion {

    /** Presupuesto mensual de compras de la tienda (USD). */
    public static final double PRESUPUESTO_MENSUAL = 3000.00;

    /** Capacidad de la bodega en litros (aprox. 2 m³). */
    public static final double CAPACIDAD_BODEGA_LITROS = 2000.0;

    /**
     * Método principal. "String[] args" son datos que se pueden pasar al programa
     * al ejecutarlo; aquí no los usamos.
     */
    public static void main(String[] args) {
        // 1) Creamos el inventario con las restricciones de la empresa.
        Inventario inventario = new Inventario(PRESUPUESTO_MENSUAL, CAPACIDAD_BODEGA_LITROS);
        // 2) Cargamos productos de ejemplo para poder probar el sistema.
        cargarDatosIniciales(inventario);
        // 3) Creamos el menú y lo iniciamos.
        MenuInventario menu = new MenuInventario(inventario);
        menu.iniciar();
    }

    /**
     * Carga seis productos de ejemplo. Las fechas se calculan a partir de HOY,
     * para que las alertas de caducidad funcionen en cualquier fecha de ejecución.
     */
    private static void cargarDatosIniciales(Inventario inventario) {
        LocalDate hoy = LocalDate.now();
        try {
            // Perecederos: (código, nombre, cantidad, costo, stock mín., volumen L, ingreso, caducidad, refrigerado)
            inventario.registrarProducto(new ProductoPerecedero("P001", "Leche entera 1 L", 40, 0.95, 15, 1.0,
                    hoy, hoy.plusDays(10), true));
            inventario.registrarProducto(new ProductoPerecedero("P002", "Yogur natural 1 L", 12, 1.80, 10, 1.0,
                    hoy, hoy.plusDays(3), true));
            inventario.registrarProducto(new ProductoPerecedero("P003", "Pan de molde", 6, 1.60, 8, 2.5,
                    hoy, hoy.plusDays(5), false));
            // No perecederos: (código, nombre, cantidad, costo, stock mín., volumen L, ingreso, categoría)
            inventario.registrarProducto(new ProductoNoPerecedero("N001", "Arroz 2 kg", 60, 1.70, 20, 2.0,
                    hoy, "Abarrotes"));
            inventario.registrarProducto(new ProductoNoPerecedero("N002", "Detergente 1 kg", 8, 2.40, 10, 1.5,
                    hoy, "Limpieza"));
            inventario.registrarProducto(new ProductoNoPerecedero("N003", "Clavos 2\" (caja)", 30, 1.10, 10, 0.5,
                    hoy, "Ferretería"));
        } catch (InventarioException e) {
            // Si algo falla al cargar los ejemplos, lo informamos pero el programa sigue.
            System.out.println("Aviso al cargar datos iniciales: " + e.getMessage());
        }
    }
}

// Esta clase vive en el paquete "negocio".
package negocio;

/**
 * EXCEPCIÓN PERSONALIZADA del sistema de inventario.
 *
 * Una "excepción" es un aviso de error que interrumpe el proceso normal.
 * Creamos la nuestra (heredando de Exception) para que todos los errores de
 * reglas de negocio (presupuesto insuficiente, código repetido, campo vacío...)
 * lleguen a la pantalla con un mensaje claro y el programa NO se cierre.
 */
public class InventarioException extends Exception {

    // Número de versión que Java recomienda en las clases de excepción (buena práctica).
    private static final long serialVersionUID = 1L;

    /** Recibe el mensaje de error y se lo pasa a la clase padre (Exception). */
    public InventarioException(String mensaje) {
        super(mensaje);
    }
}

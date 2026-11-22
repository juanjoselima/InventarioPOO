// Paquete "negocio".
package negocio;

// Herramientas para convertir texto en fechas y detectar fechas mal escritas.
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
// ResolverStyle.STRICT rechaza fechas imposibles como 31/02/2026.
import java.time.format.ResolverStyle;

/**
 * CLASE ValidadorEntrada.
 *
 * Todo lo que el usuario escribe por teclado llega como TEXTO (String).
 * Esta clase revisa ese texto ANTES de usarlo:
 *   - que no esté vacío (campos obligatorios),
 *   - que sea un número cuando se espera un número,
 *   - que la fecha tenga el formato correcto.
 * Si algo está mal, lanza InventarioException con un mensaje claro.
 *
 * Todos sus métodos son "static": se usan sin crear un objeto,
 * escribiendo ValidadorEntrada.nombreDelMetodo(...).
 */
public class ValidadorEntrada {

    // Formato estricto de fecha: dd/MM/uuuu (en modo STRICT, el año se escribe "uuuu").
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Constructor privado: nadie necesita crear objetos de esta clase,
     * porque todos sus métodos son estáticos.
     */
    private ValidadorEntrada() { }

    /**
     * Verifica que un campo de texto obligatorio no esté vacío.
     * @param valor el texto escrito por el usuario.
     * @param campo el nombre del campo, para armar el mensaje de error.
     * @return el texto sin espacios al inicio ni al final.
     */
    public static String validarTextoObligatorio(String valor, String campo) throws InventarioException {
        // "valor == null" = no llegó nada; "isBlank()" = está vacío o solo tiene espacios.
        if (valor == null || valor.isBlank()) {
            throw new InventarioException("El campo '" + campo + "' es obligatorio y no puede quedar vacío.");
        }
        // trim() quita espacios sobrantes: "  Tornillo " -> "Tornillo".
        return valor.trim();
    }

    /**
     * Convierte un texto en número entero (int) y verifica que sea >= minimo.
     * Ejemplo de error del caso de prueba CP1: cantidad vacía.
     */
    public static int convertirEntero(String valor, String campo, int minimo) throws InventarioException {
        // Primero: ¿está vacío? Reutilizamos el método anterior.
        String texto = validarTextoObligatorio(valor, campo);
        int numero; // Aquí guardaremos el número convertido.
        try {
            // Integer.parseInt convierte "25" en 25. Si el texto es "abc", lanza NumberFormatException.
            numero = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            // Atrapamos el error técnico y lo cambiamos por un mensaje entendible.
            throw new InventarioException("El campo '" + campo + "' debe ser un número entero (ej. 25).");
        }
        // Verificamos el valor mínimo permitido.
        if (numero < minimo) {
            throw new InventarioException("El campo '" + campo + "' debe ser mayor o igual a " + minimo + ".");
        }
        return numero;
    }

    /**
     * Convierte un texto en número decimal (double) y verifica que sea mayor que cero.
     * Acepta tanto punto como coma decimal: "0.50" o "0,50".
     */
    public static double convertirDecimalPositivo(String valor, String campo) throws InventarioException {
        String texto = validarTextoObligatorio(valor, campo);
        double numero;
        try {
            // replace(',', '.') cambia la coma por punto, porque Java usa punto decimal.
            numero = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new InventarioException("El campo '" + campo + "' debe ser un número (ej. 0.50).");
        }
        if (numero <= 0) {
            throw new InventarioException("El campo '" + campo + "' debe ser mayor que cero.");
        }
        return numero;
    }

    /** Convierte un texto con formato dd/MM/aaaa en una fecha (LocalDate). */
    public static LocalDate convertirFecha(String valor, String campo) throws InventarioException {
        String texto = validarTextoObligatorio(valor, campo);
        try {
            return LocalDate.parse(texto, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new InventarioException("El campo '" + campo
                    + "' debe ser una fecha válida con formato dd/mm/aaaa (ej. 15/12/2026).");
        }
    }

    /** Convierte una respuesta S/N en verdadero o falso. */
    public static boolean convertirSiNo(String valor, String campo) throws InventarioException {
        // toUpperCase() pasa a mayúsculas, así "s" y "S" valen igual.
        String texto = validarTextoObligatorio(valor, campo).toUpperCase();
        if (texto.equals("S")) {
            return true;
        }
        if (texto.equals("N")) {
            return false;
        }
        throw new InventarioException("El campo '" + campo + "' solo admite S (sí) o N (no).");
    }
}

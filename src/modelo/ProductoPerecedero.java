// Esta clase también vive en el paquete "modelo".
package modelo;

// Herramientas de fechas que vamos a usar.
import java.time.LocalDate;
// ChronoUnit.DAYS sirve para contar cuántos días hay entre dos fechas.
import java.time.temporal.ChronoUnit;

/**
 * CLASE ProductoPerecedero (subclase o "clase hija" de Producto).
 *
 * Representa productos que CADUCAN: lácteos, pan, embutidos, etc.
 * "extends Producto" significa que HEREDA todo lo de Producto
 * (código, nombre, cantidad, costo, métodos...) y además agrega lo suyo:
 * la fecha de caducidad y si necesita refrigeración.
 */
public class ProductoPerecedero extends Producto {

    /** Días de anticipación con que se avisa que un producto está por caducar. */
    public static final int DIAS_ALERTA_CADUCIDAD = 7;

    /** Porcentaje del costo que se reconoce como valor si el producto está por caducar (50 %). */
    public static final double FACTOR_PROXIMO_A_CADUCAR = 0.5;

    // Atributos PROPIOS de esta clase (los demás ya vienen heredados de Producto).
    private final LocalDate fechaCaducidad;       // Último día en que el producto es apto.
    private final boolean requiereRefrigeracion;  // true = va a la nevera; false = estantería.

    /**
     * Constructor de la clase hija.
     * Recibe los datos comunes + los datos propios del perecedero.
     */
    public ProductoPerecedero(String codigo, String nombre, int cantidad, double costoUnitario,
                              int stockMinimo, double volumenUnitario, LocalDate fechaIngreso,
                              LocalDate fechaCaducidad, boolean requiereRefrigeracion) {
        // "super(...)" llama al constructor de la clase padre (Producto) para que guarde
        // y valide los datos comunes. SIEMPRE debe ser la primera línea del constructor.
        super(codigo, nombre, cantidad, costoUnitario, stockMinimo, volumenUnitario, fechaIngreso);
        // Regla de negocio: no se registran productos que ya llegan caducados.
        if (fechaCaducidad.isBefore(fechaIngreso)) {
            throw new IllegalArgumentException("La fecha de caducidad no puede ser anterior a la fecha de ingreso.");
        }
        // Guardamos los datos propios del perecedero.
        this.fechaCaducidad = fechaCaducidad;
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    // Getters de los atributos propios.
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public boolean isRequiereRefrigeracion() { return requiereRefrigeracion; }

    /**
     * Cuántos días faltan para que caduque, contando desde "hoy".
     * Si el resultado es negativo, el producto YA caducó.
     * "long" es un número entero que admite valores más grandes que "int".
     */
    public long diasParaCaducar(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, fechaCaducidad);
    }

    /** true si la fecha de caducidad ya pasó. */
    public boolean estaCaducado(LocalDate hoy) {
        return diasParaCaducar(hoy) < 0;
    }

    /** true si caduca dentro de los próximos 7 días (y todavía no ha caducado). */
    public boolean estaProximoACaducar(LocalDate hoy) {
        long dias = diasParaCaducar(hoy);
        // "&&" significa "Y": deben cumplirse las dos condiciones.
        return dias >= 0 && dias <= DIAS_ALERTA_CADUCIDAD;
    }

    // ============ POLIMORFISMO: implementación propia de los métodos abstractos ============

    /**
     * Valor del inventario para un perecedero:
     *  - Caducado            -> vale $0 (es pérdida).
     *  - Por caducar (≤7 d.) -> vale el 50 % de su costo (se tendría que rematar).
     *  - Vigente             -> vale el 100 % de su costo.
     */
    @Override
    public double calcularValorInventario(LocalDate hoy) {
        if (estaCaducado(hoy)) {
            return 0.0;
        }
        if (estaProximoACaducar(hoy)) {
            return calcularCostoTotal() * FACTOR_PROXIMO_A_CADUCAR;
        }
        return calcularCostoTotal();
    }

    /** Un perecedero necesita alerta si tiene stock bajo, está por caducar o ya caducó. */
    @Override
    public boolean requiereAlerta(LocalDate hoy) {
        // "||" significa "O": basta con que se cumpla una de las condiciones.
        return tieneStockBajo() || estaProximoACaducar(hoy) || estaCaducado(hoy);
    }

    /** Arma un texto con todos los motivos de alerta que apliquen. */
    @Override
    public String obtenerDetalleAlerta(LocalDate hoy) {
        // StringBuilder permite ir "pegando" textos uno tras otro de forma eficiente.
        StringBuilder motivos = new StringBuilder();
        if (estaCaducado(hoy)) {
            motivos.append("CADUCADO hace ").append(-diasParaCaducar(hoy)).append(" día(s). ");
        } else if (estaProximoACaducar(hoy)) {
            motivos.append("Caduca en ").append(diasParaCaducar(hoy)).append(" día(s). ");
        }
        if (tieneStockBajo()) {
            motivos.append("Stock bajo (").append(getCantidad())
                   .append(" de mínimo ").append(getStockMinimo()).append("). ");
        }
        // trim() quita el espacio sobrante del final.
        return motivos.toString().trim();
    }

    /** Un producto caducado NO se puede vender (sobrescribe el comportamiento del padre). */
    @Override
    public boolean puedeVenderse(LocalDate hoy) {
        // "!" significa "NO": se puede vender si NO está caducado.
        return !estaCaducado(hoy);
    }

    @Override
    public String getTipo() {
        return "Perecedero";
    }

    /** Agrega al texto del padre los datos propios del perecedero. */
    @Override
    public String toString() {
        // "super.toString()" reutiliza el texto que ya arma la clase Producto.
        // El operador "? :" es un "if" corto: condición ? valorSiVerdadero : valorSiFalso.
        return super.toString() + " | Caduca: " + fechaCaducidad.format(FORMATO_FECHA)
                + (requiereRefrigeracion ? " | Refrigerado" : "");
    }
}

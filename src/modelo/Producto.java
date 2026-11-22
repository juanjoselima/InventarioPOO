// "package" indica en qué carpeta (paquete) vive esta clase. Esta vive en "modelo".
package modelo;

// "import" trae herramientas que ya existen en Java para poder usarlas aquí.
// LocalDate sirve para manejar fechas (día, mes, año) sin hora.
import java.time.LocalDate;
// DateTimeFormatter sirve para mostrar una fecha con el formato que queramos (dd/MM/yyyy).
import java.time.format.DateTimeFormatter;
// Locale.US hace que los decimales se muestren con punto (0.50) y no con coma (0,50).
import java.util.Locale;

/**
 * CLASE ABSTRACTA Producto (superclase o "clase padre").
 *
 * Representa lo que TODOS los productos del inventario tienen en común,
 * sean perecederos o no perecederos.
 *
 * Es "abstract" porque nunca se crea un "Producto" a secas: siempre se crea
 * un ProductoPerecedero o un ProductoNoPerecedero (clases hijas).
 *
 * Conceptos de POO que aplica:
 *  - ENCAPSULAMIENTO: todos los atributos son "private" y solo se modifican
 *    a través de métodos que validan los datos.
 *  - HERENCIA: las clases hijas heredan todo lo que está aquí.
 *  - POLIMORFISMO: declara métodos abstractos que cada hija implementa a su manera.
 */
public abstract class Producto {

    // Constante compartida por todas las clases: formato de fecha día/mes/año.
    // "static" = pertenece a la clase, no a cada objeto. "final" = no se puede cambiar.
    // "protected" = la pueden usar esta clase y sus clases hijas.
    protected static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===================== ATRIBUTOS (datos del producto) =====================
    // "private" = nadie fuera de esta clase puede leerlos ni cambiarlos directamente.
    private final String codigo;          // Identificador único, ej. "P001". "final": no cambia nunca.
    private final String nombre;          // Nombre del producto, ej. "Leche entera 1L".
    private int cantidad;                 // Unidades en bodega. "int" = número entero.
    private final double costoUnitario;   // Precio de compra de una unidad en USD. "double" = número con decimales.
    private final int stockMinimo;        // Si la cantidad baja hasta este número, se genera alerta.
    private final double volumenUnitario; // Espacio que ocupa una unidad en la bodega, en litros.
    private final LocalDate fechaIngreso; // Fecha en que el producto se registró en el sistema.

    // ===================== CONSTRUCTOR =====================
    /**
     * El constructor es el método que se ejecuta al crear un objeto con "new".
     * Recibe los datos y los guarda en los atributos.
     * Valida que los datos tengan sentido; si no, lanza un error
     * (IllegalArgumentException) y el objeto NO se crea.
     */
    public Producto(String codigo, String nombre, int cantidad, double costoUnitario,
                    int stockMinimo, double volumenUnitario, LocalDate fechaIngreso) {
        // Si la cantidad es negativa, detenemos todo con un mensaje claro.
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        // El costo de compra debe ser mayor que cero.
        if (costoUnitario <= 0) {
            throw new IllegalArgumentException("El costo unitario debe ser mayor que cero.");
        }
        // El stock mínimo no puede ser negativo.
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
        // Todo producto ocupa algo de espacio, así que el volumen debe ser mayor que cero.
        if (volumenUnitario <= 0) {
            throw new IllegalArgumentException("El volumen unitario debe ser mayor que cero.");
        }
        // "this.codigo" es el atributo del objeto; "codigo" es el dato que llegó por parámetro.
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.stockMinimo = stockMinimo;
        this.volumenUnitario = volumenUnitario;
        this.fechaIngreso = fechaIngreso;
    }

    // ===================== GETTERS (métodos para LEER los atributos) =====================
    // Como los atributos son privados, otras clases los leen con estos métodos públicos.
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public double getCostoUnitario() { return costoUnitario; }
    public int getStockMinimo() { return stockMinimo; }
    public double getVolumenUnitario() { return volumenUnitario; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }

    // ===================== MÉTODOS QUE MODIFICAN EL STOCK (encapsulamiento) =====================
    // No existe un "setCantidad" libre: solo se puede sumar o restar stock con validación.

    /** Suma unidades al stock (cuando llega mercadería). */
    public void aumentarStock(int unidades) {
        // No tiene sentido "aumentar" cero o un número negativo.
        if (unidades <= 0) {
            throw new IllegalArgumentException("Las unidades a ingresar deben ser mayores que cero.");
        }
        // "+=" significa: cantidad = cantidad + unidades.
        this.cantidad += unidades;
    }

    /** Resta unidades del stock (cuando se vende o se despacha). */
    public void disminuirStock(int unidades) {
        if (unidades <= 0) {
            throw new IllegalArgumentException("Las unidades a retirar deben ser mayores que cero.");
        }
        // No se puede sacar más de lo que hay en bodega.
        if (unidades > this.cantidad) {
            throw new IllegalArgumentException("Stock insuficiente: solo hay " + this.cantidad + " unidades.");
        }
        // "-=" significa: cantidad = cantidad - unidades.
        this.cantidad -= unidades;
    }

    // ===================== MÉTODOS DE CÁLCULO COMUNES =====================

    /** Cuánto dinero costó comprar todas las unidades actuales: cantidad × costo unitario. */
    public double calcularCostoTotal() {
        return cantidad * costoUnitario;
    }

    /** Cuántos litros de bodega ocupa el producto: cantidad × volumen unitario. */
    public double calcularEspacioOcupado() {
        return cantidad * volumenUnitario;
    }

    /** Devuelve true (verdadero) si la cantidad llegó al stock mínimo o menos. */
    public boolean tieneStockBajo() {
        return cantidad <= stockMinimo;
    }

    /**
     * Indica si el producto se puede vender en la fecha indicada.
     * Por defecto, cualquier producto se puede vender. La clase ProductoPerecedero
     * SOBRESCRIBE este método para impedir vender productos caducados (polimorfismo).
     */
    public boolean puedeVenderse(LocalDate hoy) {
        return true;
    }

    // ===================== MÉTODOS ABSTRACTOS (POLIMORFISMO) =====================
    // Solo se declara QUÉ hacen; el CÓMO lo escribe cada clase hija.
    // Por eso no tienen llaves { } sino punto y coma.

    /** Valor real (contable) del producto en bodega a la fecha indicada. */
    public abstract double calcularValorInventario(LocalDate hoy);

    /** true si el producto necesita atención (stock bajo, por caducar, etc.). */
    public abstract boolean requiereAlerta(LocalDate hoy);

    /** Texto que explica el motivo de la alerta. */
    public abstract String obtenerDetalleAlerta(LocalDate hoy);

    /** Nombre del tipo de producto: "Perecedero" o "No perecedero". */
    public abstract String getTipo();

    // ===================== REPRESENTACIÓN EN TEXTO =====================
    /**
     * toString() convierte el objeto en un texto legible para mostrarlo en pantalla.
     * "@Override" indica que estamos reescribiendo un método que ya existía en Java.
     */
    @Override
    public String toString() {
        // String.format arma un texto con "huecos": %s = texto, %d = entero, %.2f = decimal con 2 cifras.
        // %-14s = texto alineado a la izquierda ocupando 14 espacios (para que la tabla se vea ordenada).
        return String.format(Locale.US, "[%s] %-22s | %-13s | Stock: %4d (mín. %3d) | Costo u.: $%.2f",
                codigo, nombre, getTipo(), cantidad, stockMinimo, costoUnitario);
    }
}

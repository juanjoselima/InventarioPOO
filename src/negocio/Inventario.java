// Paquete "negocio": aquí están las REGLAS de la empresa.
package negocio;

// Importamos las clases del paquete "modelo" para poder usarlas aquí.
import modelo.Producto;

// Herramientas de Java para fechas y listas.
import java.time.LocalDate;
import java.util.ArrayList;      // Lista que crece automáticamente.
import java.util.Collections;    // Utilidades para listas (ej. lista de solo lectura).
import java.util.List;           // "Tipo" general de las listas.
import java.util.Locale;

/**
 * CLASE Inventario (lógica de negocio).
 *
 * Administra la lista de productos y hace cumplir las RESTRICCIONES del problema:
 *  - Presupuesto de compras: no se puede comprar si no alcanza el dinero.
 *  - Espacio de bodega: no se puede ingresar mercadería si no cabe.
 *  - Código único: no puede haber dos productos con el mismo código.
 *  - Stock: no se puede vender más de lo que hay, ni vender productos caducados.
 *
 * Gracias al POLIMORFISMO, esta clase trabaja con "Producto" en general y no
 * necesita saber si cada uno es perecedero o no: cada objeto responde a su manera.
 */
public class Inventario {

    // Lista donde se guardan TODOS los productos (perecederos y no perecederos juntos).
    // "List<Producto>" significa "lista que contiene objetos de tipo Producto".
    private final List<Producto> productos;

    // Dinero que queda disponible para comprar mercadería (USD).
    private double presupuestoDisponible;

    // Capacidad total de la bodega en litros. No cambia: "final".
    private final double capacidadBodega;

    /**
     * Constructor: crea un inventario vacío con un presupuesto y una capacidad iniciales.
     */
    public Inventario(double presupuestoInicial, double capacidadBodega) {
        // Validamos que los valores de configuración tengan sentido.
        if (presupuestoInicial < 0 || capacidadBodega <= 0) {
            throw new IllegalArgumentException("Presupuesto y capacidad deben ser valores positivos.");
        }
        this.productos = new ArrayList<>(); // Creamos la lista vacía.
        this.presupuestoDisponible = presupuestoInicial;
        this.capacidadBodega = capacidadBodega;
    }

    // ===================== RF1: REGISTRAR NUEVO PRODUCTO =====================
    /**
     * Agrega un producto nuevo al inventario, verificando todas las restricciones.
     * Si alguna falla, lanza InventarioException y NO se agrega nada.
     */
    public void registrarProducto(Producto producto) throws InventarioException {
        // 1) Código único.
        if (buscarPorCodigo(producto.getCodigo()) != null) {
            throw new InventarioException("Ya existe un producto con el código " + producto.getCodigo() + ".");
        }
        // 2) Presupuesto: comprar el lote cuesta cantidad × costo unitario.
        double costoCompra = producto.calcularCostoTotal();
        verificarPresupuesto(costoCompra);
        // 3) Espacio: el lote ocupa cantidad × volumen unitario.
        double espacioNecesario = producto.calcularEspacioOcupado();
        verificarEspacio(espacioNecesario);
        // 4) Si todo está bien: se agrega a la lista y se descuenta el presupuesto.
        productos.add(producto);
        presupuestoDisponible -= costoCompra;
    }

    // ===================== RF3: REGISTRAR ENTRADA (REPOSICIÓN) =====================
    /** Suma unidades a un producto existente, verificando presupuesto y espacio. */
    public void registrarEntrada(String codigo, int unidades) throws InventarioException {
        Producto producto = obtenerProductoExistente(codigo); // Lanza error si no existe.
        if (unidades <= 0) {
            throw new InventarioException("Las unidades a ingresar deben ser mayores que cero.");
        }
        double costoCompra = unidades * producto.getCostoUnitario();
        verificarPresupuesto(costoCompra);
        double espacioNecesario = unidades * producto.getVolumenUnitario();
        verificarEspacio(espacioNecesario);
        producto.aumentarStock(unidades);       // Encapsulamiento: el producto actualiza su propio stock.
        presupuestoDisponible -= costoCompra;
    }

    // ===================== RF4: REGISTRAR SALIDA (VENTA) =====================
    /** Resta unidades de un producto (venta o despacho). */
    public void registrarSalida(String codigo, int unidades, LocalDate hoy) throws InventarioException {
        Producto producto = obtenerProductoExistente(codigo);
        if (unidades <= 0) {
            throw new InventarioException("Las unidades a retirar deben ser mayores que cero.");
        }
        // Polimorfismo: cada tipo de producto decide si puede venderse.
        if (!producto.puedeVenderse(hoy)) {
            throw new InventarioException("No se puede vender '" + producto.getNombre()
                    + "': el producto está caducado. Debe darse de baja.");
        }
        if (unidades > producto.getCantidad()) {
            throw new InventarioException("Stock insuficiente de '" + producto.getNombre()
                    + "'. Disponible: " + producto.getCantidad() + " unidades.");
        }
        producto.disminuirStock(unidades);
    }

    // ===================== RF2: CONSULTAR / LISTAR =====================
    /**
     * Devuelve la lista de productos en modo "solo lectura", para que nadie
     * fuera de esta clase pueda agregar o borrar productos saltándose las reglas.
     */
    public List<Producto> listarProductos() {
        return Collections.unmodifiableList(productos);
    }

    /** Busca un producto por su código. Devuelve null si no lo encuentra. */
    public Producto buscarPorCodigo(String codigo) {
        // Recorremos la lista uno por uno ("para cada producto p en productos...").
        for (Producto p : productos) {
            // equalsIgnoreCase compara textos sin importar mayúsculas: "p001" = "P001".
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null; // No se encontró.
    }

    // ===================== RF5: ALERTAS =====================
    /** Devuelve los productos que requieren atención (stock bajo, por caducar, caducados). */
    public List<Producto> obtenerAlertas(LocalDate hoy) {
        List<Producto> conAlerta = new ArrayList<>();
        for (Producto p : productos) {
            // Polimorfismo: cada objeto responde según su clase real.
            if (p.requiereAlerta(hoy)) {
                conAlerta.add(p);
            }
        }
        return conAlerta;
    }

    // ===================== RF6: REPORTE GENERAL =====================
    /** Suma el valor real de todos los productos (cada uno lo calcula a su manera). */
    public double calcularValorTotal(LocalDate hoy) {
        double total = 0;
        for (Producto p : productos) {
            total += p.calcularValorInventario(hoy); // Llamada polimórfica.
        }
        return total;
    }

    /** Suma el costo de compra de todos los productos (sin descuentos por caducidad). */
    public double calcularCostoTotal() {
        double total = 0;
        for (Producto p : productos) {
            total += p.calcularCostoTotal();
        }
        return total;
    }

    /** Litros ocupados en la bodega. */
    public double getEspacioOcupado() {
        double ocupado = 0;
        for (Producto p : productos) {
            ocupado += p.calcularEspacioOcupado();
        }
        return ocupado;
    }

    /** Litros libres en la bodega. */
    public double getEspacioDisponible() {
        return capacidadBodega - getEspacioOcupado();
    }

    public double getPresupuestoDisponible() { return presupuestoDisponible; }
    public double getCapacidadBodega() { return capacidadBodega; }
    public int getTotalProductos() { return productos.size(); }

    // ===================== MÉTODOS PRIVADOS DE APOYO =====================
    // "private": solo se usan dentro de esta clase para no repetir código.

    /** Busca el producto y, si no existe, lanza un error claro. */
    private Producto obtenerProductoExistente(String codigo) throws InventarioException {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) {
            throw new InventarioException("No existe ningún producto con el código " + codigo + ".");
        }
        return producto;
    }

    /** Lanza error si el costo supera el presupuesto disponible. */
    private void verificarPresupuesto(double costo) throws InventarioException {
        if (costo > presupuestoDisponible) {
            throw new InventarioException(String.format(Locale.US,
                    "Presupuesto insuficiente. La compra cuesta $%.2f y solo hay $%.2f disponibles.",
                    costo, presupuestoDisponible));
        }
    }

    /** Lanza error si el espacio necesario supera el espacio libre de la bodega. */
    private void verificarEspacio(double litros) throws InventarioException {
        if (litros > getEspacioDisponible()) {
            throw new InventarioException(String.format(Locale.US,
                    "Espacio insuficiente en bodega. Se necesitan %.1f L y solo hay %.1f L libres.",
                    litros, getEspacioDisponible()));
        }
    }
}

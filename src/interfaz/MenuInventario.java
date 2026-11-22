// Paquete "interfaz": todo lo que tiene que ver con mostrar y leer datos en pantalla.
package interfaz;

// Clases de los otros paquetes que vamos a usar.
import modelo.Producto;
import modelo.ProductoNoPerecedero;
import modelo.ProductoPerecedero;
import negocio.Inventario;
import negocio.InventarioException;
import negocio.ValidadorEntrada;

// Herramientas de Java.
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner; // Scanner lee lo que el usuario escribe en el teclado.

/**
 * CLASE MenuInventario (capa de interfaz / presentación).
 *
 * Muestra el menú por consola, pide los datos al usuario y muestra los resultados.
 * NO contiene reglas de negocio: solo "conversa" con el usuario y delega el
 * trabajo a la clase Inventario (paquete negocio). Esto es SEPARACIÓN EN CAPAS.
 */
public class MenuInventario {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String LINEA = "=".repeat(78); // Línea decorativa de 78 signos "=".

    private final Inventario inventario; // El inventario con el que trabaja el menú.
    private final Scanner teclado;       // Lector del teclado.

    /** Constructor: recibe el inventario ya creado y prepara el lector de teclado. */
    public MenuInventario(Inventario inventario) {
        this.inventario = inventario;
        this.teclado = new Scanner(System.in); // System.in = el teclado.
    }

    /** Bucle principal: muestra el menú hasta que el usuario elija 0 (Salir). */
    public void iniciar() {
        boolean continuar = true; // Mientras sea true, el menú se repite.
        // "while" = "mientras": repite el bloque mientras la condición sea verdadera.
        while (continuar) {
            mostrarMenu();
            String opcion = leer("Seleccione una opción: ");
            // "switch" elige qué hacer según el valor de "opcion".
            switch (opcion) {
                case "1": registrarProducto(); break;
                case "2": listarInventario(); break;
                case "3": registrarEntrada(); break;
                case "4": registrarSalida(); break;
                case "5": mostrarAlertas(); break;
                case "6": mostrarReporte(); break;
                case "0":
                    System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
                    continuar = false; // Rompe el bucle while.
                    break;
                default: // Cualquier otra cosa que no sea 0-6.
                    System.out.println(">> ERROR: Opción no válida. Escriba un número del 0 al 6.");
            }
        }
    }

    /** Imprime el menú principal. */
    private void mostrarMenu() {
        System.out.println();
        System.out.println(LINEA);
        System.out.println("   SISTEMA DE INVENTARIO - Minimarket y Ferretería \"El Vecino\"");
        System.out.println("   Fecha del sistema: " + LocalDate.now().format(FORMATO_FECHA));
        System.out.println(LINEA);
        System.out.println("  1. Registrar nuevo producto");
        System.out.println("  2. Listar inventario");
        System.out.println("  3. Registrar entrada de mercadería (reposición)");
        System.out.println("  4. Registrar salida de mercadería (venta)");
        System.out.println("  5. Ver alertas (stock bajo / caducidad)");
        System.out.println("  6. Reporte general (valor, presupuesto y espacio)");
        System.out.println("  0. Salir");
        System.out.println(LINEA);
    }

    // ===================== RF1 =====================
    /**
     * Pide TODOS los datos primero y luego los valida.
     * Si algún dato falta o es incorrecto, muestra el error y NO registra nada.
     */
    private void registrarProducto() {
        System.out.println("\n--- REGISTRAR NUEVO PRODUCTO ---");
        // 1) Leemos todo como texto (tal cual lo escribe el usuario).
        String tipo = leer("Tipo (1 = Perecedero, 2 = No perecedero): ");
        String codigo = leer("Código (ej. P010): ");
        String nombre = leer("Nombre: ");
        String cantidad = leer("Cantidad: ");
        String precio = leer("Precio de compra unitario (USD): ");
        String stockMinimo = leer("Stock mínimo: ");
        String volumen = leer("Volumen por unidad (litros): ");

        // "try" = intentar; si dentro ocurre una InventarioException, salta al "catch".
        try {
            // 2) Validamos cada dato (el orden define qué error se muestra primero).
            if (!tipo.equals("1") && !tipo.equals("2")) {
                throw new InventarioException("El tipo debe ser 1 (Perecedero) o 2 (No perecedero).");
            }
            String codigoOk = ValidadorEntrada.validarTextoObligatorio(codigo, "Código").toUpperCase();
            String nombreOk = ValidadorEntrada.validarTextoObligatorio(nombre, "Nombre");
            int cantidadOk = ValidadorEntrada.convertirEntero(cantidad, "Cantidad", 1);
            double precioOk = ValidadorEntrada.convertirDecimalPositivo(precio, "Precio");
            int stockMinOk = ValidadorEntrada.convertirEntero(stockMinimo, "Stock mínimo", 0);
            double volumenOk = ValidadorEntrada.convertirDecimalPositivo(volumen, "Volumen");
            LocalDate hoy = LocalDate.now();

            // 3) Creamos el objeto del tipo correcto.
            // La variable es de tipo "Producto" (padre) pero el objeto puede ser
            // de cualquiera de las hijas: esto también es POLIMORFISMO.
            Producto nuevo;
            if (tipo.equals("1")) {
                String fecha = leer("Fecha de caducidad (dd/mm/aaaa): ");
                String refrigera = leer("¿Requiere refrigeración? (S/N): ");
                LocalDate caducidad = ValidadorEntrada.convertirFecha(fecha, "Fecha de caducidad");
                boolean refrigeracion = ValidadorEntrada.convertirSiNo(refrigera, "Refrigeración");
                nuevo = new ProductoPerecedero(codigoOk, nombreOk, cantidadOk, precioOk,
                        stockMinOk, volumenOk, hoy, caducidad, refrigeracion);
            } else {
                String categoria = leer("Categoría (ej. Ferretería, Limpieza, Abarrotes): ");
                String categoriaOk = ValidadorEntrada.validarTextoObligatorio(categoria, "Categoría");
                nuevo = new ProductoNoPerecedero(codigoOk, nombreOk, cantidadOk, precioOk,
                        stockMinOk, volumenOk, hoy, categoriaOk);
            }

            // 4) La capa de negocio verifica código único, presupuesto y espacio.
            inventario.registrarProducto(nuevo);
            System.out.println(">> ÉXITO: Producto registrado correctamente.");
            System.out.println("   " + nuevo);
            System.out.printf(Locale.US, "   Presupuesto restante: $%.2f | Espacio libre: %.1f L%n",
                    inventario.getPresupuestoDisponible(), inventario.getEspacioDisponible());

        } catch (InventarioException e) {
            // Errores de validación o de reglas de negocio.
            System.out.println(">> ERROR: " + e.getMessage());
            System.out.println("   El producto NO fue registrado.");
        } catch (IllegalArgumentException e) {
            // Errores detectados por los constructores del modelo (ej. caducidad pasada).
            System.out.println(">> ERROR: " + e.getMessage());
            System.out.println("   El producto NO fue registrado.");
        }
    }

    // ===================== RF2 =====================
    private void listarInventario() {
        System.out.println("\n--- INVENTARIO ACTUAL ---");
        List<Producto> lista = inventario.listarProductos();
        // isEmpty() = true si la lista no tiene elementos.
        if (lista.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return; // Sale del método.
        }
        for (Producto p : lista) {
            System.out.println(p); // Llama automáticamente a toString() (polimorfismo).
        }
        System.out.println("Total de productos: " + lista.size());
    }

    // ===================== RF3 =====================
    private void registrarEntrada() {
        System.out.println("\n--- REGISTRAR ENTRADA (REPOSICIÓN) ---");
        String codigo = leer("Código del producto: ");
        String unidades = leer("Unidades que ingresan: ");
        try {
            String codigoOk = ValidadorEntrada.validarTextoObligatorio(codigo, "Código");
            int unidadesOk = ValidadorEntrada.convertirEntero(unidades, "Unidades", 1);
            inventario.registrarEntrada(codigoOk, unidadesOk);
            Producto p = inventario.buscarPorCodigo(codigoOk);
            System.out.println(">> ÉXITO: Entrada registrada. Nuevo stock de '" + p.getNombre()
                    + "': " + p.getCantidad() + " unidades.");
            System.out.printf(Locale.US, "   Presupuesto restante: $%.2f%n", inventario.getPresupuestoDisponible());
        } catch (InventarioException e) {
            System.out.println(">> ERROR: " + e.getMessage());
        }
    }

    // ===================== RF4 =====================
    private void registrarSalida() {
        System.out.println("\n--- REGISTRAR SALIDA (VENTA) ---");
        String codigo = leer("Código del producto: ");
        String unidades = leer("Unidades que salen: ");
        try {
            String codigoOk = ValidadorEntrada.validarTextoObligatorio(codigo, "Código");
            int unidadesOk = ValidadorEntrada.convertirEntero(unidades, "Unidades", 1);
            inventario.registrarSalida(codigoOk, unidadesOk, LocalDate.now());
            Producto p = inventario.buscarPorCodigo(codigoOk);
            System.out.println(">> ÉXITO: Salida registrada. Stock restante de '" + p.getNombre()
                    + "': " + p.getCantidad() + " unidades.");
            if (p.tieneStockBajo()) {
                System.out.println("   AVISO: el producto quedó con stock bajo. Considere reponerlo.");
            }
        } catch (InventarioException e) {
            System.out.println(">> ERROR: " + e.getMessage());
        }
    }

    // ===================== RF5 =====================
    private void mostrarAlertas() {
        System.out.println("\n--- ALERTAS DEL INVENTARIO ---");
        LocalDate hoy = LocalDate.now();
        List<Producto> alertas = inventario.obtenerAlertas(hoy);
        if (alertas.isEmpty()) {
            System.out.println("Sin alertas. Todo el inventario está en orden.");
            return;
        }
        for (Producto p : alertas) {
            // Cada producto explica su propia alerta (polimorfismo).
            System.out.println("[" + p.getCodigo() + "] " + p.getNombre() + " -> " + p.obtenerDetalleAlerta(hoy));
        }
        System.out.println("Productos con alerta: " + alertas.size());
    }

    // ===================== RF6 =====================
    private void mostrarReporte() {
        System.out.println("\n--- REPORTE GENERAL ---");
        LocalDate hoy = LocalDate.now();
        double costo = inventario.calcularCostoTotal();
        double valor = inventario.calcularValorTotal(hoy);
        double ocupado = inventario.getEspacioOcupado();
        double capacidad = inventario.getCapacidadBodega();
        // printf imprime con formato; %n = salto de línea; %% = el símbolo %.
        System.out.printf(Locale.US, "Productos registrados ........: %d%n", inventario.getTotalProductos());
        System.out.printf(Locale.US, "Costo de compra del stock ....: $%.2f%n", costo);
        System.out.printf(Locale.US, "Valor real del inventario ....: $%.2f%n", valor);
        System.out.printf(Locale.US, "Pérdida estimada por caducidad: $%.2f%n", costo - valor);
        System.out.printf(Locale.US, "Presupuesto disponible .......: $%.2f%n", inventario.getPresupuestoDisponible());
        System.out.printf(Locale.US, "Espacio ocupado en bodega ....: %.1f L de %.1f L (%.1f%%)%n",
                ocupado, capacidad, ocupado * 100 / capacidad);
    }

    // ===================== APOYO =====================
    /**
     * Muestra un mensaje y devuelve lo que el usuario escriba.
     * Si la entrada se termina (no hay más líneas), devuelve "0" para salir sin errores.
     */
    private String leer(String mensaje) {
        System.out.print(mensaje); // print (sin "ln") deja el cursor en la misma línea.
        if (!teclado.hasNextLine()) {
            return "0";
        }
        return teclado.nextLine().trim();
    }
}

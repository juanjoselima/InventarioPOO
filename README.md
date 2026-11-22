# Sistema de Inventario POO — Minimarket y Ferretería "El Vecino"

Proyecto de la asignatura **Programación II** (UDLA). Sistema de consola en Java que gestiona el inventario de una pequeña empresa aplicando **encapsulamiento, herencia y polimorfismo**.

## Funcionalidades

| Código | Funcionalidad |
|---|---|
| RF1 | Registrar nuevo producto (perecedero o no perecedero) con validación de campos obligatorios, código único, presupuesto y espacio de bodega |
| RF2 | Listar el inventario |
| RF3 | Registrar entrada de mercadería (reposición) |
| RF4 | Registrar salida de mercadería (venta); impide vender más del stock o productos caducados |
| RF5 | Ver alertas de stock bajo, productos por caducar (≤ 7 días) y caducados |
| RF6 | Reporte general: costo, valor real del inventario, pérdida por caducidad, presupuesto y ocupación de bodega |

## Estructura del proyecto

```
src/
├── modelo/       Clases que representan los datos
│   ├── Producto.java              (clase abstracta)
│   ├── ProductoPerecedero.java    (hereda de Producto)
│   └── ProductoNoPerecedero.java  (hereda de Producto)
├── negocio/      Reglas y validaciones
│   ├── Inventario.java
│   ├── ValidadorEntrada.java
│   └── InventarioException.java
└── interfaz/     Interacción con el usuario (consola)
    ├── Aplicacion.java            (contiene el método main)
    └── MenuInventario.java
```

## Requisitos

- JDK 17 o superior.
- Cualquier IDE de Java (NetBeans, IntelliJ IDEA, Eclipse o VS Code).

## Cómo ejecutar

**Desde un IDE:** abrir la carpeta del proyecto, marcar `src` como carpeta de fuentes y ejecutar la clase `interfaz.Aplicacion`.

**Desde la terminal** (en la carpeta raíz del proyecto):

```bash
javac -encoding UTF-8 -d out src/modelo/*.java src/negocio/*.java src/interfaz/*.java
java -cp out interfaz.Aplicacion
```

## Parámetros de la empresa

- Presupuesto mensual de compras: **USD 3 000,00**
- Capacidad de bodega: **2 000 litros** (aprox. 2 m³)
- Alerta de caducidad: **7 días** de anticipación

Al iniciar, el sistema carga seis productos de ejemplo cuyas fechas se calculan a partir del día de ejecución.

## Declaración de uso de IA

Se utilizó Claude (Anthropic) como herramienta de apoyo al aprendizaje. El detalle de la herramienta y el prompt empleado constan en el informe técnico.

# Tarea Pizzería

Aplicación de consola desarrollada en **Kotlin** para gestionar los pedidos de una pizzería. El proyecto permite registrar clientes, seleccionar productos, controlar el inventario, calcular descuentos e impuestos y generar reportes del día.

## Funcionalidades

- Registro de clientes y validación de:
  - Nombre.
  - Correo electrónico.
  - Teléfono móvil chileno.
- Registro de pedidos mediante un menú interactivo.
- Catálogo de pizzas, bebidas y extras.
- Control de inventario por ingrediente.
- Verificación de disponibilidad antes de confirmar un pedido.
- Reabastecimiento interactivo de ingredientes.
- Asignación de repartidores según la dirección del cliente.
- Cálculo de:
  - Subtotal.
  - IVA del 19%.
  - Descuento por cantidad de pizzas.
  - Descuento por promoción.
- Promociones basadas en un rango de fechas.
- Generación de resumen individual de pedidos.
- Reporte de pedidos del día.
- Reporte del inventario.
- Directorio de clientes y repartidores.
- Combinación de pedidos del mismo cliente mediante el operador `+`.
- Ejemplos de desestructuración, igualdad con `equals`/`hashCode`, anotaciones y reflection.

## Reglas principales

### Teléfonos

Los teléfonos ingresados por el usuario se normalizan al formato:

```text
+56 9 1234 1234
```

También se acepta el número local de 9 dígitos, asumiendo automáticamente el código de país `56`.

### Descuentos y promociones

- **IVA:** 19%.
- **Descuento por cantidad:** 10% cuando el pedido contiene más de 3 pizzas.
- **Descuento de promoción:** 15% sobre los productos promocionables.
- **Periodo de promoción:** desde el 1 de noviembre hasta el 31 de diciembre de 2026.

## Estructura del proyecto

```text
TareaPizzeria/
├── README.md
├── TareaPizzeria.iml
└── src/
    ├── Main.kt
    │
    ├── model/
    │   ├── Bebida.kt
    │   ├── Cliente.kt
    │   ├── Extra.kt
    │   ├── Ingrediente.kt
    │   ├── Pedido.kt
    │   ├── Pizza.kt
    │   ├── Producto.kt
    │   ├── Promocionable.kt
    │   ├── Repartidor.kt
    │   └── Usuario.kt
    │
    ├── reports/
    │   ├── Reportes.kt
    │   └── ResumenFinal.kt
    │
    ├── services/
    │   ├── Inventario.kt
    │   └── Registro.kt
    │
    └── utils/
        ├── Calculos.kt
        ├── Entrada.kt
        ├── Formato.kt
        ├── Reflexion.kt
        └── Validaciones.kt
```

### Descripción de los paquetes

- `model`: contiene las entidades principales del dominio, como `Cliente`, `Pedido`, `Producto` y `Repartidor`.
- `reports`: genera los resúmenes de pedidos, reportes del día, inventario, directorio y resumen final.
- `services`: concentra las operaciones de registro y gestión del inventario.
- `utils`: contiene validaciones, lectura de datos, cálculos, formato de moneda y funciones de reflection.
- `Main.kt`: inicializa los datos y ejecuta el flujo principal de la aplicación.

## Requisitos

- JDK 8 o superior.
- Kotlin 1.8 o superior.
- IntelliJ IDEA con soporte para Kotlin, o el compilador de Kotlin configurado en el sistema.

El proyecto no utiliza una base de datos ni servicios externos. Los datos se cargan en memoria al iniciar la aplicación.

## Ejecución con IntelliJ IDEA

1. Abrir la carpeta `TareaPizzeria` en IntelliJ IDEA.
2. Verificar que el SDK del proyecto sea un JDK compatible.
3. Abrir `src/Main.kt`.
4. Ejecutar la función `main`.
5. Seguir las instrucciones mostradas en la consola.

## Ejecución desde la consola

Desde la carpeta raíz del proyecto, compilar los archivos Kotlin:

```bash
kotlinc src/*.kt src/model/*.kt src/reports/*.kt src/services/*.kt src/utils/*.kt -include-runtime -d TareaPizzeria.jar
```

Luego ejecutar la aplicación:

```bash
java -jar TareaPizzeria.jar
```

En Windows PowerShell, si el comodín `*.kt` no se expande correctamente para el compilador, se pueden indicar los archivos mediante la configuración de ejecución de IntelliJ.

## Flujo general de uso

1. La aplicación carga el menú, el inventario, los repartidores y algunos pedidos iniciales.
2. Solicita los datos del cliente.
3. Permite agregar productos al pedido.
4. Verifica el inventario disponible.
5. Calcula descuentos, impuestos y total.
6. Descuenta los ingredientes utilizados.
7. Asigna un repartidor cuando corresponde.
8. Muestra el resumen del pedido confirmado.
9. Pregunta si se desea registrar otro pedido.
10. Al finalizar, muestra los reportes y demostraciones de la tarea.

## Conceptos de Kotlin utilizados

El proyecto incorpora varios conceptos del lenguaje:

- Clases abiertas y herencia.
- Clases de datos.
- Enumeraciones.
- Interfaces y anotaciones personalizadas.
- Sobrecarga de operadores:
  - `Pedido + Pedido`.
  - Desestructuración mediante `componentN`.
- Sobrescritura de `equals`, `hashCode` y `toString`.
- Colecciones mutables e inmutables.
- `when`, rangos y operadores de pertenencia.
- Manejo de excepciones con `try-catch`.
- Funciones de extensión y funciones de orden superior.
- Reflection mediante `kotlin.reflect`.

## Notas

- Los datos son de demostración y se reinician cada vez que se ejecuta el programa.
- Los pedidos se almacenan únicamente en memoria.
- Las pausas de la interfaz utilizan la función `pausa` definida en `utils/Entrada.kt`.
- El resumen de pedidos combinados puede mostrar un identificador compuesto, por ejemplo `1001+1004`, sin modificar el identificador numérico original de cada pedido.

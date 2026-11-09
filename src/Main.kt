import java.time.LocalDate
import model.*
import model.Pedido.EstadoPedido
import model.Producto.Categoria
import reports.generarResumenPedido
import reports.imprimirReporteDelDia
import reports.mostrarDirectorio
import reports.mostrarInventario
import reports.mostrarResumenFinal
import services.calcularIngredientesDelPedido
import services.mostrarPasosReabastecimiento
import services.prepararLote
import services.reabastecerInteractivo
import services.registrarClienteSinDuplicar
import services.registrarProductoSinDuplicar
import services.verificarDisponibilidad
import utils.*

fun main() {
    // ---------- Datos iniciales ----------

    // val porque la referencia al Map nunca cambia pero su contenido si
    val inventario: MutableMap<String, Int> = mutableMapOf(
        "Masa" to 12,
        "Salsa de tomate" to 12,
        "Queso mozzarella" to 20,
        "Pepperoni" to 8,
        "Jamón" to 6,
        "Piña" to 4,
        "Champiñones" to 5,
        "Aceitunas" to 5,
        "Coca-Cola" to 10,
        "Agua mineral" to 10
    )

    val masa = Ingrediente("Masa", 1)
    val salsa = Ingrediente("Salsa de tomate", 1)

    // El menú se arma con services.registrarProductoSinDuplicar para no repetir productos
    val menu: MutableList<Producto> = mutableListOf()
    registrarProductoSinDuplicar(
        menu,
        Pizza("Pepperoni", 8990.0, listOf(masa, salsa, Ingrediente("Queso mozzarella", 2), Ingrediente("Pepperoni", 2)))
    )
    registrarProductoSinDuplicar(
        menu,
        Pizza(
            "Hawaiana",
            9490.0,
            listOf(masa, salsa, Ingrediente("Queso mozzarella", 2), Ingrediente("Jamón", 1), Ingrediente("Piña", 1))
        )
    )
    registrarProductoSinDuplicar(
        menu,
        Pizza(
            "Vegetariana",
            8490.0,
            listOf(
                masa,
                salsa,
                Ingrediente("Queso mozzarella", 1),
                Ingrediente("Champiñones", 1),
                Ingrediente("Aceitunas", 1)
            )
        )
    )
    registrarProductoSinDuplicar(menu, Bebida("Coca-Cola", 1800.0, listOf(Ingrediente("Coca-Cola", 1))))
    registrarProductoSinDuplicar(menu, Bebida("Agua mineral", 1200.0, listOf(Ingrediente("Agua mineral", 1))))
    registrarProductoSinDuplicar(menu, Extra("Queso extra", 1000.0, listOf(Ingrediente("Queso mozzarella", 1))))
    registrarProductoSinDuplicar(menu, Extra("Pepperoni extra", 1500.0, listOf(Ingrediente("Pepperoni", 1))))

    val repartidores: List<Repartidor> = listOf(
        Repartidor("Juan Pérez", "+56 (911) 111-1111", "juan.perez@pizzeria.cl", 2.5),
        Repartidor("María González", "+56 (922) 222-2222", "maria.gonzalez@pizzeria.cl", 1.2),
        Repartidor("Pedro Soto", "+56 (933) 333-3333", "pedro.soto@pizzeria.cl", 4.0)
    )

    // Lista de clientes registrados (sin duplicados)
    val clientes: MutableList<Cliente> = mutableListOf()

    // Arreglo con los pedidos del día,  var porque al agregar un pedido se crea un arreglo nuevo.
    var pedidosDelDia: Array<Pedido> = crearPedidosIniciales(menu, repartidores, clientes)
    var siguienteIdPedido = 1005   // var porque aumenta con cada pedido

    // ---------- Programa ----------
    println("========== PIZZERÍA ==========")
    pausa(1000)

    do {
        val pedido = registrarPedido(siguienteIdPedido, menu, inventario, repartidores, clientes)

        // Si el pedido fue rechazado (por falta de stock) no se agrega
        if (pedido != null) {
            pedidosDelDia += pedido
            siguienteIdPedido++

            pausa(1000)
            println("\n" + generarResumenPedido(pedido))
            notificarPedido(pedido)
        }
    } while (leerSiNo("\n¿Desea registrar otro pedido?"))

    // ---------- Cierre del día ----------
    imprimirReporteDelDia(pedidosDelDia)
    mostrarInventario(inventario)

    // Cliente y Repartidor son Usuario: se pueden mezclar en una misma lista
    val usuarios: List<Usuario> = clientes + repartidores
    mostrarDirectorio(usuarios)

    // Resumen final con todo lo de la Semana 3
    mostrarResumenFinal(pedidosDelDia, menu, clientes)
}

// Pedidos que ya estaban registrados antes de abrir el programa (datos de ejemplo)
fun crearPedidosIniciales(
    menu: List<Producto>,
    repartidores: List<Repartidor>,
    clientes: MutableList<Cliente>
): Array<Pedido> {
    val pepperoni = menu[0]
    val hawaiana = menu[1]
    val vegetariana = menu[2]
    val cocaCola = menu[3]
    val agua = menu[4]

    // Camila hace dos pedidos: como es el mismo cliente (mismo nombre y correo) se registra una sola vez
    val camila = registrarClienteSinDuplicar(
        clientes, Cliente("Camila Rojas", "+56 9 1234 5678", "camila.rojas@correo.cl", Cliente.DIRECCION_RETIRO)
    )

    val pedido1 = Pedido(1001, camila, listOf(pepperoni, pepperoni), fecha = LocalDate.of(2026, 11, 3))
    pedido1.cambiarEstado(EstadoPedido.LISTO)

    val diego = registrarClienteSinDuplicar(
        clientes, Cliente("Diego Fuentes", "+56 (923) 456-7890", "diego.fuentes@correo.cl", "Av. Los Leones 123")
    )
    val pedido2 = Pedido(1002, diego, listOf(hawaiana, vegetariana, cocaCola), "Sin cebolla", LocalDate.of(2026, 11, 4))
    repartidores.find { it.nombre == "Pedro Soto" }?.entregarPedido(pedido2)

    val valentina = registrarClienteSinDuplicar(
        clientes, Cliente("Valentina Soto", "+56 (934) 567-8901", "valentina.soto@correo.cl", Cliente.DIRECCION_RETIRO)
    )
    // Este pedido es de octubre: queda FUERA del periodo de promoción
    val pedido3 = Pedido(1003, valentina, listOf(vegetariana), fecha = LocalDate.of(2026, 10, 20))

    val pedido4 = Pedido(1004, camila, listOf(hawaiana, vegetariana, agua), fecha = LocalDate.of(2026, 11, 5))
    pedido4.cambiarEstado(EstadoPedido.LISTO)

    return arrayOf(pedido1, pedido2, pedido3, pedido4)
}

// Registra un pedido completo. Retorna null si no se pudo confirmar.
fun registrarPedido(
    id: Int,
    menu: List<Producto>,
    inventario: MutableMap<String, Int>,
    repartidores: List<Repartidor>,
    clientes: MutableList<Cliente>
): Pedido? {
    val cliente = registrarCliente(clientes)
    val notas = leerNotas()
    val productos = seleccionarProductos(menu)

    val pizzasPedidas = productos.count { it.categoria == Categoria.PIZZA }
    if (pizzasPedidas > PIZZAS_PARA_DESCUENTO) {
        println("\n¡Su pedido supera las $PIZZAS_PARA_DESCUENTO pizzas! Se aplicará un ${(TASA_DESCUENTO * 100).toInt()}% de descuento.")
    }

    // Verificación de stock ANTES de aceptar el pedido
    val necesarios = calcularIngredientesDelPedido(productos)
    if (!confirmarStock(inventario, necesarios)) {
        return null
    }

    val pedido = Pedido(id, cliente, productos, notas)

    // Rango de fechas: si la fecha del pedido está dentro del periodo, se aplica la promoción
    if (pedido.fecha in PERIODO_PROMOCION) {
        println("\n¡Hay promoción! Se aplicará un ${(TASA_PROMOCION * 100).toInt()}% de descuento a las pizzas y bebidas.")
    }

    // try-catch: errores de costo (IllegalArgumentException) o de inventario (IllegalStateException)
    try {
        pedido.calcularTotal()   // valida que el costo del pedido sea correcto
        pedido.productosAgrupados().forEach { (producto, cantidad) ->
            prepararLote(inventario, producto.ingredientes, cantidad)   // descuenta del inventario
        }
    } catch (e: IllegalArgumentException) {
        println("\nError en el pedido: ${e.message}")
        return null
    } catch (e: IllegalStateException) {
        println("\nError de inventario: ${e.message}")
        return null
    }

    println("\nHay ingredientes suficientes. Pedido N°$id confirmado.")
    asignarEntrega(pedido, repartidores)
    return pedido
}

fun registrarCliente(clientes: MutableList<Cliente>): Cliente {
    println("\n--- Datos del cliente ---")
    val nombre = leerNombre()

    // Para simplificar: si la dirección queda vacía se toma como retiro en el local
    print("Ingrese la dirección de entrega (Enter para retiro en local): ")
    val direccion = leerLinea().ifEmpty { Cliente.DIRECCION_RETIRO }

    val telefono = leerTelefono()
    val correo = leerCorreo()

    // Si ya existe un cliente igual (mismo nombre y correo) no se duplica
    val cliente = registrarClienteSinDuplicar(clientes, Cliente(nombre, telefono, correo, direccion))
    println("\nCliente registrado:")
    println(cliente.descripcion())
    return cliente
}

// Notas opcionales: si el cliente no escribe nada, retorna null
fun leerNotas(): String? {
    print("Notas del pedido (opcional, Enter para omitir): ")
    return leerLinea().takeIf { it.isNotEmpty() }
}

fun seleccionarProductos(menu: List<Producto>): List<Producto> {
    println("\n--- Menú ---")
    menu.forEachIndexed { indice, producto -> println("${indice + 1}. ${producto.detallarProducto()}") }

    val productos = mutableListOf<Producto>()
    do {
        val opcion = leerEnteroEnRango("\nSeleccione un producto (1-${menu.size}): ", 1..menu.size)
        val elegido = menu[opcion - 1]

        // La cantidad debe estar dentro del rango permitido (1 a 100)
        val cantidad = leerEnteroEnRango("¿Cuántas unidades de ${elegido.nombre} desea?: ", RANGO_CANTIDAD)

        repeat(cantidad) { productos.add(elegido) }
        println("Agregado: $cantidad x ${elegido.nombre}")
    } while (leerSiNo("¿Desea agregar otro producto al pedido?"))

    return productos
}

// Retorna true si el pedido tiene stock suficiente (permitiendo services.reabastecer si falta)
fun confirmarStock(inventario: MutableMap<String, Int>, necesarios: Map<String, Int>): Boolean {
    println("\nVerificando ingredientes en el inventario...")
    var faltantes = verificarDisponibilidad(inventario, necesarios)
    if (faltantes.isEmpty()) return true

    mostrarPasosReabastecimiento(faltantes)

    if (leerSiNo("\n¿Desea registrar ahora el reabastecimiento?")) {
        reabastecerInteractivo(inventario, faltantes)
        println("\nVolviendo a verificar los ingredientes...")
        faltantes = verificarDisponibilidad(inventario, necesarios)
    }

    if (faltantes.isNotEmpty()) {
        println("\nEl pedido no puede ser completado por falta de ingredientes. Pedido rechazado.")
        return false
    }
    return true
}

// Retiro en local -> Listo. Despacho -> se busca el repartidor disponible más cercano con find.
fun asignarEntrega(pedido: Pedido, repartidores: List<Repartidor>) {
    if (pedido.cliente.esRetiroEnLocal) {
        pedido.cambiarEstado(EstadoPedido.LISTO)
        println("El cliente retira en el local. Pedido listo.")
        return
    }

    val masCercano = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
    if (masCercano != null) {
        println(masCercano.entregarPedido(pedido))
    } else {
        println("No hay repartidores disponibles. El pedido queda en preparación hasta que se libere uno.")
    }
}

// let ejecuta el bloque solo si el valor NO es null
fun notificarPedido(pedido: Pedido) {
    println("\n--- Notificaciones ---")
    println("Correo enviado a ${pedido.cliente.correo}: su pedido N°${pedido.id} está '${pedido.estado.descripcion}'.")
    pedido.repartidor?.let { println("Aviso enviado a ${it.nombre}: debe entregar el pedido N°${pedido.id}.") }
    pedido.notas?.let { println("Notas enviadas a cocina: \"$it\"") }
}
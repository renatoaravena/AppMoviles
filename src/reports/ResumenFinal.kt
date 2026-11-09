package reports

import java.time.LocalDate
import model.*
import services.registrarClienteSinDuplicar
import services.registrarProductoSinDuplicar
import utils.*


fun mostrarResumenFinal(pedidosDelDia: Array<Pedido>, menu: MutableList<Producto>, clientes: MutableList<Cliente>) {
    println("\n==================== RESUMEN FINAL SUMATIVA ====================")
    pausa(1000)
    mostrarPedidosCombinados(pedidosDelDia)
    pausa(1000)
    mostrarClienteDesestructurado(pedidosDelDia)
    pausa(1000)
    mostrarPromocionables(menu)
    pausa(1000)
    mostrarIgualdad(menu, clientes)
    pausa(1000)
    mostrarReflection()
}

// Operador + para combinar pedidos del mismo cliente y desestructuración de objetos
fun mostrarPedidosCombinados(pedidosDelDia: Array<Pedido>) {
    println("\n--- 1. Pedidos combinados con el operador + ---")

    // groupBy usa equals() y hashCode() de Cliente: agrupa los pedidos de un mismo cliente
    val pedidosPorCliente = pedidosDelDia.groupBy { it.cliente }
    val delMismoCliente = pedidosPorCliente.values.firstOrNull { it.size >= 2 }

    if (delMismoCliente == null) {
        println("No hay dos pedidos de un mismo cliente para combinar.")
        return
    }

    // Desestructuración de una lista: primero y segundo elemento
    val (primero, segundo) = delMismoCliente
    val combinado = primero + segundo   // usa el operador + que definimos en Pedido

    println("Pedido N°${primero.id}: ${primero.productos.size} productos, total ${formatearPesos(primero.calcularTotal().total)}")
    println("Pedido N°${segundo.id}: ${segundo.productos.size} productos, total ${formatearPesos(segundo.calcularTotal().total)}")
    println("Pedido combinado (N°${primero.id} + N°${segundo.id}):")
    pausa(1000)
    println(generarResumenPedido(primero))
    pausa(1000)
    println(generarResumenPedido(segundo))
    pausa(1000)
    println(generarResumenPedido(combinado, "${primero.id}+${segundo.id}"))
    pausa(1000)
}



// Desestructuracion de objetos: Cliente y Pedido
fun mostrarClienteDesestructurado(pedidosDelDia: Array<Pedido>) {
    println("\n--- 2. Desestructuración ---")
    pausa(1000)

    // Cliente: se separa en nombre, correo y teléfono
    val (nombre, correo, telefono) = pedidosDelDia.last().cliente
    println("Cliente desestructurado -> nombre: $nombre | correo: $correo | teléfono: $telefono")

    // Pedido: se separa en cliente, productos y total
    val (cliente, productos, total) = pedidosDelDia.last()
    println("Pedido desestructurado  -> cliente: ${cliente.nombre} | productos: ${productos.size} | total: ${formatearPesos(total)}")
    pausa(1000)
}

// Anotacion @Promocionable
fun mostrarPromocionables(menu: List<Producto>) {
    println("\n--- 3. Productos promocionables (anotación @Promocionable) ---")
    pausa(1000)
    println("Periodo de promoción: $PERIODO_PROMOCION | ¿hoy está en el periodo? ${LocalDate.now() in PERIODO_PROMOCION}")

    for (producto in menu) {
        val texto = if (esPromocionable(producto)) "SÍ  (${descripcionPromocion(producto)})" else "no"
        println("  ${producto.nombre.padEnd(18)} ${producto.categoria.descripcion.padEnd(12)} $texto")
    }

    val promocionables = menu.filter { esPromocionable(it) }.map { it.nombre }
    println("Promocionables: ${promocionables.joinToString(", ")}")
    pausa(1000)
}

// Igualdad de objetos
fun mostrarIgualdad(menu: MutableList<Producto>, clientes: MutableList<Cliente>) {
    println("\n--- 4. Igualdad de objetos (equals y hashCode) ---")
    pausa(1000)

    // Clientes: mismo nombre y correo -> iguales, aunque sean objetos distintos en memoria
    val clienteA = Cliente("Camila Rojas", "+56 9 1234 5678", "camila.rojas@correo.cl", "Av. Central 100")
    val clienteB = Cliente("Camila Rojas", "+56 9 9999 8888", "camila.rojas@correo.cl", "Otra dirección 200")
    val clienteC = Cliente("Camila Rojas", "+56 9 1234 5678", "otro.correo@correo.cl", "Av. Central 100")
    println("Clientes:")
    println("  A == B  (mismo nombre y correo)  -> ${clienteA == clienteB}")
    println("  A === B (misma instancia)        -> ${clienteA === clienteB}")
    println("  A == C  (distinto correo)        -> ${clienteA == clienteC}")
    println("  hashCode de A y B iguales        -> ${clienteA.hashCode() == clienteB.hashCode()}")

    // Productos: mismo nombre y categoría -> iguales (aunque cambie el precio)
    val ingredientesDePrueba = listOf(Ingrediente("Masa", 1))
    val pizzaA = Pizza("Pepperoni", 8990.0, ingredientesDePrueba)
    val pizzaB = Pizza("Pepperoni", 9990.0, ingredientesDePrueba)
    val extraC = Extra("Pepperoni", 1500.0, listOf(Ingrediente("Pepperoni", 1)))
    println("Productos:")
    println("  Pizza Pepperoni == Pizza Pepperoni (otro precio) -> ${pizzaA == pizzaB}")
    println("  Pizza Pepperoni == Extra Pepperoni (otra categoría) -> ${pizzaA == extraC}")
    println("  hashCode de las dos pizzas iguales -> ${pizzaA.hashCode() == pizzaB.hashCode()}")

    // Un Set no guarda elementos repetidos: usa equals() y hashCode()
    val sinRepetidos = setOf(pizzaA, pizzaB, extraC)
    println("  Set con 3 productos (2 son iguales) -> queda con ${sinRepetidos.size}")

    // Evitar duplicados al registrar
    println("Registro sin duplicados:")
    val productosAntes = menu.size
    registrarProductoSinDuplicar(menu, pizzaB)
    println("  Productos en el menú: antes $productosAntes, después ${menu.size}")

    val clientesAntes = clientes.size
    registrarClienteSinDuplicar(clientes, clienteA)
    registrarClienteSinDuplicar(clientes, clienteB)   // es igual a A: no se agrega otra vez
    println("  Clientes registrados: antes $clientesAntes, después ${clientes.size}")
    pausa(1000)
}

// Reflection
fun mostrarReflection() {
    println("\n--- 5. Reflection: propiedades y métodos de las clases ---")
    pausa(1000)
    listarMiembros(Pedido::class)
    pausa(1000)
    listarMiembros(Cliente::class)
    pausa(1000)
    listarMiembros(Producto::class)
    pausa(1000)
}
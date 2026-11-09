import model.*
import model.Pedido.EstadoPedido
import utils.*

//Resumen de un pedido
fun generarResumenPedido(pedido: Pedido): String {
    val totales = pedido.calcularTotal()
    val cliente = pedido.cliente
    val porcentajeDescuento = (TASA_DESCUENTO * 100).toInt()
    val porcentajeIva = (TASA_IVA * 100).toInt()

    // Una línea de totales con la etiqueta a la izquierda y el valor alineado a la derecha
    fun lineaTotal(etiqueta: String, valor: String) = "${etiqueta.padEnd(24)}${valor.padStart(14)}"

    return buildString {
        appendLine("========================================")
        appendLine("       RESUMEN DEL PEDIDO N° ${pedido.id}")
        appendLine("========================================")

        appendLine("--- Cliente ---")
        appendLine("Nombre:     ${cliente.nombre}")
        appendLine("Dirección:  ${cliente.direccion}")
        appendLine("Teléfono:   ${cliente.telefono}")
        appendLine("Correo:     ${cliente.correo}")

        appendLine("--- Productos ---")
        appendLine("${"Producto".padEnd(14)}${"Precio".padStart(10)}${"Cant.".padStart(7)}${"Subtotal".padStart(14)}")
        for ((pizza, cantidad) in pedido.productosAgrupados()) {
            val subtotalLinea = pizza.precio * cantidad
            appendLine(
                "${pizza.nombre.padEnd(14)}${formatearPesos(pizza.precio).padStart(10)}" +
                        "${cantidad.toString().padStart(7)}${formatearPesos(subtotalLinea).padStart(14)}"
            )
        }

        appendLine("--- Totales ---")
        appendLine(lineaTotal("Subtotal:", formatearPesos(totales.subtotal)))
        if (totales.descuento > 0) {
            appendLine(lineaTotal("Descuento ($porcentajeDescuento%):", "-" + formatearPesos(totales.descuento)))
        } else {
            appendLine(lineaTotal("Descuento:", formatearPesos(0.0)))
        }
        appendLine(lineaTotal("IVA ($porcentajeIva%):", formatearPesos(totales.impuestos)))
        appendLine(lineaTotal("TOTAL:", formatearPesos(totales.total)))

        appendLine("--- Estado ---")
        appendLine("Estado:     ${pedido.estado.descripcion}")
        // ?. y ?: -> si no hay repartidor asignado se muestra un texto por defecto
        val textoSinRepartidor = if (cliente.esRetiroEnLocal) "No aplica (retiro en local)" else "Sin asignar"
        appendLine("Repartidor: ${pedido.repartidor?.nombre ?: textoSinRepartidor}")
        appendLine("Notas:      ${pedido.notas ?: "Sin notas"}")
        append("========================================")
    }
}

//Reporte de todos los pedidos
fun imprimirReporteDelDia(pedidos: Array<Pedido>) {
    println("\n=================== REPORTE DEL DÍA ===================")
    println("${"N°".padEnd(6)}${"Cliente".padEnd(18)}${"Pizzas".padEnd(8)}${"Total".padEnd(12)}Estado")
    println("-".repeat(55))

    for (pedido in pedidos) {
        val total = formatearPesos(pedido.calcularTotal().total)
        println(
            "${pedido.id.toString().padEnd(6)}${pedido.cliente.nombre.padEnd(18)}" +
                    "${pedido.cantidadPizzas.toString().padEnd(8)}${total.padEnd(12)}${pedido.estado.descripcion}"
        )
    }

    // Agrupación con filter: completados (ya salieron de cocina) y pendientes (aún en preparación)
    val completados = pedidos.filter { it.estado == EstadoPedido.LISTO || it.estado == EstadoPedido.EN_CAMINO }
    val pendientes = pedidos.filter { it.estado == EstadoPedido.EN_PREPARACION }

    println("-".repeat(55))
    println("Pedidos completados (Listo / En camino): ${completados.size}")
    completados.forEach { println("   - N°${it.id} ${it.cliente.nombre}: ${it.estado.descripcion}") }
    println("Pedidos pendientes (En preparación): ${pendientes.size}")
    pendientes.forEach { println("   - N°${it.id} ${it.cliente.nombre}") }

    val totalVendido = pedidos.sumOf { it.calcularTotal().total }
    println("Total vendido en el día: ${formatearPesos(totalVendido)}")
}

fun mostrarInventario(inventario: Map<String, Int>) {
    println("\n------------- INVENTARIO ACTUAL -------------")
    inventario.forEach { (ingrediente, cantidad) -> println("  ${ingrediente.padEnd(18)} $cantidad") }
}

// Polimorfismo, la lista es de Usuario, pero cada elemento ejecuta SU versión de descripcion()
fun mostrarDirectorio(usuarios: List<Usuario>) {
    println("\n------------- DIRECTORIO DEL DÍA -------------")
    usuarios.forEach { println("  ${it.descripcion()}") }
}
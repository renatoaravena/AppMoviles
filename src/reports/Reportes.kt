package reports

import model.*
import model.Pedido.EstadoPedido
import utils.*
import kotlin.collections.iterator


// Un descuento se muestra con signo menos (o $0 si no hubo descuento)
private fun textoDescuento(monto: Double): String =
    if (monto > 0) "-" + formatearPesos(monto) else formatearPesos(0.0)

// Resumen de un pedido. La etiqueta permite mostrar identificadores compuestos
// cuando se combinan pedidos, sin cambiar el id numérico del objeto.
fun generarResumenPedido(pedido: Pedido, identificador: String = pedido.id.toString()): String {
    val totales = pedido.calcularTotal()
    val cliente = pedido.cliente
    val porcentajeDescuento = (TASA_DESCUENTO * 100).toInt()
    val porcentajePromocion = (TASA_PROMOCION * 100).toInt()
    val porcentajeIva = (TASA_IVA * 100).toInt()

    // Una línea de totales con la etiqueta a la izquierda y el valor alineado a la derecha
    fun lineaTotal(etiqueta: String, valor: String) = "${etiqueta.padEnd(30)}${valor.padStart(18)}"

    // El rango de fechas se usa con "in": true si la fecha del pedido está dentro del periodo
    val textoPeriodo = if (pedido.fecha in PERIODO_PROMOCION) "en periodo de promoción" else "fuera de promoción"

    return buildString {
        appendLine("\n================================================")
        appendLine("          RESUMEN DEL PEDIDO N° $identificador")
        appendLine("================================================")

        appendLine("--- Cliente ---")
        appendLine("Nombre:     ${cliente.nombre}")
        appendLine("Dirección:  ${cliente.direccion}")
        appendLine("Teléfono:   ${cliente.telefono}")
        appendLine("Correo:     ${cliente.correo}")

        appendLine("--- Productos ---")
        appendLine("${"Producto".padEnd(18)}${"Precio".padStart(10)}${"Cant.".padStart(7)}${"Subtotal".padStart(13)}")
        for ((producto, cantidad) in pedido.productosAgrupados()) {
            val subtotalLinea = producto.precio * cantidad
            appendLine(
                "${producto.nombre.padEnd(18)}${formatearPesos(producto.precio).padStart(10)}" +
                        "${cantidad.toString().padStart(7)}${formatearPesos(subtotalLinea).padStart(13)}"
            )
        }

        appendLine("--- Totales ---")
        appendLine(lineaTotal("Subtotal:", formatearPesos(totales.subtotal)))
        appendLine(lineaTotal("Desc. por cantidad ($porcentajeDescuento%):", textoDescuento(totales.descuentoCantidad)))
        appendLine(lineaTotal("Desc. promoción ($porcentajePromocion%):", textoDescuento(totales.descuentoPromocion)))
        appendLine(lineaTotal("Descuento total:", textoDescuento(totales.descuento)))
        appendLine(lineaTotal("IVA ($porcentajeIva%):", formatearPesos(totales.impuestos)))
        appendLine(lineaTotal("TOTAL:", formatearPesos(totales.total)))

        appendLine("--- Estado ---")
        appendLine("Fecha:      ${pedido.fecha} ($textoPeriodo)")
        appendLine("Estado:     ${pedido.estado.descripcion}")
        // ?. y ?: -> si no hay repartidor asignado se muestra un texto por defecto
        val textoSinRepartidor = if (cliente.esRetiroEnLocal) "No aplica (retiro en local)" else "Sin asignar"
        appendLine("Repartidor: ${pedido.repartidor?.nombre ?: textoSinRepartidor}")
        appendLine("Notas:      ${pedido.notas ?: "Sin notas"}")
        append("================================================\n")
    }
}

//Reporte de todos los pedidos del dia
fun imprimirReporteDelDia(pedidos: Array<Pedido>) {
    println("\n=========================== REPORTE DEL DÍA ===========================")
    println("${"N°".padEnd(6)}${"Cliente".padEnd(18)}${"Pizzas".padEnd(8)}${"Fecha".padEnd(12)}${"Total".padEnd(12)}Estado")
    println("-".repeat(68))

    for (pedido in pedidos) {
        val total = formatearPesos(pedido.calcularTotal().total)
        println(
            "${pedido.id.toString().padEnd(6)}${pedido.cliente.nombre.padEnd(18)}" +
                    "${pedido.cantidadPizzas.toString().padEnd(8)}${pedido.fecha.toString().padEnd(12)}" +
                    "${total.padEnd(12)}${pedido.estado.descripcion}"
        )
    }

    // Agrupación con filter, completados (ya salieron de cocina) y pendientes (aún en preparación)
    val completados = pedidos.filter { it.estado == EstadoPedido.LISTO || it.estado == EstadoPedido.EN_CAMINO }
    val pendientes = pedidos.filter { it.estado == EstadoPedido.EN_PREPARACION }

    println("-".repeat(68))
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
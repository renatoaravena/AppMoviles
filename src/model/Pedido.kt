package model

import java.time.LocalDate
import model.Producto.Categoria
import utils.TotalesPedido
import utils.calcularTotalPedido

class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,             // opcional, puede ser null
    val fecha: LocalDate = LocalDate.now() // fecha del pedido (por defecto, hoy)
) {
    // Los tres estados posibles del pedido
    enum class EstadoPedido(val descripcion: String) {
        EN_PREPARACION("En preparación"),
        LISTO("Listo"),
        EN_CAMINO("En camino")
    }

    // Solo se puede cambiar a través de los métodos de la clase, desde fuera solo se puede leer
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    var repartidor: Repartidor? = null
        private set

    // Solo cuenta las pizzas, los pedidos igual pueden tener extras, bebidas, etc
    val cantidadPizzas: Int
        get() = productos.count { it.categoria == Categoria.PIZZA }

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor) {
        repartidor = nuevoRepartidor
    }

    // Usa la función reutilizable de utils/Calculos.kt
    fun calcularTotal(): TotalesPedido = calcularTotalPedido(productos, fecha)

    // Agrupa los productos repetidos: Producto -> cantidad pedida
    fun productosAgrupados(): Map<Producto, Int> = productos.groupingBy { it }.eachCount()

    // Sobrecarga de operadores para poder combinar pedidos del mismo cliente y desestructurarlos
    // Permite escribir: val combinado = pedido1 + pedido2
    // Se juntan los productos y el total se recalcula (con sus descuentos) al crear el nuevo pedido
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) { "Solo se pueden combinar pedidos del mismo cliente." }
        val notasJuntas = listOfNotNull(notas, otro.notas).joinToString(" | ").ifEmpty { null }
        return Pedido(id, cliente, productos + otro.productos, notasJuntas, fecha)
    }

    // Desestructuración
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Double = calcularTotal().total
}

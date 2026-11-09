package model

import utils.TotalesPedido
import utils.calcularTotalPedido

class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Pizza>,
    val notas: String? = null          // opcional: puede ser null (String?)
) {
    // Los tres estados posibles de un pedido
    enum class EstadoPedido(val descripcion: String) {
        EN_PREPARACION("En preparación"),
        LISTO("Listo"),
        EN_CAMINO("En camino")
    }

    // Solo se puede cambiar a través de los métodos de la clase, con private set es solo de lectura desde afuera
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    var repartidor: Repartidor? = null
        private set

    val cantidadPizzas: Int
        get() = productos.size

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor) {
        repartidor = nuevoRepartidor
    }

    // Usa la función reutilizable de utils/Calculos.kt
    fun calcularTotal(): TotalesPedido = calcularTotalPedido(productos)

    // Agrupa las pizzas repetidas: Pizza -> cantidad pedida
    fun productosAgrupados(): Map<Pizza, Int> = productos.groupingBy { it }.eachCount()
}
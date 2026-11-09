package model

import model.Pedido.EstadoPedido

class Repartidor(
    nombre: String,
    telefono: String,
    correo: String,
    val distanciaKm: Double,          // distancia al local, sirve para elegir al más cercano
    var disponible: Boolean = true    // var porque cambia cuando sale a repartir
) : Usuario(nombre, telefono, correo) {

    // Metodo propio del repartidor: toma el pedido y sale a entregarlo, cambia el esto del pedido se agrega mensaje
    fun entregarPedido(pedido: Pedido): String {
        disponible = false
        pedido.asignarRepartidor(this)
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        return "$nombre sale a entregar el pedido N°${pedido.id} a ${pedido.cliente.direccion}."
    }

    override fun descripcion(): String {
        val estadoActual = if (disponible) "Disponible" else "En reparto"
        return "Repartidor: ${super.descripcion()} | A $distanciaKm km del local | $estadoActual"
    }
}
package model

import utils.formatearPesos

// Un ingrediente y cuántas unidades necesita UNA pizza
data class Ingrediente(val nombre: String, val cantidadPorPizza: Int)


data class Pizza(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<Ingrediente>
) {
    // Se valida al crear la pizza: un precio <= 0 es un costo no válido
    init {
        require(precio > 0) { "El precio de '$nombre' debe ser mayor a 0." }
        require(ingredientes.isNotEmpty()) { "La pizza '$nombre' debe tener ingredientes." }
    }

    // Retorna una descripción completa de la pizza
    fun detallarProducto(): String {
        val detalleIngredientes = ingredientes.joinToString(", ") { "${it.nombre} (x${it.cantidadPorPizza})" }
        return "$nombre - ${formatearPesos(precio)} | Ingredientes: $detalleIngredientes"
    }
}
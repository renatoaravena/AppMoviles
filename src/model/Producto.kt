package model

import utils.formatearPesos

// open permite que Pizza, Bebida y Extra hereden de esta clase
open class Producto(
    val nombre: String,
    val precio: Double,
    val categoria: Categoria,
    val ingredientes: List<Ingrediente>
) {
    // Las tres categorías de producto que pide el enunciado
    enum class Categoria(val descripcion: String) {
        PIZZA("Pizza"),
        BEBIDA("Bebida"),
        INGREDIENTE("Ingrediente")
    }

    // Se valida al crear el producto
    init {
        require(precio > 0) { "El precio de '$nombre' debe ser mayor a 0." }
        require(ingredientes.isNotEmpty()) { "El producto '$nombre' debe usar al menos un ingrediente." }
    }

    // Retorna una descripción completa del producto
    fun detallarProducto(): String {
        val detalleIngredientes = ingredientes.joinToString(", ") { "${it.nombre} (x${it.cantidadPorUnidad})" }
        return "[${categoria.descripcion}] $nombre - ${formatearPesos(precio)} | Usa: $detalleIngredientes"
    }

    //Sobrecarga de operadores para poder comparar productos por nombre y categoría, sin importar el precio ni los ingredientes
    override fun equals(other: Any?): Boolean {
        if (this === other) return true        // es exactamente el mismo objeto
        if (other !is Producto) return false   // el otro no es un Producto
        return nombre == other.nombre && categoria == other.categoria
    }

    // Si dos objetos son iguales con equals(), deben tener el mismo hashCode()
    override fun hashCode(): Int = 31 * nombre.hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.descripcion})"
}
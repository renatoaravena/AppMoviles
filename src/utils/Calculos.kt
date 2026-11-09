package utils

import kotlin.math.round
import model.Pizza

//Paso 2 -- Calculos asi cumplimos con funciones reutilizables

const val TASA_IVA = 0.19            // impuesto: 19%
const val TASA_DESCUENTO = 0.10      // descuento: 10%
const val PIZZAS_PARA_DESCUENTO = 3  // el descuento aplica con MÁS de 3 pizzas

// Agrupa los valores del cálculo para poder retornarlos juntos cuando sea necesario
data class TotalesPedido(
    val subtotal: Double,
    val descuento: Double,
    val impuestos: Double,
    val total: Double
)

fun calcularTotalPedido(productos: List<Pizza>): TotalesPedido {
    // Manejo de errores

    require(true) { "La lista de productos no puede ser nula." }
    require(productos.isNotEmpty()) { "El pedido debe tener al menos una pizza." }

    val subtotal = productos.sumOf { it.precio }
    require(subtotal > 0) { "El costo del pedido no es válido: $subtotal" }

    // 10% de descuento solo si hay más de 3 pizzas
    val descuento = if (productos.size > PIZZAS_PARA_DESCUENTO) round(subtotal * TASA_DESCUENTO) else 0.0

    val baseImponible = subtotal - descuento
    val impuestos = round(baseImponible * TASA_IVA)

    //Retorna un objeto de tipo TotalesPedido con los valores listos
    return TotalesPedido(
        subtotal = subtotal,
        descuento = descuento,
        impuestos = impuestos,
        total = baseImponible + impuestos
    )
}
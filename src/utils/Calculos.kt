package utils

import java.time.LocalDate
import kotlin.math.round
import model.Producto
import model.Producto.Categoria


const val TASA_IVA = 0.19            // impuesto: 19%
const val TASA_DESCUENTO = 0.10      // descuento por cantidad: 10%
const val PIZZAS_PARA_DESCUENTO = 3  // el descuento aplica con MÁS de 3 pizzas (4 o más)
const val TASA_PROMOCION = 0.15      // descuento de promoción: 15%

// Rango de fecha para las promociones
// "fecha in PERIODO_PROMOCION" es true si la fecha está entre el 1 de noviembre y el 31 de diciembre de 2026
val PERIODO_PROMOCION: ClosedRange<LocalDate> = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 12, 31)

// Agrupa los valores del cálculo para poder retornarlos juntos
data class TotalesPedido(
    val subtotal: Double,
    val descuentoCantidad: Double,
    val descuentoPromocion: Double,
    val impuestos: Double,
    val total: Double
) {
    // Suma de los dos descuentos
    val descuento: Double
        get() = descuentoCantidad + descuentoPromocion
}

fun calcularTotalPedido(productos: List<Producto>, fecha: LocalDate): TotalesPedido {
    //Manejo de errores
    require(productos.isNotEmpty()) { "El pedido debe tener al menos un producto." }

    val subtotal = productos.sumOf { it.precio }
    require(subtotal > 0) { "El costo del pedido no es válido: $subtotal" }

    // Descuento 1: 10% solo si hay más de 3 pizzas
    val cantidadPizzas = productos.count { it.categoria == Categoria.PIZZA }
    val descuentoCantidad = if (cantidadPizzas > PIZZAS_PARA_DESCUENTO) round(subtotal * TASA_DESCUENTO) else 0.0

    // Descuento 2: 15% si la fecha del pedido está dentro del periodo de promoción.
    // Solo se descuenta sobre los productos marcados con @Promocionable (se revisa con reflection)
    val descuentoPromocion = if (fecha in PERIODO_PROMOCION) {
        val subtotalPromocionable = productos.filter { esPromocionable(it) }.sumOf { it.precio }
        round(subtotalPromocionable * TASA_PROMOCION)
    } else {
        0.0
    }

    val baseImponible = subtotal - descuentoCantidad - descuentoPromocion
    val impuestos = round(baseImponible * TASA_IVA)

    return TotalesPedido(
        subtotal = subtotal,
        descuentoCantidad = descuentoCantidad,
        descuentoPromocion = descuentoPromocion,
        impuestos = impuestos,
        total = baseImponible + impuestos
    )
}
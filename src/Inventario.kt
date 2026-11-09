import model.*
import utils.*

//Inventario iniciado con 10 unidades de cada ingrediente

// Cantidad de cada ingrediente necesaria para un pedido de pizzas
fun calcularIngredientesNecesarios(ingredientes: List<Ingrediente>, cantidadPizzas: Int): Map<String, Int> =
    ingredientes.associate { it.nombre to it.cantidadPorPizza * cantidadPizzas }

// Calcula lo necesario para un pedido y actualiza automáticamente el inventario
// check() lanza IllegalStateException si el inventario quedaría negativo
fun prepararLote(
    inventario: MutableMap<String, Int>,
    ingredientes: List<Ingrediente>,
    cantidadPizzas: Int
): Map<String, Int> {
    val necesarios = calcularIngredientesNecesarios(ingredientes, cantidadPizzas)

    for ((nombre, cantidad) in necesarios) {
        val disponible = inventario[nombre] ?: 0
        check(disponible >= cantidad) { "Inventario insuficiente de $nombre: se necesitan $cantidad y hay $disponible." }
    }
    for ((nombre, cantidad) in necesarios) {
        inventario[nombre] = (inventario[nombre] ?: 0) - cantidad
    }
    return necesarios
}

// Suma los ingredientes de todo el pedido, varias pizzas pueden compartir ingredientes
fun calcularIngredientesDelPedido(productos: List<Pizza>): Map<String, Int> {
    val total = mutableMapOf<String, Int>()
    for ((pizza, cantidad) in productos.groupingBy { it }.eachCount()) {
        for ((ingrediente, necesario) in calcularIngredientesNecesarios(pizza.ingredientes, cantidad)) {
            total[ingrediente] = (total[ingrediente] ?: 0) + necesario
        }
    }
    return total
}

// Verifica con un when si alcanza cada ingrediente.
// Retorna un Map con lo que falta, vacío si todo alcanza
fun verificarDisponibilidad(inventario: Map<String, Int>, necesarios: Map<String, Int>): Map<String, Int> {
    val faltantes = mutableMapOf<String, Int>()

    for ((ingrediente, necesario) in necesarios) {
        val disponible = inventario[ingrediente] ?: 0
        when {
            disponible >= necesario ->
                println("  [OK]           $ingrediente: se necesitan $necesario, hay $disponible")
            disponible == 0 -> {
                println("  [AGOTADO]      $ingrediente: se necesitan $necesario, no hay stock")
                faltantes[ingrediente] = necesario
            }
            else -> {
                println("  [INSUFICIENTE] $ingrediente: se necesitan $necesario, solo hay $disponible")
                faltantes[ingrediente] = necesario - disponible
            }
        }
    }
    return faltantes
}

// Mensaje con los ingredientes insuficientes y los pasos para reabastecerlos
fun mostrarPasosReabastecimiento(faltantes: Map<String, Int>) {
    println("\nNo hay ingredientes suficientes para este pedido. Faltan:")
    faltantes.forEach { (ingrediente, falta) -> println("  - $ingrediente: faltan $falta unidades") }

    println("\nPasos para reabastecer:")
    println("  1. Contactar al proveedor y solicitar las unidades que faltan.")
    println("  2. Recibir el pedido del proveedor y revisar que venga completo.")
    println("  3. Registrar el ingreso en el inventario del sistema.")
    println("  4. Volver a confirmar el pedido del cliente.")
}

// Suma unidades al inventario. Una cantidad negativa es un error.
fun reabastecer(inventario: MutableMap<String, Int>, ingrediente: String, cantidad: Int) {
    require(cantidad >= 0) { "No se puede agregar una cantidad negativa ($cantidad) al inventario de $ingrediente." }
    inventario[ingrediente] = (inventario[ingrediente] ?: 0) + cantidad
}

// Pide por consola cuántas unidades agregar de cada ingrediente faltante.
// try-catch: se vuelve a preguntar si escribe letras o una cantidad negativa.
fun reabastecerInteractivo(inventario: MutableMap<String, Int>, faltantes: Map<String, Int>) {
    for ((ingrediente, falta) in faltantes) {
        var registrado = false
        while (!registrado) {
            print("Unidades a agregar de $ingrediente (faltan $falta, 0 para omitir): ")
            try {
                val cantidad = leerLinea().toInt()
                reabastecer(inventario, ingrediente, cantidad)
                registrado = true
            } catch (e: NumberFormatException) {
                println("\nError: debe ingresar solo números (sin letras ni símbolos).\n")
            } catch (e: IllegalArgumentException) {
                println("\nError: ${e.message}\n")
            }
        }
    }
}
fun main() {
    // ===== Paso 1: Declaración de variables =====

    // Cliente: se llenan con lo que ingresa el usuario
    val nombre: String
    val direccion: String
    val telefono: String

    // Pizza
    val nombrePizza: String = "Pepperoni"
    val precioUnitario: Double = 8990.0
    var ingredientesDisponibles: Int = 10 // var porque se descuenta al confirmar un pedido
    val ingredientesPorPizza: Int = 2

    // Pedido
    val idPedido: Int = 1001
    val cantidadPizzas: Int
    val totalPedido: Double
    var estadoPedido: String = "Preparación"

    // Repartidores
    val repartidores: List<String> = listOf("Juan Pérez", "María González", "Pedro Soto")
    var repartidorAsignado: String = "Sin asignar"

    println("========== PIZZERÍA KOTLIN ==========")

    // ===== Paso 2: Entrada y salida de datos =====
    print("Ingrese el nombre del cliente: ")
    val nombreIngresado = readlnOrNull()
    // Si viene nulo o vacío se asigna un valor por defecto
    nombre = if (nombreIngresado.isNullOrBlank()) "Cliente sin nombre" else nombreIngresado.trim()

    print("Ingrese la dirección de entrega: ")
    val direccionIngresada = readlnOrNull()
    direccion = if (direccionIngresada.isNullOrBlank()) "Retiro en local" else direccionIngresada.trim()

    print("Ingrese el teléfono de contacto (solo números): ")
    val telefonoIngresado = readlnOrNull()?.trim() ?: ""
    // Se valida que tenga al menos 8 dígitos y que sean solo números
    telefono = if (telefonoIngresado.length >= 8 && telefonoIngresado.toLongOrNull() != null) {
        telefonoIngresado
    } else {
        println("Teléfono inválido, se asigna el valor por defecto.")
        "000000000"
    }

    println("\nCliente registrado:")
    println("Nombre: $nombre")
    println("Dirección: $direccion")
    println("Teléfono: $telefono")

    println("\nPizza disponible: $nombrePizza - $%,.0f".format(precioUnitario))
    print("¿Cuántas pizzas desea?: ")
    // toInt() lanza NumberFormatException si no se ingresa un número
    cantidadPizzas = try {
        val cantidad = readlnOrNull()?.trim()?.toInt() ?: 1
        if (cantidad > 0) cantidad else 1
    } catch (e: NumberFormatException) {
        println("Cantidad inválida, se asigna 1 pizza por defecto.")
        1
    }

    // ===== Paso 3: Cálculo del total con descuento =====
    val subtotal: Double = precioUnitario * cantidadPizzas
    // 10% de descuento si el pedido tiene más de 3 pizzas
    val descuento: Double = if (cantidadPizzas > 3) subtotal * 0.10 else 0.0
    totalPedido = subtotal - descuento

    // ===== Paso 4: Verificación de stock =====
    val ingredientesNecesarios: Int = cantidadPizzas * ingredientesPorPizza
    val pedidoConfirmado: Boolean = if (ingredientesDisponibles >= ingredientesNecesarios) {
        ingredientesDisponibles -= ingredientesNecesarios
        println("\nHay ingredientes suficientes. Pedido confirmado.")
        println("Ingredientes restantes en inventario: $ingredientesDisponibles")
        true
    } else {
        println("\nNo hay ingredientes suficientes. El pedido no puede ser completado.")
        println("Se necesitan $ingredientesNecesarios y solo hay $ingredientesDisponibles.")
        false
    }

    // ===== Paso 5: Asignación de repartidor =====
    if (pedidoConfirmado) {
        // Se reparte por turnos usando el id del pedido
        repartidorAsignado = repartidores[idPedido % repartidores.size]
        estadoPedido = "En Camino"
    } else {
        estadoPedido = "Cancelado"
    }

    // ===== Paso 6 y 7: Resumen del pedido =====
    println("\n========== RESUMEN DEL PEDIDO ==========")
    println("Pedido N°: $idPedido")
    println("--- Cliente ---")
    println("Nombre:     $nombre")
    println("Dirección:  $direccion")
    println("Teléfono:   $telefono")
    println("--- Pizza ---")
    println("Pizza:      $nombrePizza")
    println("Precio:     $%,.0f".format(precioUnitario))
    println("--- Detalle ---")
    println("Cantidad:   $cantidadPizzas")
    println("Subtotal:   $%,.0f".format(subtotal))
    println("Descuento:  $%,.0f".format(descuento))
    println("Total:      $%,.0f".format(totalPedido))
    println("Estado:     $estadoPedido")
    println("Repartidor: $repartidorAsignado")
    println("========================================")
}

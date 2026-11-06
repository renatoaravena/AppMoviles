fun main() {
    // Declaración de variables

    // Cliente
    val nombre: String
    val direccion: String
    val telefono: String

    // Pizza
    val nombrePizza = "Pepperoni"
    val precioUnitario = 8990.0
    var ingredientesDisponibles = 10 // var porque se descuenta al confirmar un pedido
    val ingredientesPorPizza = 2

    // Pedido
    val idPedido = 1001
    val cantidadPizzas: Int
    val totalPedido: Double
    var estadoPedido: String

    // Repartidores
    val repartidores: List<String> = listOf("Juan Pérez", "María González", "Pedro Soto")
    var repartidorAsignado = "Sin asignar"




    println("========== PIZZERÍA ==========")
    Thread.sleep(1000)


    //  Entrada y salida de datos


    // Se vuelve a preguntar mientras el nombre venga nulo o vacío hasta que se de uno correcto
    var nombreIngresado: String
    do {
        print("Ingrese el nombre del cliente: ")
        nombreIngresado = readlnOrNull()?.trim() ?: ""
        if (nombreIngresado.isEmpty()) println("\nError: El nombre no puede estar vacío.\n")
        Thread.sleep(500)
    } while (nombreIngresado.isEmpty())
    nombre = nombreIngresado

    // Para simplificar si la direccion de entrega esta vacia se tomara como retiro en local
    print("Ingrese la dirección de entrega: ")
    val direccionIngresada = readlnOrNull()
    direccion = if (direccionIngresada.isNullOrBlank()) "Retiro en local" else direccionIngresada.trim()

    // Se vuelve a preguntar hasta que tenga al menos 8 dígitos y sean solo números
    var telefonoIngresado: String
    do {
        print("Ingrese el teléfono de contacto (solo números): ")
        telefonoIngresado = readlnOrNull()?.trim() ?: ""
        val esValido = telefonoIngresado.length >= 8 && telefonoIngresado.all { it.isDigit() } //Revisa la entrada y ve si son digitos del 0 al 9, eso hace el .all{it.isDigit()}
        if (!esValido) println("\nError: Teléfono inválido, debe tener al menos 8 dígitos y solo números.\n")
        Thread.sleep(500)
    } while (!esValido)
    telefono = telefonoIngresado

    println("\nCliente registrado:")
    println("Nombre: $nombre")
    println("Dirección: $direccion")
    println("Teléfono: $telefono")

    println("\nPizza disponible: $nombrePizza - $$precioUnitario")
    //Utilizamos bloque try-catch debido a que tenemos un toInt, en caso de iungresarse una letra nos arrojara error
    // Se vuelve a preguntar hasta que la cantidad sea un número mayor a 0 y sea numero
    var cantidadIngresada: Int
    do {
        print("¿Cuántas pizzas desea?: ")
        cantidadIngresada = try {
            val texto = readlnOrNull()?.trim() ?: ""
            if (texto.isEmpty()) {
                println("\nError: la cantidad no puede estar vacía.\n")
                Thread.sleep(500)
                0
            } else {
                val cantidad = texto.toInt()
                if (cantidad <= 0) println("\nError: la cantidad debe ser mayor a 0.\n")
                Thread.sleep(500)
                cantidad
            }
        } catch (_: NumberFormatException) {
            // toInt() lanza este error cuando se ingresan letras u otros caracteres
            println("\nError: debe ingresar solo números, no letras.\n")
            Thread.sleep(500)
            0
        }
    } while (cantidadIngresada <= 0)
    cantidadPizzas = cantidadIngresada




    //  Cálculo del total con descuento 
    val subtotal: Double = precioUnitario * cantidadPizzas
    // 10% de descuento si el pedido tiene más de 3 pizzas
    val descuento: Double = if (cantidadPizzas > 3) subtotal * 0.10 else 0.0
    totalPedido = subtotal - descuento

    //  Verificación de stock
    val ingredientesNecesarios: Int = cantidadPizzas * ingredientesPorPizza
    val pedidoConfirmado: Boolean = if (ingredientesDisponibles >= ingredientesNecesarios) {
        ingredientesDisponibles -= ingredientesNecesarios
        println("\nHay ingredientes suficientes. Pedido confirmado.")
        println("Ingredientes restantes en inventario: $ingredientesDisponibles")
        estadoPedido = "Preparacion"
        println("\n Estado del pedido: $estadoPedido")
        true
    } else {
        println("\nNo hay ingredientes suficientes. El pedido no puede ser completado.")
        println("Se necesitan $ingredientesNecesarios y solo hay $ingredientesDisponibles.")
        false
    }

    //  Asignación de repartidor
    if (pedidoConfirmado) {
        // Se reparte por turnos usando el id del pedido
        repartidorAsignado = repartidores[idPedido % repartidores.size]
        estadoPedido = "En Camino"
    } else {
        estadoPedido = "Cancelado"
    }

    // Resumen del pedido

    Thread.sleep(1000)
    println("\n========== RESUMEN DEL PEDIDO ==========")

    println("Pedido N°: $idPedido")
    println("--- Cliente ---")
    println("Nombre:     $nombre")
    println("Dirección:  $direccion")
    println("Teléfono:   $telefono")
    println("--- Pizza ---")
    println("Pizza:      $nombrePizza")
    println("Precio:     $$precioUnitario")
    println("--- Detalle ---")
    println("Cantidad:   $cantidadPizzas")
    println("Subtotal:   $$subtotal")
    println("Descuento:  $$descuento")
    println("Total:      $$totalPedido")
    println("Estado:     $estadoPedido")
    println("Repartidor: $repartidorAsignado")
    println("========================================")
}

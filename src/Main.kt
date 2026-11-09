import model.*
import model.Pedido.EstadoPedido
import utils.*

fun main() {
    // ---------- Datos iniciales ----------

    // val porque la referencia al Map nunca cambia pero su contenido si se modifica (MutableMap)
    val inventario: MutableMap<String, Int> = mutableMapOf(
        "Masa" to 12,
        "Salsa de tomate" to 12,
        "Queso mozzarella" to 20,
        "Pepperoni" to 8,
        "Jamón" to 6,
        "Piña" to 4,
        "Champiñones" to 5,
        "Aceitunas" to 5
    )

    //Todas las pizzas llevan Masa y Salsa de tomate, por lo que se crean como variables para no repetirlas en cada pizza
    val masa = Ingrediente("Masa", 1)
    val salsa = Ingrediente("Salsa de tomate", 1)

    //Empezamos con el menu de pizzas con 3 tipos predeterminados, se podrian agregar mas
    val menu: List<Pizza> = listOf(
        Pizza("Pepperoni", 8990.0, listOf(masa, salsa, Ingrediente("Queso mozzarella", 2), Ingrediente("Pepperoni", 2))),
        Pizza("Hawaiana", 9490.0, listOf(masa, salsa, Ingrediente("Queso mozzarella", 2), Ingrediente("Jamón", 1), Ingrediente("Piña", 1))),
        Pizza("Vegetariana", 8490.0, listOf(masa, salsa, Ingrediente("Queso mozzarella", 1), Ingrediente("Champiñones", 1), Ingrediente("Aceitunas", 1)))
    )

    //Repartidores predeterminados, se podrian agregar mas
    val repartidores: List<Repartidor> = listOf(
        Repartidor("Juan Pérez", "+56 9 1111 1111", "juan.perez@pizzeria.cl", 2.5),
        Repartidor("María González", "+56 9 2222 2222", "maria.gonzalez@pizzeria.cl", 1.2),
        Repartidor("Pedro Soto", "+56 9 3333 3333", "pedro.soto@pizzeria.cl", 4.0)
    )

    // Arreglo con los pedidos del día. Es var porque al agregar un pedido se crea un arreglo nuevo,seria mejor
    // utilizar mutableListOf pero se mantiene el arreglo por el enunciado
    var pedidosDelDia: Array<Pedido> = crearPedidosIniciales(menu, repartidores)
    var siguienteIdPedido = 1004   // var porque aumenta con cada pedido

    // ---------- Programa ----------
    println("========== PIZZERÍA ==========")
    pausa(1000)

    do {
        val pedido = registrarPedido(siguienteIdPedido, menu, inventario, repartidores) //Continua el prgrama en registrarPedido

        // Si el pedido fue rechazado (por falta de stock) no se agrega
        if (pedido != null) {
            pedidosDelDia += pedido
            siguienteIdPedido++

            pausa(1000)
            println("\n" + generarResumenPedido(pedido))
            notificarPedido(pedido)
        }
    } while (leerSiNo("\n¿Desea registrar otro pedido?"))

    // ---------- Cierre del día ----------
    imprimirReporteDelDia(pedidosDelDia)
    mostrarInventario(inventario)

    // Cliente y Repartidor son Usuario: se pueden mezclar en una misma lista
    val clientesDelDia: List<Usuario> = pedidosDelDia.map { it.cliente }.distinctBy { it.nombre }
    mostrarDirectorio(clientesDelDia + repartidores)
}

// Pedidos que ya estaban registrados antes de abrir el programa (datos de ejemplo para el reporte)
fun crearPedidosIniciales(menu: List<Pizza>, repartidores: List<Repartidor>): Array<Pedido> {
    val pepperoni = menu[0]
    val hawaiana = menu[1]
    val vegetariana = menu[2]

    val pedido1 = Pedido(
        1001,
        Cliente("Camila Rojas", "987654321", "camila.rojas@correo.cl", DIRECCION_RETIRO),
        listOf(pepperoni, pepperoni)
    )
    pedido1.cambiarEstado(EstadoPedido.LISTO)

    val pedido2 = Pedido(
        1002,
        Cliente("Diego Fuentes", "912345678", "diego.fuentes@correo.cl", "Av. Los Leones 123"),
        listOf(hawaiana, vegetariana),
        "Sin cebolla"
    )
    repartidores.find { it.nombre == "Pedro Soto" }?.entregarPedido(pedido2)

    val pedido3 = Pedido(
        1003,
        Cliente("Valentina Soto", "955512345", CORREO_POR_DEFECTO, DIRECCION_RETIRO),
        listOf(vegetariana)
    )

    return arrayOf(pedido1, pedido2, pedido3)
}

// Registra un pedido completo. Retorna null si no se pudo confirmar.
fun registrarPedido(
    id: Int,
    menu: List<Pizza>,
    inventario: MutableMap<String, Int>,
    repartidores: List<Repartidor>
): Pedido? {
    val cliente = registrarCliente()
    val notas = leerNotas()
    val productos = seleccionarProductos(menu)

    if (productos.size > PIZZAS_PARA_DESCUENTO) {
        println("\n¡Su pedido supera las $PIZZAS_PARA_DESCUENTO pizzas! Se aplicará un ${(TASA_DESCUENTO * 100).toInt()}% de descuento.")
    }

    // Verificación de stock ANTES de aceptar el pedido
    val necesarios = calcularIngredientesDelPedido(productos)
    if (!confirmarStock(inventario, necesarios)) {
        return null
    }

    val pedido = Pedido(id, cliente, productos, notas)

    // try-catch: errores de costo (IllegalArgumentException) o de inventario (IllegalStateException)
    try {
        pedido.calcularTotal()   // valida que el costo del pedido sea correcto
        pedido.productosAgrupados().forEach { (pizza, cantidad) ->
            prepararLote(inventario, pizza.ingredientes, cantidad)   // descuenta del inventario
        }
    } catch (e: IllegalArgumentException) {
        println("\nError en el pedido: ${e.message}")
        return null
    } catch (e: IllegalStateException) {
        println("\nError de inventario: ${e.message}")
        return null
    }

    println("\nHay ingredientes suficientes. Pedido N°$id confirmado.")
    asignarEntrega(pedido, repartidores)
    return pedido
}

fun registrarCliente(): Cliente {
    println("\n--- Datos del cliente ---")
    // Se vuelve a preguntar hasta que el nombre tenga solo letras
    var nombre: String
    do {
        print("Ingrese el nombre del cliente: ")
        nombre = leerLinea()
        val nombreValido = esNombreValido(nombre)
        if (!nombreValido) {
            println("\nError: el nombre debe tener solo letras (mínimo 2) y no puede estar vacío.\n")
            pausa()
        }
    } while (!nombreValido)

    print("Ingrese la dirección de entrega (Enter para retiro en local): ")
    val direccion = leerLinea().ifEmpty { DIRECCION_RETIRO }

    print("Ingrese el teléfono de contacto: ")
    val telefono = validarTelefono(leerLinea())
    if (telefono == TELEFONO_POR_DEFECTO) {
        println("Aviso: el teléfono no es válido (8 a 12 dígitos). Se usará $TELEFONO_POR_DEFECTO.")
    }

    print("Ingrese el correo electrónico: ")
    val correo = validarCorreo(leerLinea())
    if (correo == CORREO_POR_DEFECTO) {
        println("Aviso: el correo no es válido. Se usará $CORREO_POR_DEFECTO.")
    }

    val cliente = Cliente(nombre, telefono, correo, direccion)
    println("\nCliente registrado:")
    println(cliente.descripcion())
    return cliente
}

// Notas opcionales, si el cliente no escribe nada, retorna null
fun leerNotas(): String? {
    print("Notas del pedido (opcional, Enter para omitir): ")
    return leerLinea().takeIf { it.isNotEmpty() }
}

fun seleccionarProductos(menu: List<Pizza>): List<Pizza> {
    println("\n--- Menú ---")
    menu.forEachIndexed { indice, pizza -> println("${indice + 1}. ${pizza.detallarProducto()}") }

    val productos = mutableListOf<Pizza>()
    do {
        val opcion = leerEnteroEnRango("\nSeleccione una pizza (1-${menu.size}): ", 1, menu.size)
        val elegida = menu[opcion - 1]
        val cantidad = leerEnteroEnRango("¿Cuántas pizzas ${elegida.nombre} desea?: ", 1, 50)

        repeat(cantidad) { productos.add(elegida) }
        println("Agregado: $cantidad x ${elegida.nombre}")
    } while (leerSiNo("¿Desea agregar otra pizza al pedido?"))

    return productos
}

// Retorna true si el pedido tiene stock suficiente, permite reabastecer y verifica otra vez
fun confirmarStock(inventario: MutableMap<String, Int>, necesarios: Map<String, Int>): Boolean {
    println("\nVerificando ingredientes en el inventario...")
    var faltantes = verificarDisponibilidad(inventario, necesarios)
    if (faltantes.isEmpty()) return true

    mostrarPasosReabastecimiento(faltantes)

    if (leerSiNo("\n¿Desea registrar ahora el reabastecimiento?")) {
        reabastecerInteractivo(inventario, faltantes)
        println("\nVolviendo a verificar los ingredientes...")
        faltantes = verificarDisponibilidad(inventario, necesarios)
    }

    if (faltantes.isNotEmpty()) {
        println("\nEl pedido no puede ser completado por falta de ingredientes. Pedido rechazado.")
        return false
    }
    return true
}

// Retiro en local -> Listo. Despacho -> se busca el repartidor disponible más cercano con find
fun asignarEntrega(pedido: Pedido, repartidores: List<Repartidor>) {
    if (pedido.cliente.esRetiroEnLocal) {
        pedido.cambiarEstado(EstadoPedido.LISTO)
        println("El cliente retira en el local. Pedido listo.")
        return
    }

    val masCercano = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
    if (masCercano != null) {
        println(masCercano.entregarPedido(pedido))
    } else {
        println("No hay repartidores disponibles. El pedido queda en preparación hasta que se libere uno.")
    }
}

// let ejecuta el bloque solo si el valor NO es null
fun notificarPedido(pedido: Pedido) {
    println("\n--- Notificaciones ---")

    val correoCliente: String? = pedido.cliente.correo.takeIf { it != CORREO_POR_DEFECTO }
    correoCliente?.let { println("Correo enviado a $it: su pedido N°${pedido.id} está '${pedido.estado.descripcion}'.") }
    if (correoCliente == null) {
        println("El cliente no tiene un correo válido registrado, no se envía correo.")
    }

    pedido.repartidor?.let { println("Aviso enviado a ${it.nombre}: debe entregar el pedido N°${pedido.id}.") }
    pedido.notas?.let { println("Notas enviadas a cocina: \"$it\"") }
}
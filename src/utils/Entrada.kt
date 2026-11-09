package utils

import kotlin.system.exitProcess

// RANGO permitido para la cantidad de productos de un pedido
val RANGO_CANTIDAD: IntRange = 1..100

fun pausa(milisegundos: Long = 500) = Thread.sleep(milisegundos)

// Lee una línea. readlnOrNull() retorna null si se cierra la entrada, y con ?: se
// termina el programa en vez de quedar preguntando para siempre.
fun leerLinea(): String =
    readlnOrNull()?.trim() ?: run {
        println("\nEntrada finalizada. Cerrando el programa.")
        exitProcess(0)
    }

// Se vuelve a preguntar hasta que el nombre sea válido
fun leerNombre(): String {
    while (true) {
        print("Ingrese el nombre del cliente: ")
        val nombre = leerLinea()
        if (esNombreValido(nombre)) return nombre

        println("\nError: el nombre debe tener solo letras (mínimo 2) y no puede estar vacío.\n")
        pausa()
    }
}

// Se vuelve a preguntar hasta que el correo cumpla el formato (Regex)
fun leerCorreo(): String {
    while (true) {
        print("Ingrese el correo electrónico (ej: nombre@dominio.com): ")
        val correo = leerLinea().lowercase()
        if (esCorreoValido(correo)) return correo

        println("\nError: el correo no tiene un formato válido. Ejemplo: nombre@dominio.com\n")
        pausa()
    }
}

// Se vuelve a preguntar hasta que el teléfono sea válido; retorna el teléfono ya formateado
fun leerTelefono(): String {
    while (true) {
        print("Ingrese el teléfono (ej: +56 9 1234 1234): ")
        val telefonoFormateado = formatearTelefono(leerLinea())
        if (telefonoFormateado != null) return telefonoFormateado

        println("\nError: ingrese un teléfono móvil chileno válido. Ejemplo: +56 9 1234 1234\n")
        pausa()
    }
}

// Lee un número entero que debe estar dentro de un rango
// toInt() lanza NumberFormatException cuando se ingresan letras u otros caracteres;
// require() lanza IllegalArgumentException cuando el número está fuera del rango.
fun leerEnteroEnRango(mensaje: String, rango: IntRange): Int {
    while (true) {
        print(mensaje)
        try {
            val numero = leerLinea().toInt()
            require(numero in rango) { "el número debe estar entre ${rango.first} y ${rango.last}." }
            return numero
        } catch (e: NumberFormatException) {
            // Se captura primero porque es una subclase de IllegalArgumentException
            println("\nError: debe ingresar solo números (sin letras ni símbolos).\n")
        } catch (e: IllegalArgumentException) {
            println("\nError: ${e.message}\n")
        }
        pausa()
    }
}

// Pregunta de sí/no
fun leerSiNo(mensaje: String): Boolean {
    while (true) {
        print("$mensaje (s/n): ")
        when (leerLinea().lowercase()) {
            "s", "si", "sí" -> return true
            "n", "no" -> return false
            else -> println("\nError: responda con 's' o 'n'.\n")
        }
    }
}
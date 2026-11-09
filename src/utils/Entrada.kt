package utils

import kotlin.system.exitProcess

//Manejo de errores en la entrada de datos

//Pausa para hacer mas dinamico el programa
fun pausa(milisegundos: Long = 500) = Thread.sleep(milisegundos)

// Lee una línea. readlnOrNull() retorna null si se cierra la entrada, y con ?: se
// termina el programa en vez de quedar preguntando para siempre.
fun leerLinea(): String =
    readlnOrNull()?.trim() ?: run {
        println("\nEntrada finalizada. Cerrando el programa.")
        exitProcess(0)
    }

// Lee un número entero dentro de un rango.
// toInt() lanza NumberFormatException cuando se ingresan letras u otros caracteres
// require() lanza IllegalArgumentException cuando el número está fuera del rango
fun leerEnteroEnRango(mensaje: String, minimo: Int, maximo: Int): Int {
    while (true) {
        print(mensaje)
        try {
            val numero = leerLinea().toInt()
            require(numero in minimo..maximo) { "el número debe estar entre $minimo y $maximo." }
            return numero
        } catch (e: NumberFormatException) {
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

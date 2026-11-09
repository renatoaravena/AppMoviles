package utils

//Validar correo y telefono sean validos y los cambiamos por los valors por defecto que aparece en el enunciado

const val TELEFONO_POR_DEFECTO = "(000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

// Expresión regular: texto@dominio.extensión
private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

fun esTelefonoValido(entrada: String): Boolean {
    // Se aceptan espacios, guiones, paréntesis y +, pero no letras
    val caracteresPermitidos = entrada.all { it.isDigit() || it in " -+()" }
    val cantidadDigitos = entrada.count { it.isDigit() }
    return caracteresPermitidos && cantidadDigitos in 8..12
}

fun validarTelefono(entrada: String): String =
    if (esTelefonoValido(entrada)) entrada.trim() else TELEFONO_POR_DEFECTO

fun esCorreoValido(entrada: String): Boolean = regexCorreo.matches(entrada)

fun validarCorreo(entrada: String): String =
    if (esCorreoValido(entrada)) entrada.trim().lowercase() else CORREO_POR_DEFECTO

// El nombre debe tener solo letras (con tildes y ñ) y espacios, y al menos 2 letras
fun esNombreValido(entrada: String): Boolean {
    val soloLetrasYEspacios = entrada.all { it.isLetter() || it == ' ' }
    val cantidadLetras = entrada.count { it.isLetter() }
    return soloLetrasYEspacios && cantidadLetras >= 2
}
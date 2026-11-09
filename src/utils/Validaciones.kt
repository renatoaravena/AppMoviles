package utils


// Expresión regular (Regex) para el correo: texto@dominio.extensión
private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

// Regex para un teléfono con formato chileno: código de país +56, número 9 y 8 dígitos.
private val regexTelefono = Regex("^(56)(9)(\\d{4})(\\d{4})$")

fun esCorreoValido(entrada: String): Boolean = regexCorreo.matches(entrada)

// El nombre debe tener solo letras (con tildes y ñ) y espacios, y al menos 2 letras
fun esNombreValido(entrada: String): Boolean {
    val soloLetrasYEspacios = entrada.all { it.isLetter() || it == ' ' }
    val cantidadLetras = entrada.count { it.isLetter() }
    return soloLetrasYEspacios && cantidadLetras >= 2
}

// Procesa un teléfono y lo deja con el formato uniforme +56 9 1234 1234.
// Retorna null si el teléfono no es válido.
fun formatearTelefono(entrada: String): String? {
    // Solo se aceptan dígitos, espacios, guiones, paréntesis y +
    if (!entrada.all { it.isDigit() || it in " -+()" }) return null

    var digitos = entrada.filter { it.isDigit() }

    // Si no trae código de país (9 dígitos) se asume Chile (56).
    if (digitos.length == 9) digitos = "56$digitos"

    // Si no tiene exactamente 11 dígitos o no es un numero chileno, es inválido.
    val partes = regexTelefono.matchEntire(digitos) ?: return null

    // destructured separa los 4 grupos de la Regex en variables (desestructuración)
    val (pais, prefijo, parte1, parte2) = partes.destructured
    return "+$pais $prefijo $parte1 $parte2"
}
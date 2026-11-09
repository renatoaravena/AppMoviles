package model

// Clase base open para permitir herencia
open class Usuario(
    val nombre: String,
    val telefono: String,
    val correo: String
) {
    // open, asi las clases hijas pueden sobrescribirlo
    open fun descripcion(): String = "$nombre | Tel: $telefono | Correo: $correo"
}
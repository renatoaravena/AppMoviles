package model

// Si el cliente no ingresa dirección se toma como retiro en el local
const val DIRECCION_RETIRO = "Retiro en local"

class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    // true cuando el cliente no pidió despacho a domicilio
    val esRetiroEnLocal: Boolean
        get() = direccion == DIRECCION_RETIRO

    override fun descripcion(): String = "Cliente: ${super.descripcion()} | Dirección: $direccion"
}
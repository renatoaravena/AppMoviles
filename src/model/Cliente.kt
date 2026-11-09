package model

class Cliente(
    nombre: String,
    telefono: String,
    correo: String,
    val direccion: String
) : Usuario(nombre, telefono, correo) {

    companion object {
        // Si el cliente no ingresa dirección se toma como retiro en el local
        const val DIRECCION_RETIRO = "Retiro en local"
    }

    // true cuando el cliente no pidió despacho a domicilio
    val esRetiroEnLocal: Boolean
        get() = direccion == DIRECCION_RETIRO

    override fun descripcion(): String = "Cliente: ${super.descripcion()} | Dirección: $direccion"

    // Desestructurar un cliente en 3 variables: nombre, correo y teléfono
    // Permite escribir: val (nombre, correo, telefono) = cliente
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // Igualdad
    // Dos clientes son iguales si tienen el mismo nombre y el mismo correo
    override fun equals(other: Any?): Boolean {
        if (this === other) return true //Si tienen misma instancia
        if (other !is Cliente) return false
        return nombre.equals(other.nombre, ignoreCase = true) && correo == other.correo
    }

    // Debe ser coherente con equals(), como ignoramos mayúsculas en el nombre, usamos lowercase()
    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + correo.hashCode()
}
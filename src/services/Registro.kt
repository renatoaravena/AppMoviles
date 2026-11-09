package services

import model.*

//Para evitar duplicados, se comparan los objetos por sus propiedades relevantes (nombre y correo para Cliente, nombre y categoría para Producto)

// Si ya existe un cliente igual (mismo nombre y correo) se reutiliza, y si no, se agrega a la lista.
fun registrarClienteSinDuplicar(clientes: MutableList<Cliente>, nuevo: Cliente): Cliente {
    val existente = clientes.find { it == nuevo }   // == usa el equals() de Cliente
    if (existente != null) {
        println("Aviso: ${existente.nombre} ya estaba registrado. Se usarán los datos que ya estaban guardados.")
        return existente
    }
    clientes.add(nuevo)
    return nuevo
}

// Si ya existe un producto igual (mismo nombre y categoría) no se agrega al menú
// Retorna true si se agregó y false si estaba duplicado
fun registrarProductoSinDuplicar(menu: MutableList<Producto>, nuevo: Producto): Boolean {
    if (nuevo in menu) {   // "in" usa el equals() de Producto
        println("Aviso: $nuevo ya está en el menú, no se agrega de nuevo.")
        return false
    }
    menu.add(nuevo)
    return true
}
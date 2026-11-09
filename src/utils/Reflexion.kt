package utils

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import model.Producto
import model.Promocionable


// La clase de este producto tiene la anotación @Promocionable?
fun esPromocionable(producto: Producto): Boolean =
    producto::class.annotations.any { it is Promocionable }

// Lee el texto que trae la anotación, es null si la clase no la tiene
fun descripcionPromocion(producto: Producto): String? =
    producto::class.findAnnotation<Promocionable>()?.descripcion

// Lista todas las propiedades y todos los métodos de una clase
fun listarMiembros(clase: KClass<*>) {
    println("\nClase ${clase.simpleName}:")

    println("  Propiedades:")
    clase.members.filterIsInstance<KProperty<*>>()
        .sortedBy { it.name }
        .forEach { println("    - ${it.name}") }

    println("  Métodos:")
    clase.members.filterIsInstance<KFunction<*>>()
        .sortedBy { it.name }
        .forEach { println("    - ${it.name}()") }
}
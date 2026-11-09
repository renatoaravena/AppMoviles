package model

// Anotación personalizada que marca las clases de productos que califican para promociones
// @Target significa que solo se puede poner sobre clases
// @Retention(RUNTIME) es que se puede leer mientras el programa corre con reflection
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented // Se documenta la anotación
annotation class Promocionable(val descripcion: String = "Califica para promociones")
package model

// Ingrediente adicional que se agrega al pedido como queso extra
// NO lleva @Promocionable por lo que no califica para promociones
class Extra(
    nombre: String,
    precio: Double,
    ingredientes: List<Ingrediente>
) : Producto(nombre, precio, Categoria.INGREDIENTE, ingredientes)
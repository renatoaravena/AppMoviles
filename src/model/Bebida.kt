package model


// @Promocionable: las bebidas también califican para promociones
@Promocionable("Las bebidas participan en las promociones")
class Bebida(
    nombre: String,
    precio: Double,
    ingredientes: List<Ingrediente>
) : Producto(nombre, precio, Categoria.BEBIDA, ingredientes)
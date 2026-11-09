package model

// @Promocionable: las pizzas califican para promociones
@Promocionable("Las pizzas participan en las promociones")
class Pizza(
    nombre: String,
    precio: Double,
    ingredientes: List<Ingrediente>
) : Producto(nombre, precio, Categoria.PIZZA, ingredientes)
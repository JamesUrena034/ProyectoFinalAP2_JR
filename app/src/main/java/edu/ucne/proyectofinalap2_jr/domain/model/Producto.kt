package edu.ucne.proyectofinalap2_jr.domain.model

data class Producto(
    val productoId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val categoriaId: String = "",
    val disponible: Boolean = true,
    val stock: Int = 0
)
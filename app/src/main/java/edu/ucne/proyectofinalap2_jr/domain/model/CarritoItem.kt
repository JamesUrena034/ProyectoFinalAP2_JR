package edu.ucne.proyectofinalap2_jr.domain.model

data class CarritoItem(
    val productoId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val imagen: String = ""
)
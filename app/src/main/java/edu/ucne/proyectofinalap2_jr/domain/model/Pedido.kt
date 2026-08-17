package edu.ucne.proyectofinalap2_jr.domain.model

data class Pedido(
    val pedidoId: String = "",
    val usuarioId: String = "",
    val productos: List<ItemPedido> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val fecha: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val metodoPago: String = ""
)

data class ItemPedido(
    val productoId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val imagen: String = ""
)
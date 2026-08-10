package edu.ucne.proyectofinalap2_jr.presentation.pedido

import edu.ucne.proyectofinalap2_jr.domain.model.Pedido

data class AdminPedidosUiState(
    val isLoading: Boolean = false,
    val pedidos: List<Pedido> = emptyList(),
    val error: String? = null
)
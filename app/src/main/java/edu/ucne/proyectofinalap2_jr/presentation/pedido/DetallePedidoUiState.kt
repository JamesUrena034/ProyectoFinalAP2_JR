package edu.ucne.proyectofinalap2_jr.presentation.pedido

import edu.ucne.proyectofinalap2_jr.domain.model.Pedido

data class DetallePedidoUiState(
    val isLoading: Boolean = false,
    val pedido: Pedido? = null,
    val error: String? = null
)
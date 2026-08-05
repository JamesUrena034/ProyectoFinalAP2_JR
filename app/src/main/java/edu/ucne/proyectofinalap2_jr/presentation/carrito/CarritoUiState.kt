package edu.ucne.proyectofinalap2_jr.presentation.carrito

import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem

data class CarritoUiState(
    val items: List<CarritoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val pedidoExitoso: Boolean = false,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val fechaInicioError: String? = null,
    val fechaFinError: String? = null
) {
    val total: Double get() = items.sumOf { it.precio * it.cantidad }
}
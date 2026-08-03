package edu.ucne.proyectofinalap2_jr.presentation.producto.detail

import edu.ucne.proyectofinalap2_jr.domain.model.Producto

data class ProductoDetailUiState(
    val isLoading: Boolean = false,
    val producto: Producto? = null,
    val error: String? = null
)
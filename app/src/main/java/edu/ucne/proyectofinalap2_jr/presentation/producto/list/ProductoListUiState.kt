package edu.ucne.proyectofinalap2_jr.presentation.producto.list

import edu.ucne.proyectofinalap2_jr.domain.model.Producto

data class ProductoListUiState(
    val isLoading: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val error: String? = null
)
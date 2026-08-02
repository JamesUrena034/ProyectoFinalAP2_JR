package edu.ucne.proyectofinalap2_jr.presentation.home

import edu.ucne.proyectofinalap2_jr.domain.model.Producto

data class HomeUiState(
    val isLoading: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val error: String? = null,
    val userNombre: String = "",
    val userRol: String = "usuario"
)
package edu.ucne.proyectofinalap2_jr.presentation.categoria

import edu.ucne.proyectofinalap2_jr.domain.model.Categoria

data class CategoriaListUiState(
    val isLoading: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val error: String? = null
)
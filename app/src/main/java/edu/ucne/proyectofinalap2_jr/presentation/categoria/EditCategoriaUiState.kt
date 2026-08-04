package edu.ucne.proyectofinalap2_jr.presentation.categoria

data class EditCategoriaUiState(
    val categoriaId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagen: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null,
    val nombreError: String? = null
)
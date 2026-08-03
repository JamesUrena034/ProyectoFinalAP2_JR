package edu.ucne.proyectofinalap2_jr.presentation.perfil

import edu.ucne.proyectofinalap2_jr.domain.model.Usuario

data class PerfilUiState(
    val isLoading: Boolean = false,
    val usuario: Usuario? = null,
    val error: String? = null
)
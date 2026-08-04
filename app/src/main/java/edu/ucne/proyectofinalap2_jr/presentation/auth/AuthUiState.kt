package edu.ucne.proyectofinalap2_jr.presentation.auth

import com.google.firebase.auth.FirebaseUser
import edu.ucne.proyectofinalap2_jr.domain.model.Usuario

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val usuario: Usuario? = null,
    val errorMessage: String? = null
) {
    val isAdmin: Boolean get() = usuario?.rol == "admin"
}
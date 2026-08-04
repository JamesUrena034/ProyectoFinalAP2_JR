package edu.ucne.proyectofinalap2_jr.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.auth.GetCurrentUserUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.auth.SignInUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init { checkSession() }

    private fun checkSession() {
        val user = getCurrentUserUseCase()
        if (user != null) {
            _state.update { it.copy(isLoading = true, user = user) }
            viewModelScope.launch {
                val userData = authRepository.getUserData(user.uid)
                _state.update { it.copy(isLoading = false, usuario = userData) }
            }
        }
    }

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignInWithGoogle -> signIn(intent.context)
            is AuthIntent.SignOut -> signOut()
        }
    }

    private fun signIn(context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = signInUseCase(context)
            result.fold(
                onSuccess = { user ->
                    val userData = authRepository.getUserData(user.uid)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            usuario = userData
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _state.update { AuthUiState() }
        }
    }
}
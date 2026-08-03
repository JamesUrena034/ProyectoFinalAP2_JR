package edu.ucne.proyectofinalap2_jr.presentation.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.GetPedidosByUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisPedidosViewModel @Inject constructor(
    private val getPedidosByUsuarioUseCase: GetPedidosByUsuarioUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MisPedidosUiState())
    val state: StateFlow<MisPedidosUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPedidosByUsuarioUseCase(uid).collect { pedidos ->
                _state.update { it.copy(isLoading = false, pedidos = pedidos) }
            }
        }
    }
}
package edu.ucne.proyectofinalap2_jr.presentation.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.GetPedidosUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.UpdateEstadoPedidoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPedidosViewModel @Inject constructor(
    private val getPedidosUseCase: GetPedidosUseCase,
    private val updateEstadoPedidoUseCase: UpdateEstadoPedidoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminPedidosUiState())
    val state: StateFlow<AdminPedidosUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPedidosUseCase().collect { pedidos ->
                _state.update { it.copy(isLoading = false, pedidos = pedidos) }
            }
        }
    }

    fun updateEstado(id: String, estado: String) {
        viewModelScope.launch {
            updateEstadoPedidoUseCase(id, estado)
        }
    }
}
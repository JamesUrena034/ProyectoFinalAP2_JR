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
class DetallePedidoViewModel @Inject constructor(
    private val getPedidosUseCase: GetPedidosUseCase,
    private val updateEstadoPedidoUseCase: UpdateEstadoPedidoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetallePedidoUiState())
    val state: StateFlow<DetallePedidoUiState> = _state.asStateFlow()

    fun load(pedidoId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPedidosUseCase().collect { pedidos ->
                val pedido = pedidos.find { it.pedidoId == pedidoId }
                _state.update { it.copy(isLoading = false, pedido = pedido) }
            }
        }
    }

    fun updateEstado(id: String, estado: String) {
        viewModelScope.launch {
            updateEstadoPedidoUseCase(id, estado)
        }
    }
}
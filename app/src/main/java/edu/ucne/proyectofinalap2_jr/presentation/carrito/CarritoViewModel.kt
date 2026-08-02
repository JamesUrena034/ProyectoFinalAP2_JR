package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem
import edu.ucne.proyectofinalap2_jr.domain.model.ItemPedido
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.SavePedidoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CarritoViewModel @Inject constructor(
    private val savePedidoUseCase: SavePedidoUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CarritoUiState())
    val state: StateFlow<CarritoUiState> = _state.asStateFlow()

    fun agregarItem(item: CarritoItem) {
        val items = _state.value.items.toMutableList()
        val existente = items.indexOfFirst { it.productoId == item.productoId }
        if (existente >= 0) {
            items[existente] = items[existente].copy(cantidad = items[existente].cantidad + 1)
        } else {
            items.add(item)
        }
        _state.update { it.copy(items = items) }
    }

    fun eliminarItem(productoId: String) {
        _state.update { it.copy(items = it.items.filter { i -> i.productoId != productoId }) }
    }

    fun realizarPedido() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pedido = Pedido(
                    usuarioId = user.uid,
                    productos = _state.value.items.map {
                        ItemPedido(
                            productoId = it.productoId,
                            nombre = it.nombre,
                            precio = it.precio,
                            cantidad = it.cantidad,
                            imagen = it.imagen
                        )
                    },
                    total = _state.value.total,
                    estado = "pendiente",
                    fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date())
                )
                savePedidoUseCase(pedido)
                _state.update {
                    it.copy(isLoading = false, items = emptyList(), pedidoExitoso = true)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
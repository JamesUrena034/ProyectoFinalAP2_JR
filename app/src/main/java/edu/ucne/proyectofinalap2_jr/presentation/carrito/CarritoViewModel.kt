package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem
import edu.ucne.proyectofinalap2_jr.domain.model.ItemPedido
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.GetPedidosUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.pedido.SavePedidoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CarritoViewModel @Inject constructor(
    private val savePedidoUseCase: SavePedidoUseCase,
    private val getPedidosUseCase: GetPedidosUseCase,
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

    fun cambiarCantidad(productoId: String, cantidad: Int) {
        if (cantidad <= 0) {
            eliminarItem(productoId)
            return
        }
        val items = _state.value.items.toMutableList()
        val index = items.indexOfFirst { it.productoId == productoId }
        if (index >= 0) {
            items[index] = items[index].copy(cantidad = cantidad)
            _state.update { it.copy(items = items) }
        }
    }

    fun onFechaInicioSelected(millis: Long) {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = formatter.format(Date(millis))
        _state.update { it.copy(fechaInicio = fecha, fechaInicioError = null, fechasOcupadas = emptyList()) }
        verificarDisponibilidad()
    }

    fun onFechaFinSelected(millis: Long) {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = formatter.format(Date(millis))
        _state.update { it.copy(fechaFin = fecha, fechaFinError = null, fechasOcupadas = emptyList()) }
        verificarDisponibilidad()
    }

    private fun verificarDisponibilidad() {
        val s = _state.value
        if (s.fechaInicio.isBlank() || s.fechaFin.isBlank()) return
        viewModelScope.launch {
            try {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val inicio = formatter.parse(s.fechaInicio) ?: return@launch
                val fin = formatter.parse(s.fechaFin) ?: return@launch
                val pedidosExistentes = getPedidosUseCase().first()
                val productosOcupados = mutableListOf<String>()

                for (item in s.items) {
                    val ocupado = pedidosExistentes.any { pedido ->
                        pedido.estado != "cancelado" &&
                                pedido.productos.any { it.productoId == item.productoId } &&
                                run {
                                    val pedidoInicio = try { formatter.parse(pedido.fechaInicio) } catch (e: Exception) { null }
                                    val pedidoFin = try { formatter.parse(pedido.fechaFin) } catch (e: Exception) { null }
                                    if (pedidoInicio != null && pedidoFin != null) {
                                        !(fin.before(pedidoInicio) || inicio.after(pedidoFin))
                                    } else false
                                }
                    }
                    if (ocupado) productosOcupados.add(item.nombre)
                }
                _state.update { it.copy(fechasOcupadas = productosOcupados) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun limpiarCarrito() {
        _state.update { CarritoUiState() }
    }
}
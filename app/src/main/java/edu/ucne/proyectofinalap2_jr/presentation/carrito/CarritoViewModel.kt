package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem
import edu.ucne.proyectofinalap2_jr.domain.model.ItemPedido
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
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

    fun onFechaInicioChange(value: String) = _state.update {
        it.copy(fechaInicio = value, fechaInicioError = null)
    }

    fun onFechaFinChange(value: String) = _state.update {
        it.copy(fechaFin = value, fechaFinError = null)
    }

    fun realizarPedido() {
        val s = _state.value

        val fechaInicioError = if (s.fechaInicio.isBlank()) "La fecha de inicio es requerida" else null
        val fechaFinError = if (s.fechaFin.isBlank()) "La fecha de fin es requerida" else null

        if (fechaInicioError != null || fechaFinError != null) {
            _state.update {
                it.copy(
                    fechaInicioError = fechaInicioError,
                    fechaFinError = fechaFinError
                )
            }
            return
        }

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicio = try { formatter.parse(s.fechaInicio) } catch (e: Exception) { null }
        val fin = try { formatter.parse(s.fechaFin) } catch (e: Exception) { null }

        if (inicio == null) {
            _state.update { it.copy(fechaInicioError = "Formato inválido (dd/MM/yyyy)") }
            return
        }
        if (fin == null) {
            _state.update { it.copy(fechaFinError = "Formato inválido (dd/MM/yyyy)") }
            return
        }
        if (fin.before(inicio)) {
            _state.update { it.copy(fechaFinError = "La fecha de fin no puede ser antes de la fecha de inicio") }
            return
        }
        if (inicio.before(Date())) {
            _state.update { it.copy(fechaInicioError = "La fecha de inicio no puede ser en el pasado") }
            return
        }

        val user = authRepository.getCurrentUser() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
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

                if (productosOcupados.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Los siguientes productos no están disponibles en esas fechas: ${productosOcupados.joinToString(", ")}"
                        )
                    }
                    return@launch
                }

                val pedido = Pedido(
                    usuarioId = user.uid,
                    productos = s.items.map {
                        ItemPedido(
                            productoId = it.productoId,
                            nombre = it.nombre,
                            precio = it.precio,
                            cantidad = it.cantidad,
                            imagen = it.imagen
                        )
                    },
                    total = s.total,
                    estado = "pendiente",
                    fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                    fechaInicio = s.fechaInicio,
                    fechaFin = s.fechaFin
                )
                savePedidoUseCase(pedido)
                _state.update {
                    it.copy(
                        isLoading = false,
                        items = emptyList(),
                        pedidoExitoso = true,
                        fechaInicio = "",
                        fechaFin = ""
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.ItemPedido
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
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
class CheckoutViewModel @Inject constructor(
    private val savePedidoUseCase: SavePedidoUseCase,
    private val getPedidosUseCase: GetPedidosUseCase,
    private val authRepository: AuthRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    fun init(carritoState: CarritoUiState) {
        _state.update {
            it.copy(
                items = carritoState.items,
                total = carritoState.total,
                fechaInicio = carritoState.fechaInicio,
                fechaFin = carritoState.fechaFin
            )
        }
    }

    fun onMetodoPagoChange(metodo: String) = _state.update { it.copy(metodoPago = metodo) }

    fun onNombreTarjetaChange(value: String) = _state.update {
        it.copy(nombreTarjeta = value, nombreTarjetaError = if (value.isBlank()) "Requerido" else null)
    }

    fun onNumeroTarjetaChange(value: String) = _state.update {
        it.copy(numeroTarjeta = value, numeroTarjetaError = if (value.length < 16) "Número inválido" else null)
    }

    fun onFechaExpiracionChange(value: String) = _state.update {
        it.copy(fechaExpiracion = value, fechaExpiracionError = if (value.isBlank()) "Requerido" else null)
    }

    fun onCvvChange(value: String) = _state.update {
        it.copy(cvv = value, cvvError = if (value.length < 3) "CVV inválido" else null)
    }

    fun confirmarPago() {
        val s = _state.value

        if (s.metodoPago == "Tarjeta de Crédito/Débito") {
            val nombreError = if (s.nombreTarjeta.isBlank()) "Requerido" else null
            val numeroError = if (s.numeroTarjeta.length < 16) "Número inválido" else null
            val fechaError = if (s.fechaExpiracion.isBlank()) "Requerido" else null
            val cvvError = if (s.cvv.length < 3) "CVV inválido" else null

            if (nombreError != null || numeroError != null || fechaError != null || cvvError != null) {
                _state.update {
                    it.copy(
                        nombreTarjetaError = nombreError,
                        numeroTarjetaError = numeroError,
                        fechaExpiracionError = fechaError,
                        cvvError = cvvError
                    )
                }
                return
            }
        }

        val user = authRepository.getCurrentUser() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val pedidosExistentes = getPedidosUseCase().first()
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val inicio = try { formatter.parse(s.fechaInicio) } catch (e: Exception) { null }
                val fin = try { formatter.parse(s.fechaFin) } catch (e: Exception) { null }
                val productosOcupados = mutableListOf<String>()

                for (item in s.items) {
                    val ocupado = pedidosExistentes.any { pedido ->
                        pedido.estado != "cancelado" &&
                                pedido.productos.any { it.productoId == item.productoId } &&
                                run {
                                    val pedidoInicio = try { formatter.parse(pedido.fechaInicio) } catch (e: Exception) { null }
                                    val pedidoFin = try { formatter.parse(pedido.fechaFin) } catch (e: Exception) { null }
                                    if (pedidoInicio != null && pedidoFin != null && inicio != null && fin != null) {
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
                            error = "No disponibles en esas fechas: ${productosOcupados.joinToString(", ")}"
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
                    fechaFin = s.fechaFin,
                    metodoPago = s.metodoPago
                )

                savePedidoUseCase(pedido)

                for (item in s.items) {
                    val producto = productoRepository.getProductoById(item.productoId)
                    producto?.let { p ->
                        val nuevoStock = (p.stock - item.cantidad).coerceAtLeast(0)
                        productoRepository.saveProducto(p.copy(stock = nuevoStock))
                    }
                }

                _state.update { it.copy(isLoading = false, pedidoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
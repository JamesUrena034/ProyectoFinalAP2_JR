package edu.ucne.proyectofinalap2_jr.presentation.carrito

import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem

data class CheckoutUiState(
    val items: List<CarritoItem> = emptyList(),
    val total: Double = 0.0,
    val metodoPago: String = "Tarjeta de Crédito/Débito",
    val nombreTarjeta: String = "",
    val numeroTarjeta: String = "",
    val fechaExpiracion: String = "",
    val cvv: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val isLoading: Boolean = false,
    val pedidoExitoso: Boolean = false,
    val error: String? = null,
    val nombreTarjetaError: String? = null,
    val numeroTarjetaError: String? = null,
    val fechaExpiracionError: String? = null,
    val cvvError: String? = null
)
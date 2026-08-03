package edu.ucne.proyectofinalap2_jr.presentation.producto.create

import android.net.Uri

data class CreateProductoUiState(
    val productoId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val imagenUri: Uri? = null,
    val categoriaId: String = "",
    val disponible: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null,
    val nombreError: String? = null,
    val precioError: String? = null
)
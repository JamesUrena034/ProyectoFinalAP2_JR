package edu.ucne.proyectofinalap2_jr.presentation.producto.edit

import edu.ucne.proyectofinalap2_jr.domain.model.Categoria

data class EditProductoUiState(
    val productoId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val categoriaId: String = "",
    val categoriaNombre: String = "",
    val categorias: List<Categoria> = emptyList(),
    val disponible: Boolean = true,
    val stock: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null,
    val nombreError: String? = null,
    val descripcionError: String? = null,
    val precioError: String? = null,
    val imagenError: String? = null,
    val categoriaError: String? = null,
    val stockError: String? = null
)
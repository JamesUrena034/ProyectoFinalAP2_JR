package edu.ucne.proyectofinalap2_jr.presentation.producto.create

data class CreateProductoUiState(
    val productoId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = "",
    val categoriaId: String = "",
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
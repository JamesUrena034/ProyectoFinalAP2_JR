package edu.ucne.proyectofinalap2_jr.presentation.producto.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import edu.ucne.proyectofinalap2_jr.domain.model.Producto
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.categoria.GetCategoriasUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.SaveProductoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductoViewModel @Inject constructor(
    private val saveProductoUseCase: SaveProductoUseCase,
    private val productoRepository: ProductoRepository,
    private val getCategoriasUseCase: GetCategoriasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProductoUiState())
    val state: StateFlow<EditProductoUiState> = _state.asStateFlow()

    fun load(id: String) {
        if (id.isBlank()) return
        _state.value = EditProductoUiState()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                launch {
                    getCategoriasUseCase().collect { categorias ->
                        _state.update { it.copy(categorias = categorias) }
                    }
                }
                val producto = productoRepository.getProductoById(id)
                producto?.let { p ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            productoId = p.productoId,
                            nombre = p.nombre,
                            descripcion = p.descripcion,
                            precio = p.precio,
                            imagen = p.imagen,
                            categoriaId = p.categoriaId,
                            disponible = p.disponible,
                            stock = p.stock
                        )
                    }
                    val categoria = _state.value.categorias.find { it.categoriaId == p.categoriaId }
                    _state.update { it.copy(categoriaNombre = categoria?.nombre ?: "") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onNombreChange(value: String) = _state.update {
        it.copy(nombre = value, nombreError = if (value.isBlank()) "Nombre es requerido" else null)
    }

    fun onDescripcionChange(value: String) = _state.update {
        it.copy(descripcion = value, descripcionError = if (value.isBlank()) "Descripción es requerida" else null)
    }

    fun onPrecioChange(value: String) {
        val d = value.toDoubleOrNull()
        _state.update {
            it.copy(
                precio = d ?: 0.0,
                precioError = when {
                    value.isBlank() -> "Precio es requerido"
                    d == null -> "Ingrese un número válido"
                    d <= 0 -> "El precio debe ser mayor a 0"
                    else -> null
                }
            )
        }
    }

    fun onImagenChange(value: String) = _state.update {
        it.copy(imagen = value, imagenError = if (value.isBlank()) "URL de imagen es requerida" else null)
    }

    fun onCategoriaSelected(categoria: Categoria) = _state.update {
        it.copy(
            categoriaId = categoria.categoriaId,
            categoriaNombre = categoria.nombre,
            categoriaError = null
        )
    }

    fun onStockChange(value: String) {
        val s = value.toIntOrNull()
        _state.update {
            it.copy(
                stock = s ?: 0,
                stockError = when {
                    value.isBlank() -> "Stock es requerido"
                    s == null -> "Ingrese un número válido"
                    s < 0 -> "El stock no puede ser negativo"
                    else -> null
                }
            )
        }
    }

    fun save() {
        val s = _state.value
        val nombreError = if (s.nombre.isBlank()) "Nombre es requerido" else null
        val descripcionError = if (s.descripcion.isBlank()) "Descripción es requerida" else null
        val precioError = if (s.precio <= 0) "El precio debe ser mayor a 0" else null
        val imagenError = if (s.imagen.isBlank()) "URL de imagen es requerida" else null
        val categoriaError = if (s.categoriaId.isBlank()) "Categoría es requerida" else null
        val stockError = if (s.stock < 0) "El stock no puede ser negativo" else null

        if (nombreError != null || descripcionError != null || precioError != null ||
            imagenError != null || categoriaError != null || stockError != null
        ) {
            _state.update {
                it.copy(
                    nombreError = nombreError,
                    descripcionError = descripcionError,
                    precioError = precioError,
                    imagenError = imagenError,
                    categoriaError = categoriaError,
                    stockError = stockError
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                saveProductoUseCase(
                    Producto(
                        productoId = s.productoId,
                        nombre = s.nombre,
                        descripcion = s.descripcion,
                        precio = s.precio,
                        imagen = s.imagen,
                        categoriaId = s.categoriaId,
                        disponible = s.disponible,
                        stock = s.stock
                    )
                )
                _state.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
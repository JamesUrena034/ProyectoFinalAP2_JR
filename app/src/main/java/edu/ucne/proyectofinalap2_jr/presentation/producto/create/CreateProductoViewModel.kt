package edu.ucne.proyectofinalap2_jr.presentation.producto.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.Producto
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.GetProductoByIdUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.SaveProductoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateProductoViewModel @Inject constructor(
    private val saveProductoUseCase: SaveProductoUseCase,
    private val getProductoByIdUseCase: GetProductoByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProductoUiState())
    val state: StateFlow<CreateProductoUiState> = _state.asStateFlow()

    fun load(id: String) {
        if (id.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val producto = getProductoByIdUseCase(id)
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
                        disponible = p.disponible
                    )
                }
            }
        }
    }

    fun onNombreChange(value: String) = _state.update {
        it.copy(nombre = value, nombreError = if (value.isBlank()) "Nombre es requerido" else null)
    }

    fun onDescripcionChange(value: String) = _state.update { it.copy(descripcion = value) }

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

    fun onCategoriaChange(value: String) = _state.update { it.copy(categoriaId = value) }

    fun onImagenUriChange(uri: Uri?) = _state.update { it.copy(imagenUri = uri) }

    fun save() {
        val s = _state.value
        val nombreError = if (s.nombre.isBlank()) "Nombre es requerido" else null
        val precioError = if (s.precio <= 0) "El precio debe ser mayor a 0" else null

        if (nombreError != null || precioError != null) {
            _state.update { it.copy(nombreError = nombreError, precioError = precioError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val imagenUrl = if (s.imagenUri != null) {
                    uploadImage(s.imagenUri)
                } else {
                    s.imagen
                }
                saveProductoUseCase(
                    Producto(
                        productoId = s.productoId,
                        nombre = s.nombre,
                        descripcion = s.descripcion,
                        precio = s.precio,
                        imagen = imagenUrl,
                        categoriaId = s.categoriaId,
                        disponible = s.disponible
                    )
                )
                _state.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("productos/${UUID.randomUUID()}")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
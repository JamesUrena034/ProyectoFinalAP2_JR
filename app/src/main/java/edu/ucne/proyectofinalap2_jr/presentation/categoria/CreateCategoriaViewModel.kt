package edu.ucne.proyectofinalap2_jr.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import edu.ucne.proyectofinalap2_jr.domain.usecase.categoria.SaveCategoriaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoriaViewModel @Inject constructor(
    private val saveCategoriaUseCase: SaveCategoriaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateCategoriaUiState())
    val state: StateFlow<CreateCategoriaUiState> = _state.asStateFlow()

    fun resetState() {
        _state.value = CreateCategoriaUiState()
    }

    fun onNombreChange(value: String) = _state.update {
        it.copy(nombre = value, nombreError = if (value.isBlank()) "Nombre es requerido" else null)
    }

    fun onDescripcionChange(value: String) = _state.update {
        it.copy(descripcion = value, descripcionError = if (value.isBlank()) "Descripción es requerida" else null)
    }

    fun onImagenChange(value: String) = _state.update {
        it.copy(imagen = value, imagenError = if (value.isBlank()) "URL de imagen es requerida" else null)
    }

    fun save() {
        val s = _state.value
        val nombreError = if (s.nombre.isBlank()) "Nombre es requerido" else null
        val descripcionError = if (s.descripcion.isBlank()) "Descripción es requerida" else null
        val imagenError = if (s.imagen.isBlank()) "URL de imagen es requerida" else null

        if (nombreError != null || descripcionError != null || imagenError != null) {
            _state.update {
                it.copy(
                    nombreError = nombreError,
                    descripcionError = descripcionError,
                    imagenError = imagenError
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                saveCategoriaUseCase(
                    Categoria(
                        categoriaId = s.categoriaId,
                        nombre = s.nombre,
                        descripcion = s.descripcion,
                        imagen = s.imagen
                    )
                )
                _state.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
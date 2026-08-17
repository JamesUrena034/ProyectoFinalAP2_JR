package edu.ucne.proyectofinalap2_jr.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import edu.ucne.proyectofinalap2_jr.domain.usecase.categoria.GetCategoriasUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.GetProductosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val getCategoriasUseCase: GetCategoriasUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val userData = user?.let { authRepository.getUserData(it.uid) }
            _state.update {
                it.copy(
                    userNombre = userData?.nombre ?: user?.displayName ?: "",
                    userRol = userData?.rol ?: "usuario"
                )
            }
            launch {
                getProductosUseCase().collect { productos ->
                    _state.update { it.copy(isLoading = false, productos = productos) }
                }
            }
            launch {
                getCategoriasUseCase().collect { categorias ->
                    _state.update { it.copy(categorias = categorias) }
                }
            }
        }
    }

    fun onBusquedaChange(value: String) = _state.update { it.copy(busqueda = value) }

    fun onCategoriaSelected(categoriaId: String) = _state.update {
        it.copy(categoriaSeleccionada = categoriaId)
    }
}
package edu.ucne.proyectofinalap2_jr.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.usecase.categoria.DeleteCategoriaUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.categoria.GetCategoriasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaListViewModel @Inject constructor(
    private val getCategoriasUseCase: GetCategoriasUseCase,
    private val deleteCategoriaUseCase: DeleteCategoriaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriaListUiState())
    val state: StateFlow<CategoriaListUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCategoriasUseCase().collect { categorias ->
                _state.update { it.copy(isLoading = false, categorias = categorias) }
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            deleteCategoriaUseCase(id)
        }
    }
}
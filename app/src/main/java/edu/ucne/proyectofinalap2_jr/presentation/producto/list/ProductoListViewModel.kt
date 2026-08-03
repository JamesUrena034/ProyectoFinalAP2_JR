package edu.ucne.proyectofinalap2_jr.presentation.producto.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.DeleteProductoUseCase
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.GetProductosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoListViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val deleteProductoUseCase: DeleteProductoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductoListUiState())
    val state: StateFlow<ProductoListUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getProductosUseCase().collect { productos ->
                _state.update { it.copy(isLoading = false, productos = productos) }
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            deleteProductoUseCase(id)
        }
    }
}
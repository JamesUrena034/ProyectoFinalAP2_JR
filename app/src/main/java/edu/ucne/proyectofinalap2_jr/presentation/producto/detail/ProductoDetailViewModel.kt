package edu.ucne.proyectofinalap2_jr.presentation.producto.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.usecase.producto.GetProductoByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoDetailViewModel @Inject constructor(
    private val getProductoByIdUseCase: GetProductoByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductoDetailUiState())
    val state: StateFlow<ProductoDetailUiState> = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val producto = getProductoByIdUseCase(id)
            _state.update { it.copy(isLoading = false, producto = producto) }
        }
    }
}
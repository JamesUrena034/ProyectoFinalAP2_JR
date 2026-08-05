package edu.ucne.proyectofinalap2_jr.presentation.producto.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosPorCategoriaViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductosPorCategoriaUiState())
    val state: StateFlow<ProductosPorCategoriaUiState> = _state.asStateFlow()

    fun load(categoriaId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            productoRepository.getProductosByCategoria(categoriaId).collect { productos ->
                _state.update { it.copy(isLoading = false, productos = productos) }
            }
        }
    }
}
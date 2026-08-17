package edu.ucne.proyectofinalap2_jr.presentation.home

import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import edu.ucne.proyectofinalap2_jr.domain.model.Producto

data class HomeUiState(
    val isLoading: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val error: String? = null,
    val userNombre: String = "",
    val userRol: String = "usuario",
    val busqueda: String = "",
    val categoriaSeleccionada: String = "Todas"
) {
    val productosFiltrados: List<Producto>
        get() {
            var lista = productos
            if (categoriaSeleccionada != "Todas") {
                lista = lista.filter { it.categoriaId == categoriaSeleccionada }
            }
            if (busqueda.isNotBlank()) {
                lista = lista.filter {
                    it.nombre.contains(busqueda, ignoreCase = true) ||
                            it.descripcion.contains(busqueda, ignoreCase = true)
                }
            }
            return lista
        }
}
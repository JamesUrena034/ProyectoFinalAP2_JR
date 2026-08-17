package edu.ucne.proyectofinalap2_jr.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable data object Login : Screen()
    @Serializable data object Home : Screen()
    @Serializable data object Categorias : Screen()
    @Serializable data class ProductoDetail(val productoId: String) : Screen()
    @Serializable data class ProductoCreate(val productoId: String = "") : Screen()
    @Serializable data object AdminProductos : Screen()
    @Serializable data object Carrito : Screen()
    @Serializable data object MisPedidos : Screen()
    @Serializable data class DetallePedido(val pedidoId: String) : Screen()
    @Serializable data object Perfil : Screen()
    @Serializable data object AdminPedidos : Screen()
    @Serializable data object CreateCategoria : Screen()
    @Serializable data class EditCategoria(val categoriaId: String) : Screen()
    @Serializable data class EditProducto(val productoId: String) : Screen()
    @Serializable data class ProductosPorCategoria(
        val categoriaId: String,
        val categoriaNombre: String
    ) : Screen()
    @Serializable data object Checkout : Screen()
}
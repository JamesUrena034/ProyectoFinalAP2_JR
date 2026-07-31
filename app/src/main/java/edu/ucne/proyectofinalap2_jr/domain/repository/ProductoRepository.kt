package edu.ucne.proyectofinalap2_jr.domain.repository

import edu.ucne.proyectofinalap2_jr.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    fun getProductos(): Flow<List<Producto>>
    fun getProductosByCategoria(categoriaId: String): Flow<List<Producto>>
    suspend fun getProductoById(id: String): Producto?
    suspend fun saveProducto(producto: Producto)
    suspend fun deleteProducto(id: String)
}
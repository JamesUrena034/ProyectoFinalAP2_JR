package edu.ucne.proyectofinalap2_jr.domain.repository

import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun getCategorias(): Flow<List<Categoria>>
    suspend fun getCategoriaById(id: String): Categoria?
    suspend fun saveCategoria(categoria: Categoria)
    suspend fun deleteCategoria(id: String)
}
package edu.ucne.proyectofinalap2_jr.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import edu.ucne.proyectofinalap2_jr.domain.repository.CategoriaRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoriaRepository {

    override fun getCategorias(): Flow<List<Categoria>> = callbackFlow {
        val listener = firestore.collection("categorias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categorias = snapshot?.documents?.mapNotNull {
                    it.toObject(Categoria::class.java)
                } ?: emptyList()
                trySend(categorias)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getCategoriaById(id: String): Categoria? {
        return try {
            val doc = firestore.collection("categorias").document(id).get().await()
            doc.toObject(Categoria::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveCategoria(categoria: Categoria) {
        try {
            val id = categoria.categoriaId.ifBlank {
                firestore.collection("categorias").document().id
            }
            val categoriaConId = categoria.copy(categoriaId = id)
            firestore.collection("categorias")
                .document(id)
                .set(categoriaConId)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteCategoria(id: String) {
        try {
            firestore.collection("categorias").document(id).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
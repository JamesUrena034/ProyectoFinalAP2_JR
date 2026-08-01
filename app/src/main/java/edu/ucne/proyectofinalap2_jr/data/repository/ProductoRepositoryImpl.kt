package edu.ucne.proyectofinalap2_jr.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.domain.model.Producto
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductoRepository {

    override fun getProductos(): Flow<List<Producto>> = callbackFlow {
        val listener = firestore.collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val productos = snapshot?.documents?.mapNotNull {
                    it.toObject(Producto::class.java)
                } ?: emptyList()
                trySend(productos)
            }
        awaitClose { listener.remove() }
    }

    override fun getProductosByCategoria(categoriaId: String): Flow<List<Producto>> = callbackFlow {
        val listener = firestore.collection("productos")
            .whereEqualTo("categoriaId", categoriaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val productos = snapshot?.documents?.mapNotNull {
                    it.toObject(Producto::class.java)
                } ?: emptyList()
                trySend(productos)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getProductoById(id: String): Producto? {
        return try {
            val doc = firestore.collection("productos").document(id).get().await()
            doc.toObject(Producto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveProducto(producto: Producto) {
        try {
            val id = producto.productoId.ifBlank {
                firestore.collection("productos").document().id
            }
            val productoConId = producto.copy(productoId = id)
            firestore.collection("productos")
                .document(id)
                .set(productoConId)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteProducto(id: String) {
        try {
            firestore.collection("productos").document(id).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
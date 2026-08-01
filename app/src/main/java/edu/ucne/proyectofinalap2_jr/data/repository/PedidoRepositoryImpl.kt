package edu.ucne.proyectofinalap2_jr.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PedidoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PedidoRepository {

    override fun getPedidos(): Flow<List<Pedido>> = callbackFlow {
        val listener = firestore.collection("pedidos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pedidos = snapshot?.documents?.mapNotNull {
                    it.toObject(Pedido::class.java)
                } ?: emptyList()
                trySend(pedidos)
            }
        awaitClose { listener.remove() }
    }

    override fun getPedidosByUsuario(usuarioId: String): Flow<List<Pedido>> = callbackFlow {
        val listener = firestore.collection("pedidos")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pedidos = snapshot?.documents?.mapNotNull {
                    it.toObject(Pedido::class.java)
                } ?: emptyList()
                trySend(pedidos)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getPedidoById(id: String): Pedido? {
        return try {
            val doc = firestore.collection("pedidos").document(id).get().await()
            doc.toObject(Pedido::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun savePedido(pedido: Pedido) {
        try {
            val id = pedido.pedidoId.ifBlank {
                firestore.collection("pedidos").document().id
            }
            val pedidoConId = pedido.copy(pedidoId = id)
            firestore.collection("pedidos")
                .document(id)
                .set(pedidoConId)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateEstadoPedido(id: String, estado: String) {
        try {
            firestore.collection("pedidos")
                .document(id)
                .update("estado", estado)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
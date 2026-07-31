package edu.ucne.proyectofinalap2_jr.domain.repository

import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    fun getPedidos(): Flow<List<Pedido>>
    fun getPedidosByUsuario(usuarioId: String): Flow<List<Pedido>>
    suspend fun getPedidoById(id: String): Pedido?
    suspend fun savePedido(pedido: Pedido)
    suspend fun updateEstadoPedido(id: String, estado: String)
}
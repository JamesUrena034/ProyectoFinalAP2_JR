package edu.ucne.proyectofinalap2_jr.domain.usecase.pedido

import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
import javax.inject.Inject

class SavePedidoUseCase @Inject constructor(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(pedido: Pedido) = repository.savePedido(pedido)
}
package edu.ucne.proyectofinalap2_jr.domain.usecase.pedido

import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
import javax.inject.Inject

class UpdateEstadoPedidoUseCase @Inject constructor(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: String, estado: String) =
        repository.updateEstadoPedido(id, estado)
}
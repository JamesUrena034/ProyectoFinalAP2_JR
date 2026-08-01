package edu.ucne.proyectofinalap2_jr.domain.usecase.pedido

import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
import javax.inject.Inject

class GetPedidosUseCase @Inject constructor(
    private val repository: PedidoRepository
) {
    operator fun invoke() = repository.getPedidos()
}
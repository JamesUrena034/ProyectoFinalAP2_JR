package edu.ucne.proyectofinalap2_jr.domain.usecase.producto

import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import javax.inject.Inject

class GetProductosUseCase @Inject constructor(
    private val repository: ProductoRepository
) {
    operator fun invoke() = repository.getProductos()
}
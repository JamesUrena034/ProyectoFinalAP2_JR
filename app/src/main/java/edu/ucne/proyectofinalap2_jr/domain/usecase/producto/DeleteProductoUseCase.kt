package edu.ucne.proyectofinalap2_jr.domain.usecase.producto

import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import javax.inject.Inject

class DeleteProductoUseCase @Inject constructor(
    private val repository: ProductoRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteProducto(id)
}
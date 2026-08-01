package edu.ucne.proyectofinalap2_jr.domain.usecase.producto

import edu.ucne.proyectofinalap2_jr.domain.model.Producto
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import javax.inject.Inject

class SaveProductoUseCase @Inject constructor(
    private val repository: ProductoRepository
) {
    suspend operator fun invoke(producto: Producto) = repository.saveProducto(producto)
}
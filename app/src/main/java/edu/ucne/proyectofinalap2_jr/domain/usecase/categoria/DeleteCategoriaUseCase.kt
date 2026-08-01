package edu.ucne.proyectofinalap2_jr.domain.usecase.categoria

import edu.ucne.proyectofinalap2_jr.domain.repository.CategoriaRepository
import javax.inject.Inject

class DeleteCategoriaUseCase @Inject constructor(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteCategoria(id)
}
package edu.ucne.proyectofinalap2_jr.domain.usecase.categoria

import edu.ucne.proyectofinalap2_jr.domain.repository.CategoriaRepository
import javax.inject.Inject

class GetCategoriasUseCase @Inject constructor(
    private val repository: CategoriaRepository
) {
    operator fun invoke() = repository.getCategorias()
}
package edu.ucne.proyectofinalap2_jr.domain.usecase.auth

import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.getCurrentUser()
}
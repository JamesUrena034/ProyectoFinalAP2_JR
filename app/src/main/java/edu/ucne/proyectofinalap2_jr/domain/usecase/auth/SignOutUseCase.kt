package edu.ucne.proyectofinalap2_jr.domain.usecase.auth

import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}
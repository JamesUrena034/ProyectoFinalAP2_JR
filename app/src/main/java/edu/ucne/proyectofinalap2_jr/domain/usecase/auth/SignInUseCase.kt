package edu.ucne.proyectofinalap2_jr.domain.usecase.auth

import android.content.Context
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(context: Context) = repository.signInWithGoogle(context)
}
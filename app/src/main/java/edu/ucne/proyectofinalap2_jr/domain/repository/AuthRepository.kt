package edu.ucne.proyectofinalap2_jr.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import edu.ucne.proyectofinalap2_jr.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser>
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
    suspend fun getUserData(uid: String): Usuario?
    suspend fun saveUserData(usuario: Usuario)
}
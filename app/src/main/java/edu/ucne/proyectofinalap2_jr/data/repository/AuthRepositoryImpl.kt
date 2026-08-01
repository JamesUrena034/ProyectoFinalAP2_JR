package edu.ucne.proyectofinalap2_jr.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.domain.model.Usuario
import edu.ucne.proyectofinalap2_jr.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val credentialManager: CredentialManager
) : AuthRepository {

    override suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("803197363275-mcdte695696fqkd2uaa24blt43oq49vc.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseAuthCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult =
                    auth.signInWithCredential(firebaseAuthCredential).await()
                val user = authResult.user!!

                val usuario = Usuario(
                    uid = user.uid,
                    nombre = user.displayName ?: "",
                    email = user.email ?: "",
                    foto = user.photoUrl?.toString() ?: "",
                    rol = "usuario"
                )
                saveUserData(usuario)
                Result.success(user)
            } else {
                Result.failure(Exception("Credencial cancelada o no válida"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun getUserData(uid: String): Usuario? {
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            doc.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveUserData(usuario: Usuario) {
        try {
            val doc = firestore.collection("usuarios").document(usuario.uid).get().await()
            if (!doc.exists()) {
                firestore.collection("usuarios")
                    .document(usuario.uid)
                    .set(usuario)
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
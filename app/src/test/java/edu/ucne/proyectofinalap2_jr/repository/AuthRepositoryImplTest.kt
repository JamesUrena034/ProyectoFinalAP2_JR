package edu.ucne.proyectofinalap2_jr

import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.data.repository.AuthRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

@ExperimentalCoroutinesApi
class AuthRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var credentialManager: CredentialManager
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        auth = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        credentialManager = mockk(relaxed = true)
        repository = AuthRepositoryImpl(auth, firestore, credentialManager)
    }

    @Test
    fun `getCurrentUser retorna null cuando no hay sesion`() = runTest {
        every { auth.currentUser } returns null

        val result = repository.getCurrentUser()

        assertNull(result)
    }

    @Test
    fun `getCurrentUser retorna usuario cuando hay sesion activa`() = runTest {
        val firebaseUser = mockk<FirebaseUser>(relaxed = true)
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "user123"
        every { firebaseUser.email } returns "test@gmail.com"

        val result = repository.getCurrentUser()

        assertEquals(firebaseUser, result)
        assertEquals("user123", result?.uid)
        assertEquals("test@gmail.com", result?.email)
    }

    @Test
    fun `signOut llama signOut en FirebaseAuth`() = runTest {
        every { auth.signOut() } returns Unit

        repository.signOut()

        io.mockk.verify { auth.signOut() }
    }

    @Test
    fun `getUserData retorna null cuando documento no existe`() = runTest {
        val collectionRef = mockk<com.google.firebase.firestore.CollectionReference>(relaxed = true)
        val documentRef = mockk<com.google.firebase.firestore.DocumentReference>(relaxed = true)
        val documentSnapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>(relaxed = true)

        every { firestore.collection("usuarios") } returns collectionRef
        every { collectionRef.document("user999") } returns documentRef
        every { documentSnapshot.toObject(edu.ucne.proyectofinalap2_jr.domain.model.Usuario::class.java) } returns null
        every { documentRef.get() } returns com.google.android.gms.tasks.Tasks.forResult(documentSnapshot)

        val result = repository.getUserData("user999")

        assertNull(result)
    }
}
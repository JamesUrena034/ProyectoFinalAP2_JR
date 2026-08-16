package edu.ucne.proyectofinalap2_jr

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.data.repository.CategoriaRepositoryImpl
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

@ExperimentalCoroutinesApi
class CategoriaRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: CategoriaRepositoryImpl
    private lateinit var collectionRef: CollectionReference
    private lateinit var documentRef: DocumentReference

    @Before
    fun setup() {
        firestore = mockk(relaxed = true)
        collectionRef = mockk(relaxed = true)
        documentRef = mockk(relaxed = true)
        every { firestore.collection("categorias") } returns collectionRef
        every { collectionRef.document(any()) } returns documentRef
        every { collectionRef.document() } returns documentRef
        every { documentRef.id } returns "cat123"
        repository = CategoriaRepositoryImpl(firestore)
    }

    @Test
    fun `saveCategoria genera id cuando categoriaId esta vacio`() = runTest {
        val categoria = Categoria(
            categoriaId = "",
            nombre = "Mesas",
            descripcion = "Todo tipo de mesas para eventos",
            imagen = "https://example.com/mesas.jpg"
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.saveCategoria(categoria)

        verify { collectionRef.document(any()) }
    }

    @Test
    fun `saveCategoria usa id existente cuando categoriaId no esta vacio`() = runTest {
        val categoria = Categoria(
            categoriaId = "cat123",
            nombre = "Sillas",
            descripcion = "Todo tipo de sillas para eventos",
            imagen = "https://example.com/sillas.jpg"
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.saveCategoria(categoria)

        verify { collectionRef.document("cat123") }
    }

    @Test
    fun `deleteCategoria llama delete con id correcto`() = runTest {
        every { documentRef.delete() } returns Tasks.forResult(null)

        repository.deleteCategoria("cat123")

        verify { collectionRef.document("cat123") }
        verify { documentRef.delete() }
    }

    @Test
    fun `getCategoriaById retorna null cuando no existe`() = runTest {
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns false
        every { documentSnapshot.toObject(Categoria::class.java) } returns null
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getCategoriaById("cat999")

        assertEquals(null, result)
    }

    @Test
    fun `getCategoriaById retorna categoria cuando existe`() = runTest {
        val categoria = Categoria(
            categoriaId = "cat123",
            nombre = "Iluminación",
            descripcion = "Todo tipo de iluminación para eventos",
            imagen = "https://example.com/iluminacion.jpg"
        )
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Categoria::class.java) } returns categoria
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getCategoriaById("cat123")

        assertNotNull(result)
        assertEquals("Iluminación", result?.nombre)
        assertEquals("Todo tipo de iluminación para eventos", result?.descripcion)
    }
}
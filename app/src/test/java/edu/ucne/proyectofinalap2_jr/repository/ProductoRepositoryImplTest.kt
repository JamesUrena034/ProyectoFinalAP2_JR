package edu.ucne.proyectofinalap2_jr

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.data.repository.ProductoRepositoryImpl
import edu.ucne.proyectofinalap2_jr.domain.model.Producto
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
class ProductoRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: ProductoRepositoryImpl
    private lateinit var collectionRef: CollectionReference
    private lateinit var documentRef: DocumentReference

    @Before
    fun setup() {
        firestore = mockk(relaxed = true)
        collectionRef = mockk(relaxed = true)
        documentRef = mockk(relaxed = true)
        every { firestore.collection("productos") } returns collectionRef
        every { collectionRef.document(any()) } returns documentRef
        every { collectionRef.document() } returns documentRef
        every { documentRef.id } returns "prod123"
        repository = ProductoRepositoryImpl(firestore)
    }

    @Test
    fun `saveProducto genera id cuando productoId esta vacio`() = runTest {
        val producto = Producto(
            productoId = "",
            nombre = "Mesa Redonda",
            descripcion = "Mesa redonda para 8 personas",
            precio = 500.0,
            imagen = "https://example.com/mesa.jpg",
            categoriaId = "cat1",
            disponible = true,
            stock = 10
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.saveProducto(producto)

        verify { collectionRef.document(any()) }
    }

    @Test
    fun `saveProducto usa id existente cuando productoId no esta vacio`() = runTest {
        val producto = Producto(
            productoId = "prod123",
            nombre = "Silla Napoleon",
            descripcion = "Silla transparente",
            precio = 150.0,
            imagen = "https://example.com/silla.jpg",
            categoriaId = "cat2",
            disponible = true,
            stock = 20
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.saveProducto(producto)

        verify { collectionRef.document("prod123") }
    }

    @Test
    fun `deleteProducto llama delete con id correcto`() = runTest {
        every { documentRef.delete() } returns Tasks.forResult(null)

        repository.deleteProducto("prod123")

        verify { collectionRef.document("prod123") }
        verify { documentRef.delete() }
    }

    @Test
    fun `getProductoById retorna null cuando documento no existe`() = runTest {
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns false
        every { documentSnapshot.toObject(Producto::class.java) } returns null
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getProductoById("prod999")

        assertEquals(null, result)
    }

    @Test
    fun `getProductoById retorna producto cuando existe`() = runTest {
        val producto = Producto(
            productoId = "prod123",
            nombre = "Mesa Cuadrada",
            descripcion = "Mesa cuadrada para 4 personas",
            precio = 300.0,
            imagen = "https://example.com/mesa.jpg",
            categoriaId = "cat1",
            disponible = true,
            stock = 5
        )
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Producto::class.java) } returns producto
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getProductoById("prod123")

        assertNotNull(result)
        assertEquals("Mesa Cuadrada", result?.nombre)
        assertEquals(300.0, result?.precio)
        assertEquals(5, result?.stock)
    }
}
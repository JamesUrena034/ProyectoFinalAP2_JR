package edu.ucne.proyectofinalap2_jr

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.proyectofinalap2_jr.data.repository.PedidoRepositoryImpl
import edu.ucne.proyectofinalap2_jr.domain.model.ItemPedido
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido
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
class PedidoRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: PedidoRepositoryImpl
    private lateinit var collectionRef: CollectionReference
    private lateinit var documentRef: DocumentReference

    @Before
    fun setup() {
        firestore = mockk(relaxed = true)
        collectionRef = mockk(relaxed = true)
        documentRef = mockk(relaxed = true)
        every { firestore.collection("pedidos") } returns collectionRef
        every { collectionRef.document(any()) } returns documentRef
        every { collectionRef.document() } returns documentRef
        every { documentRef.id } returns "ped123"
        repository = PedidoRepositoryImpl(firestore)
    }

    @Test
    fun `savePedido genera id cuando pedidoId esta vacio`() = runTest {
        val pedido = Pedido(
            pedidoId = "",
            usuarioId = "user123",
            productos = listOf(
                ItemPedido(
                    productoId = "prod1",
                    nombre = "Mesa Redonda",
                    precio = 500.0,
                    cantidad = 2,
                    imagen = "https://example.com/mesa.jpg"
                )
            ),
            total = 1000.0,
            estado = "pendiente",
            fecha = "01/06/2026 10:00",
            fechaInicio = "01/06/2026",
            fechaFin = "03/06/2026"
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.savePedido(pedido)

        verify { collectionRef.document(any()) }
    }

    @Test
    fun `savePedido usa id existente cuando pedidoId no esta vacio`() = runTest {
        val pedido = Pedido(
            pedidoId = "ped123",
            usuarioId = "user123",
            productos = emptyList(),
            total = 500.0,
            estado = "pendiente",
            fecha = "01/06/2026 10:00",
            fechaInicio = "01/06/2026",
            fechaFin = "02/06/2026"
        )
        every { documentRef.set(any()) } returns Tasks.forResult(null)

        repository.savePedido(pedido)

        verify { collectionRef.document("ped123") }
    }

    @Test
    fun `updateEstadoPedido actualiza estado correctamente`() = runTest {
        every { documentRef.update("estado", any()) } returns Tasks.forResult(null)

        repository.updateEstadoPedido("ped123", "completado")

        verify { collectionRef.document("ped123") }
        verify { documentRef.update("estado", "completado") }
    }

    @Test
    fun `getPedidoById retorna null cuando no existe`() = runTest {
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns false
        every { documentSnapshot.toObject(Pedido::class.java) } returns null
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getPedidoById("ped999")

        assertEquals(null, result)
    }

    @Test
    fun `getPedidoById retorna pedido cuando existe`() = runTest {
        val pedido = Pedido(
            pedidoId = "ped123",
            usuarioId = "user123",
            productos = listOf(
                ItemPedido(
                    productoId = "prod1",
                    nombre = "Silla Napoleon",
                    precio = 150.0,
                    cantidad = 10,
                    imagen = "https://example.com/silla.jpg"
                )
            ),
            total = 1500.0,
            estado = "pendiente",
            fecha = "01/06/2026 10:00",
            fechaInicio = "05/06/2026",
            fechaFin = "07/06/2026"
        )
        val documentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Pedido::class.java) } returns pedido
        every { documentRef.get() } returns Tasks.forResult(documentSnapshot)

        val result = repository.getPedidoById("ped123")

        assertNotNull(result)
        assertEquals("user123", result?.usuarioId)
        assertEquals(1500.0, result?.total)
        assertEquals("pendiente", result?.estado)
        assertEquals("05/06/2026", result?.fechaInicio)
    }
}
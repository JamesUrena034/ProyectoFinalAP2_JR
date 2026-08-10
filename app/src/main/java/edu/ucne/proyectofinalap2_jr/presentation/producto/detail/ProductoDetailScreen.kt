package edu.ucne.proyectofinalap2_jr.presentation.producto.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem
import edu.ucne.proyectofinalap2_jr.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetailScreen(
    productoId: String,
    onBack: () -> Unit,
    onAgregarAlCarrito: (CarritoItem) -> Unit,
    viewModel: ProductoDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productoId) {
        viewModel.load(productoId)
    }

    ProductoDetailBodyScreen(
        state = state,
        onBack = onBack,
        onAgregarAlCarrito = onAgregarAlCarrito
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetailBodyScreen(
    state: ProductoDetailUiState,
    onBack: () -> Unit,
    onAgregarAlCarrito: (CarritoItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.producto?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                state.producto != null -> {
                    val producto = state.producto
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = producto.imagen,
                            contentDescription = producto.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "$${String.format("%,.2f", producto.precio)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (producto.stock > 0)
                                    "Stock disponible: ${producto.stock}"
                                else
                                    "Sin stock disponible",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (producto.stock > 0) Color.Green else Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                producto.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    onAgregarAlCarrito(
                                        CarritoItem(
                                            productoId = producto.productoId,
                                            nombre = producto.nombre,
                                            precio = producto.precio,
                                            cantidad = 1,
                                            imagen = producto.imagen
                                        )
                                    )
                                },
                                enabled = producto.stock > 0,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (producto.stock > 0)
                                        "Agregar al Carrito"
                                    else
                                        "Sin stock disponible"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoDetailBodyScreenPreview() {
    MaterialTheme {
        ProductoDetailBodyScreen(
            state = ProductoDetailUiState(
                producto = Producto(
                    productoId = "1",
                    nombre = "Silla Napoleon",
                    descripcion = "Silla transparente para eventos",
                    precio = 150.0,
                    imagen = "",
                    stock = 10
                )
            ),
            onBack = {},
            onAgregarAlCarrito = {}
        )
    }
}
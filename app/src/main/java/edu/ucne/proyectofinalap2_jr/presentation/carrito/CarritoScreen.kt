package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.proyectofinalap2_jr.domain.model.CarritoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel = hiltViewModel(),
    onPedidoExitoso: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.pedidoExitoso) {
        if (state.pedidoExitoso) onPedidoExitoso()
    }

    CarritoBodyScreen(
        state = state,
        onEliminarItem = viewModel::eliminarItem,
        onCambiarCantidad = viewModel::cambiarCantidad,
        onFechaInicioChange = viewModel::onFechaInicioChange,
        onFechaFinChange = viewModel::onFechaFinChange,
        onRealizarPedido = viewModel::realizarPedido
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoBodyScreen(
    state: CarritoUiState,
    onEliminarItem: (String) -> Unit,
    onCambiarCantidad: (String, Int) -> Unit,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    onRealizarPedido: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Carrito") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.items.isEmpty()) {
                Text(
                    "Tu carrito está vacío",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items) { item ->
                            CarritoItemCard(
                                item = item,
                                onEliminar = { onEliminarItem(item.productoId) },
                                onCambiarCantidad = { cantidad ->
                                    onCambiarCantidad(item.productoId, cantidad)
                                }
                            )
                        }
                    }

                    HorizontalDivider()

                    Column(modifier = Modifier.padding(16.dp)) {

                        OutlinedTextField(
                            value = state.fechaInicio,
                            onValueChange = onFechaInicioChange,
                            label = { Text("Fecha de inicio (dd/MM/yyyy)") },
                            isError = state.fechaInicioError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        state.fechaInicioError?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.fechaFin,
                            onValueChange = onFechaFinChange,
                            label = { Text("Fecha de fin (dd/MM/yyyy)") },
                            isError = state.fechaFinError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        state.fechaFinError?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", fontWeight = FontWeight.Bold)
                            Text(
                                "$${String.format("%,.2f", state.total)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (state.error != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                state.error,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = onRealizarPedido,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Realizar Pedido")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarritoItemCard(
    item: CarritoItem,
    onEliminar: () -> Unit,
    onCambiarCantidad: (Int) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, fontWeight = FontWeight.Bold)
                Text(
                    "$${String.format("%,.2f", item.precio * item.cantidad)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onCambiarCantidad(item.cantidad - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar")
                }
                Text(
                    "${item.cantidad}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onCambiarCantidad(item.cantidad + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun CarritoBodyScreenPreview() {
    MaterialTheme {
        CarritoBodyScreen(
            state = CarritoUiState(),
            onEliminarItem = {},
            onFechaInicioChange = {},
            onFechaFinChange = {},
            onRealizarPedido = {},
            onCambiarCantidad = { _, _ -> }
        )
    }
}
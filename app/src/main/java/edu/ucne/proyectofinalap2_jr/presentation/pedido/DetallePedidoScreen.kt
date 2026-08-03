package edu.ucne.proyectofinalap2_jr.presentation.pedido

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    pedidoId: String,
    onBack: () -> Unit,
    viewModel: DetallePedidoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pedidoId) {
        viewModel.load(pedidoId)
    }

    DetallePedidoBodyScreen(
        state = state,
        onBack = onBack,
        onUpdateEstado = { estado ->
            viewModel.updateEstado(pedidoId, estado)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoBodyScreen(
    state: DetallePedidoUiState,
    onBack: () -> Unit,
    onUpdateEstado: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido") },
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
                state.pedido != null -> {
                    val pedido = state.pedido
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Pedido #${pedido.pedidoId.take(8)}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("Fecha: ${pedido.fecha}")
                                    Text(
                                        "Estado: ${pedido.estado.uppercase()}",
                                        color = when (pedido.estado) {
                                            "pendiente" -> MaterialTheme.colorScheme.primary
                                            "completado" -> Color.Green
                                            "cancelado" -> Color.Red
                                            else -> Color.Gray
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Total: $${String.format("%,.2f", pedido.total)}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                "Productos",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        items(pedido.productos) { item ->
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(item.nombre, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Cantidad: ${item.cantidad}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(
                                        "$${String.format("%,.2f", item.precio * item.cantidad)}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateEstado("cancelado") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.Red
                                    )
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = { onUpdateEstado("completado") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Completado")
                                }
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
fun DetallePedidoBodyScreenPreview() {
    MaterialTheme {
        DetallePedidoBodyScreen(
            state = DetallePedidoUiState(),
            onBack = {},
            onUpdateEstado = {}
        )
    }
}
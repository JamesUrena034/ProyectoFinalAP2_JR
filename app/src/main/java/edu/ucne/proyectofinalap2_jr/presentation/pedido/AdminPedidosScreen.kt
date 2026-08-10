package edu.ucne.proyectofinalap2_jr.presentation.pedido

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import edu.ucne.proyectofinalap2_jr.domain.model.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    viewModel: AdminPedidosViewModel = hiltViewModel(),
    onPedidoClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AdminPedidosBodyScreen(
        state = state,
        onPedidoClick = onPedidoClick,
        onUpdateEstado = viewModel::updateEstado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosBodyScreen(
    state: AdminPedidosUiState,
    onPedidoClick: (String) -> Unit,
    onUpdateEstado: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Todos los Pedidos") })
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
                state.error != null -> Text(
                    "Error: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                state.pedidos.isEmpty() -> Text(
                    "No hay pedidos aún",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.pedidos) { pedido ->
                            AdminPedidoCard(
                                pedido = pedido,
                                onClick = { onPedidoClick(pedido.pedidoId) },
                                onUpdateEstado = onUpdateEstado
                            )
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total pedidos: ${state.pedidos.size}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Total: $${String.format("%,.2f", state.pedidos.sumOf { it.total })}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPedidoCard(
    pedido: Pedido,
    onClick: () -> Unit,
    onUpdateEstado: (String, String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "#${pedido.pedidoId.take(8)}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    pedido.estado.uppercase(),
                    color = when (pedido.estado) {
                        "pendiente" -> MaterialTheme.colorScheme.primary
                        "completado" -> Color.Green
                        "cancelado" -> Color.Red
                        else -> Color.Gray
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Usuario: ${pedido.usuarioId.take(12)}...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                "Creado: ${pedido.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            if (pedido.fechaInicio.isNotBlank()) {
                Text(
                    "Desde: ${pedido.fechaInicio} — Hasta: ${pedido.fechaFin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                "Productos: ${pedido.productos.size}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Total: $${String.format("%,.2f", pedido.total)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (pedido.estado == "pendiente") {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onUpdateEstado(pedido.pedidoId, "cancelado") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onUpdateEstado(pedido.pedidoId, "completado") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Completar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPedidosBodyScreenPreview() {
    MaterialTheme {
        AdminPedidosBodyScreen(
            state = AdminPedidosUiState(),
            onPedidoClick = {},
            onUpdateEstado = { _, _ -> }
        )
    }
}
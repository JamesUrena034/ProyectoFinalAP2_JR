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
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel = hiltViewModel(),
    onPedidoClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MisPedidosBodyScreen(
        state = state,
        onPedidoClick = onPedidoClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosBodyScreen(
    state: MisPedidosUiState,
    onPedidoClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mis Pedidos") })
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
                    "No tienes pedidos aún",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.pedidos) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            onClick = { onPedidoClick(pedido.pedidoId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onClick: () -> Unit
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
                "Fecha: ${pedido.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                "Productos: ${pedido.productos.size}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Total: $${String.format("%,.2f", pedido.total)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisPedidosBodyScreenPreview() {
    MaterialTheme {
        MisPedidosBodyScreen(
            state = MisPedidosUiState(),
            onPedidoClick = {}
        )
    }
}
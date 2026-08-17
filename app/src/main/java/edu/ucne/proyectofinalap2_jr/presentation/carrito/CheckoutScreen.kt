package edu.ucne.proyectofinalap2_jr.presentation.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    carritoState: CarritoUiState,
    onBack: () -> Unit,
    onPedidoExitoso: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init(carritoState)
    }

    LaunchedEffect(state.pedidoExitoso) {
        if (state.pedidoExitoso) onPedidoExitoso()
    }

    CheckoutBodyScreen(
        state = state,
        onBack = onBack,
        onMetodoPagoChange = viewModel::onMetodoPagoChange,
        onNombreTarjetaChange = viewModel::onNombreTarjetaChange,
        onNumeroTarjetaChange = viewModel::onNumeroTarjetaChange,
        onFechaExpiracionChange = viewModel::onFechaExpiracionChange,
        onCvvChange = viewModel::onCvvChange,
        onConfirmarPago = viewModel::confirmarPago
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBodyScreen(
    state: CheckoutUiState,
    onBack: () -> Unit,
    onMetodoPagoChange: (String) -> Unit,
    onNombreTarjetaChange: (String) -> Unit,
    onNumeroTarjetaChange: (String) -> Unit,
    onFechaExpiracionChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onConfirmarPago: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.metodoPago == "Tarjeta de Crédito/Débito")
                            "Pago con Tarjeta"
                        else "Checkout"
                    )
                },
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
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Resumen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            state.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.nombre} x${item.cantidad}")
                                    Text("$${String.format("%,.2f", item.precio * item.cantidad)}")
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
                            if (state.fechaInicio.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Desde: ${state.fechaInicio} — Hasta: ${state.fechaFin}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Text(
                        "Método de Pago",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = state.metodoPago == "Tarjeta de Crédito/Débito",
                            onClick = { onMetodoPagoChange("Tarjeta de Crédito/Débito") }
                        )
                        Text("Tarjeta de Crédito/Débito")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = state.metodoPago == "Efectivo",
                            onClick = { onMetodoPagoChange("Efectivo") }
                        )
                        Text("Efectivo")
                    }

                    if (state.metodoPago == "Tarjeta de Crédito/Débito") {
                        OutlinedTextField(
                            value = state.nombreTarjeta,
                            onValueChange = onNombreTarjetaChange,
                            label = { Text("Nombre en la tarjeta") },
                            isError = state.nombreTarjetaError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        state.nombreTarjetaError?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        OutlinedTextField(
                            value = state.numeroTarjeta,
                            onValueChange = { if (it.length <= 16) onNumeroTarjetaChange(it) },
                            label = { Text("Número de Tarjeta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Icon(Icons.Default.CreditCard, contentDescription = null)
                            },
                            isError = state.numeroTarjetaError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        state.numeroTarjetaError?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = state.fechaExpiracion,
                                onValueChange = onFechaExpiracionChange,
                                label = { Text("MMAA") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = state.fechaExpiracionError != null,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = state.cvv,
                                onValueChange = { if (it.length <= 3) onCvvChange(it) },
                                label = { Text("CVV") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                isError = state.cvvError != null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (state.error != null) {
                        Text(
                            state.error,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onConfirmarPago,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar Pago — $${String.format("%,.2f", state.total)}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutBodyScreenPreview() {
    MaterialTheme {
        CheckoutBodyScreen(
            state = CheckoutUiState(),
            onBack = {},
            onMetodoPagoChange = {},
            onNombreTarjetaChange = {},
            onNumeroTarjetaChange = {},
            onFechaExpiracionChange = {},
            onCvvChange = {},
            onConfirmarPago = {}
        )
    }
}
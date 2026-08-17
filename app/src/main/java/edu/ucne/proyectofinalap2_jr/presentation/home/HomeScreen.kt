package edu.ucne.proyectofinalap2_jr.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import edu.ucne.proyectofinalap2_jr.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductoClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeBodyScreen(
        state = state,
        onProductoClick = onProductoClick,
        onBusquedaChange = viewModel::onBusquedaChange,
        onCategoriaSelected = viewModel::onCategoriaSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBodyScreen(
    state: HomeUiState,
    onProductoClick: (String) -> Unit,
    onBusquedaChange: (String) -> Unit,
    onCategoriaSelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AlquilaFest") }
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
                state.error != null -> Text(
                    "Error: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Buscador
                        OutlinedTextField(
                            value = state.busqueda,
                            onValueChange = onBusquedaChange,
                            placeholder = { Text("Buscar por ubicación...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            singleLine = true,
                            shape = MaterialTheme.shapes.extraLarge
                        )

                        // Filtros por categoría
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = state.categoriaSeleccionada == "Todas",
                                onClick = { onCategoriaSelected("Todas") },
                                label = { Text("Todas") }
                            )
                            state.categorias.forEach { categoria ->
                                FilterChip(
                                    selected = state.categoriaSeleccionada == categoria.categoriaId,
                                    onClick = { onCategoriaSelected(categoria.categoriaId) },
                                    label = { Text(categoria.nombre) }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (state.productosFiltrados.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay productos disponibles", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.productosFiltrados) { producto ->
                                    ProductoCard(
                                        producto = producto,
                                        onClick = { onProductoClick(producto.productoId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "$${String.format("%,.2f", producto.precio)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (producto.stock > 0) "Stock: ${producto.stock}" else "Sin stock",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (producto.stock > 0) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyScreenPreview() {
    MaterialTheme {
        HomeBodyScreen(
            state = HomeUiState(),
            onProductoClick = {},
            onBusquedaChange = {},
            onCategoriaSelected = {}
        )
    }
}